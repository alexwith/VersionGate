package net.versiongate.common.translation.version1_9.buffer;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.BitSet;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.minecraft.chunk.IChunk;
import net.versiongate.api.minecraft.chunk.IChunkSection;
import net.versiongate.api.minecraft.chunk.simple.Chunk;

public class ChunkAdapter implements BufferAdapter<IChunk> {

    @Override
    public IChunk read(ByteBuf buffer) {
        final int chunkX = buffer.readInt();
        final int chunkZ = buffer.readInt();

        final boolean groundUp = buffer.readBoolean();
        final int primaryBitmask = BufferAdapter.VAR_INT.read(buffer);

        BufferAdapter.VAR_INT.read(buffer); // skip size

        final BitSet usedSections = new BitSet(16);
        final IChunkSection[] sections = new IChunkSection[16];

        for (int i = 0; i < 16; i++) {
            if ((primaryBitmask & (1 << i)) != 0) {
                usedSections.set(i);
            }
        }

        for (int i = 0; i < 16; i++) {
            if (!usedSections.get(i)) {
                continue;
            }

            final IChunkSection section = BufferAdapter1_9.CHUNK_SECTION.read(buffer);
            sections[i] = section;

            section.getLight().readBlockLight(buffer);

            //if (world.getEnvironment() == Environment.NORMAL) {
            section.getLight().readSkyLight(buffer);
            //}
        }

        final int[] biomeData = groundUp ? new int[256] : null;
        if (groundUp) {
            for (int i = 0; i < 256; i++) {
                biomeData[i] = buffer.readByte() & 0xFF;
            }
        }

        return new Chunk(chunkX, chunkZ, groundUp, false, primaryBitmask, sections, biomeData, new ArrayList<CompoundTag>());
    }

    @Override
    public void write(ByteBuf buffer, IChunk chunk) {
        buffer.writeInt(chunk.getX());
        buffer.writeInt(chunk.getZ());

        buffer.writeBoolean(chunk.isFullChunk());
        BufferAdapter.VAR_INT.write(buffer, chunk.getBitmask());

        final ByteBuf subBuffer = buffer.alloc().buffer();
        try {
            for (int i = 0; i < 16; i++) {
                final IChunkSection section = chunk.getSections()[i];
                if (section == null) {
                    continue;
                }

                BufferAdapter1_9.CHUNK_SECTION.write(subBuffer, section);

                section.getLight().writeBlockLight(subBuffer);
                if (!section.getLight().hasSkyLight()) {
                    continue;
                }

                section.getLight().writeSkyLight(subBuffer);
            }

            subBuffer.readerIndex(0);

            BufferAdapter.VAR_INT.write(buffer, subBuffer.readableBytes() + (chunk.isBiomeData() ? 256 : 0));
            buffer.writeBytes(subBuffer);
        } finally {
            subBuffer.release();
        }

        if (chunk.isBiomeData()) {
            for (final int biome : chunk.getBiomeData()) {
                buffer.writeByte((byte) biome);
            }
        }
    }
}
