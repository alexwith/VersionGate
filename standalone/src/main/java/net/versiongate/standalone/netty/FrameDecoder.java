package net.versiongate.standalone.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor;
import java.util.List;
import net.versiongate.standalone.netty.FrameDecoder.VarintByteDecoder.DecodeResult;

public class FrameDecoder extends ByteToMessageDecoder {
    private static final Exception BAD_LENGTH_CACHED = new RuntimeException("Bad packet length");
    private static final Exception VARINT_BIG_CACHED = new RuntimeException("VarInt too big");

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf packet, List<Object> out) throws Exception {
        if (!context.channel().isActive()) {
            packet.clear();
            return;
        }

        final byte[] data = new byte[packet.readableBytes()];
        packet.getBytes(packet.readerIndex(), data);

        final VarintByteDecoder reader = new VarintByteDecoder();

        final int varintEnd = packet.forEachByte(reader);
        if (varintEnd == -1) {
            // We tried to go beyond the end of the buffer. This is probably a good sign that the
            // buffer was too short to hold a proper varint.
            if (reader.getResult() == DecodeResult.RUN_OF_ZEROES) {
                // Special case where the entire packet is just a run of zeroes. We ignore them all.
                packet.clear();
            }
            return;
        }

        if (reader.getResult() == DecodeResult.RUN_OF_ZEROES) {
            // this will return to the point where the next varint starts
            packet.readerIndex(varintEnd);
        } else if (reader.getResult() == DecodeResult.SUCCESS) {
            final int readVarint = reader.getReadVarint();
            final int bytesRead = reader.getBytesRead();
            if (readVarint < 0) {
                packet.clear();
                throw BAD_LENGTH_CACHED;
            } else if (readVarint == 0) {
                // skip over the empty packet(s) and ignore it
                packet.readerIndex(varintEnd + 1);
            } else {
                final int minimumRead = bytesRead + readVarint;
                if (packet.isReadable(minimumRead)) {
                    out.add(packet.retainedSlice(varintEnd + 1, readVarint));
                    packet.skipBytes(minimumRead);
                }
            }
        } else if (reader.getResult() == DecodeResult.TOO_BIG) {
            packet.clear();
            throw VARINT_BIG_CACHED;
        }
    }

    public static class VarintByteDecoder implements ByteProcessor {
        private int readVarint;
        private int bytesRead;
        private DecodeResult result = DecodeResult.TOO_SHORT;

        @Override
        public boolean process(byte k) {
            if (k == 0 && this.bytesRead == 0) {
                // tentatively say it's invalid, but there's a possibility of redemption
                this.result = DecodeResult.RUN_OF_ZEROES;
                return true;
            }
            if (this.result == DecodeResult.RUN_OF_ZEROES) {
                return false;
            }
            this.readVarint |= (k & 0x7F) << this.bytesRead++ * 7;
            if (this.bytesRead > 3) {
                this.result = DecodeResult.TOO_BIG;
                return false;
            }
            if ((k & 0x80) != 128) {
                this.result = DecodeResult.SUCCESS;
                return false;
            }
            return true;
        }

        public int getReadVarint() {
            return this.readVarint;
        }

        public int getBytesRead() {
            return this.bytesRead;
        }

        public DecodeResult getResult() {
            return this.result;
        }

        public enum DecodeResult {
            SUCCESS,
            TOO_SHORT,
            TOO_BIG,
            RUN_OF_ZEROES
        }
    }
}
