package net.versiongate.standalone.connection;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.versiongate.standalone.util.ProtocolUtils;
import net.versiongate.standalone.worker.WorkerContext;

public interface ProtocolFormat {

    ProtocolFormat VANILLA = new ProtocolFormat() {

        @Override
        public boolean read(ConnectionContext context, ByteBuffer buffer, ByteBuffer payloadOut, WorkerContext workerContext) {
            if (!context.isCompression()) {
                return true;
            }

            final int dataLength = ProtocolUtils.readVarInt(buffer);
            if (dataLength == 0) {
                return true;
            }
            try {
                this.decompress(workerContext.getInflater(), buffer, payloadOut);
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void write(ConnectionContext context, ByteBuffer payload, ByteBuffer out, WorkerContext workerContext) {
            if (!context.isCompression()) {
                ProtocolUtils.writeVarInt(out, payload.remaining());
                out.put(payload);
                return;
            }

            final int decompressedSize = payload.remaining();
            final int lengthIndex = ProtocolUtils.writeEmptyVarIntHeader(out);
            final int contentStart = out.position();
            if (decompressedSize >= context.getCompressionThreshold()) {
                ProtocolUtils.writeVarInt(out, decompressedSize);
                this.compress(workerContext.getDeflater(), payload, out);
            } else {
                ProtocolUtils.writeVarInt(out, 0);
                out.put(payload);
            }
            final int finalSize = out.position() - contentStart;
            ProtocolUtils.writeVarIntHeader(out, lengthIndex, finalSize);
        }

        private void compress(Deflater deflater, ByteBuffer input, ByteBuffer output) {
            deflater.setInput(input);
            deflater.finish();
            deflater.deflate(output);
            deflater.reset();
        }

        private void decompress(Inflater inflater, ByteBuffer input, ByteBuffer output) throws DataFormatException {
            inflater.setInput(input);
            inflater.inflate(output);
            inflater.reset();
        }
    };

    boolean read(ConnectionContext context, ByteBuffer buffer, ByteBuffer payloadOut, WorkerContext workerContext);

    void write(ConnectionContext context, ByteBuffer payload, ByteBuffer out, WorkerContext workerContext);
}