package net.versiongate.common.translation.version1_8.buffer;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.minecraft.chunk.IChunk;
import net.versiongate.api.minecraft.chunk.IChunkSection;

public class BulkChunkAdapter implements BufferAdapter<IChunk[]> {
    private static final int BLOCKS_PER_SECTION = 16 * 16 * 16;
    private static final int BLOCKS_BYTES = BLOCKS_PER_SECTION * 2;
    private static final int LIGHT_BYTES = BLOCKS_PER_SECTION / 2;
    private static final int BIOME_BYTES = 16 * 16;

    @Override
    public IChunk[] read(ByteBuf buffer) {
        final boolean skyLight = buffer.readBoolean();
        final int count = BufferAdapter.VAR_INT.read(buffer);
        final IChunk[] chunks = new IChunk[count];
        final ChunkBulkSection[] chunkInfo = new ChunkBulkSection[count];

        for (int i = 0; i < chunkInfo.length; i++) {
            chunkInfo[i] = new ChunkBulkSection(buffer, skyLight);
        }

        for (int i = 0; i < chunks.length; i++) {
            final ChunkBulkSection chunkBulkSection = chunkInfo[i];
            chunkBulkSection.readData(buffer);

            chunks[i] = BufferAdapter1_8.CHUNK.deserialize(
                chunkBulkSection.getChunkX(),
                chunkBulkSection.getChunkZ(),
                true,
                skyLight,
                chunkBulkSection.getBitmask(),
                chunkBulkSection.getData()
            );
        }

        return chunks;
    }

    @Override
    public void write(ByteBuf buffer, IChunk[] chunks) {
        boolean skyLight = false;
        for (final IChunk chunk : chunks) {
            for (final IChunkSection section : chunk.getSections()) {
                if (section != null && section.getLight().hasSkyLight()) {
                    skyLight = true;
                    break;
                }
            }

            if (skyLight) {
                break;
            }
        }

        buffer.writeBoolean(skyLight);
        BufferAdapter.VAR_INT.write(buffer, chunks.length);

        for (final IChunk chunk : chunks) {
            buffer.writeInt(chunk.getX());
            buffer.writeInt(chunk.getZ());
            buffer.writeShort(chunk.getBitmask());
        }

        for (final IChunk chunk : chunks) {
            buffer.writeBytes(BufferAdapter1_8.CHUNK.serialize(chunk));
        }
    }

    private static final class ChunkBulkSection {
        private final int chunkX;
        private final int chunkZ;
        private final int bitmask;
        private final byte[] data;

        public ChunkBulkSection(ByteBuf buffer, boolean skyLight) {
            this.chunkX = buffer.readInt();
            this.chunkZ = buffer.readInt();
            this.bitmask = buffer.readUnsignedShort();
            this.data = new byte[Integer.bitCount(this.bitmask) * (BLOCKS_BYTES + (skyLight ? 2 * LIGHT_BYTES : LIGHT_BYTES)) + BIOME_BYTES];
        }

        public int getChunkX() {
            return this.chunkX;
        }

        public int getChunkZ() {
            return this.chunkZ;
        }

        public int getBitmask() {
            return this.bitmask;
        }

        public byte[] getData() {
            return this.data;
        }

        public void readData(final ByteBuf buffer) {
            buffer.readBytes(this.data);
        }
    }

    @Override
    public Class<IChunk[]> outputType() {
        return IChunk[].class;
    }
}