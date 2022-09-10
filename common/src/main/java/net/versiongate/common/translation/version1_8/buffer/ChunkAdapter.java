package net.versiongate.common.translation.version1_8.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.minecraft.chunk.Chunk;
import net.versiongate.api.minecraft.chunk.ChunkSection;
import net.versiongate.api.minecraft.chunk.ChunkSectionLight;
import net.versiongate.api.minecraft.chunk.simple.SimpleChunk;

public class ChunkAdapter implements BufferAdapter<Chunk> {

    @Override
    public Chunk read(ByteBuf buffer) {
        final int chunkX = buffer.readInt();
        final int chunkZ = buffer.readInt();

        final boolean fullChunk = buffer.readBoolean();
        final int bitmask = buffer.readUnsignedShort();
        final int dataLength = BufferAdapter.VAR_INT.read(buffer);

        final byte[] data = new byte[dataLength];
        buffer.readBytes(data);

        if (fullChunk && bitmask == 0) {
            return new SimpleChunk(chunkX, chunkZ, true, false, 0, new ChunkSection[16], null, new ArrayList<>());
        }

        final boolean hasSkyLight = true; //Environment.NORMAL
        return this.deserialize(chunkX, chunkZ, fullChunk, hasSkyLight, bitmask, data);
    }

    @Override
    public void write(ByteBuf buffer, Chunk chunk) {
        buffer.writeInt(chunk.getX());
        buffer.writeInt(chunk.getZ());
        buffer.writeBoolean(chunk.isFullChunk());
        buffer.writeShort(chunk.getBitmask());

        final byte[] data = this.serialize(chunk);
        BufferAdapter.VAR_INT.write(buffer, data.length);

        buffer.writeBytes(data);
    }

    private Chunk deserialize(final int chunkX, final int chunkZ, final boolean fullChunk, final boolean skyLight, final int bitmask, final byte[] data) {
        final ByteBuf input = Unpooled.wrappedBuffer(data);

        final ChunkSection[] sections = new ChunkSection[16];
        for (int i = 0; i < sections.length; i++) {
            if ((bitmask & 1 << i) == 0) {
                continue;
            }

            sections[i] = BufferAdapter1_8.CHUNK_SECTION.read(input);
        }

        for (int i = 0; i < sections.length; i++) {
            if ((bitmask & 1 << i) == 0) {
                continue;
            }

            final ChunkSectionLight light = sections[i].getLight();
            light.readBlockLight(input);
        }

        if (skyLight) {
            for (int i = 0; i < sections.length; i++) {
                if ((bitmask & 1 << i) == 0) {
                    continue;
                }

                final ChunkSectionLight light = sections[i].getLight();
                light.readSkyLight(input);
            }
        }

        final int[] biomeData = fullChunk ? new int[256] : null;
        if (biomeData != null) {
            for (int i = 0; i < 256; i++) {
                biomeData[i] = input.readUnsignedByte();
            }
        }

        input.release();

        return new SimpleChunk(chunkX, chunkZ, fullChunk, false, bitmask, sections, biomeData, new ArrayList<>());
    }

    private byte[] serialize(final Chunk chunk) {
        final ByteBuf output = Unpooled.buffer();

        final int bitmask = chunk.getBitmask();
        final ChunkSection[] sections = chunk.getSections();

        for (int i = 0; i < sections.length; i++) {
            if ((bitmask & 1 << i) == 0) {
                continue;
            }
            BufferAdapter1_8.CHUNK_SECTION.write(output, sections[i]);
        }

        for (int i = 0; i < sections.length; i++) {
            if ((bitmask & 1 << i) == 0) {
                continue;
            }

            final ChunkSectionLight light = sections[i].getLight();
            light.writeBlockLight(output);
        }

        for (int i = 0; i < sections.length; i++) {
            if ((bitmask & 1 << i) == 0) {
                continue;
            }

            final ChunkSectionLight light = sections[i].getLight();
            if (!light.hasSkyLight()) {
                continue;
            }

            light.writeSkyLight(output);
        }

        if (chunk.isFullChunk() && chunk.getBiomeData() != null) {
            for (final int biome : chunk.getBiomeData()) {
                output.writeByte((byte) biome);
            }
        }

        final byte[] data = new byte[output.readableBytes()];
        output.readBytes(data);
        output.release();

        return data;
    }
}
