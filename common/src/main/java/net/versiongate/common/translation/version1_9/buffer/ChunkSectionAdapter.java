package net.versiongate.common.translation.version1_9.buffer;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.minecraft.chunk.ChunkSection;
import net.versiongate.api.minecraft.chunk.DataPalette;
import net.versiongate.api.minecraft.chunk.PaletteType;
import net.versiongate.api.minecraft.chunk.simple.SimpleChunkSection;

public class ChunkSectionAdapter implements BufferAdapter<ChunkSection> {
    private static final int GLOBAL_PALETTE = 13;

    @Override
    public ChunkSection read(ByteBuf buffer) {
        int bitsPerBlock = buffer.readUnsignedByte();

        if (bitsPerBlock < 4) {
            bitsPerBlock = 4;
        }
        if (bitsPerBlock > 8) {
            bitsPerBlock = GLOBAL_PALETTE;
        }

        final int paletteLength = BufferAdapter.VAR_INT.read(buffer);
        final ChunkSection section = bitsPerBlock != GLOBAL_PALETTE ? new SimpleChunkSection(true, paletteLength) : new SimpleChunkSection(true);
        final DataPalette palette = section.getPalette(PaletteType.BLOCKS);

        for (int i = 0; i < paletteLength; i++) {
            if (bitsPerBlock != GLOBAL_PALETTE) {
                final int id = BufferAdapter.VAR_INT.read(buffer);
                palette.addId(id);
                continue;
            }

            BufferAdapter.VAR_INT.read(buffer);
        }

        final long[] blockData = BufferAdapter.LONG_ARRAY_PRIMITIVE.read(buffer);
        if (blockData.length > 0) {
            int expectedLength = (int) Math.ceil(ChunkSection.SIZE * bitsPerBlock / 64.0);
            if (blockData.length == expectedLength) {
                CompactArrayUtil.iterateCompactArray(bitsPerBlock, ChunkSection.SIZE, blockData,
                    bitsPerBlock == GLOBAL_PALETTE ? section::setFlatBlock
                                                   : section::setPaletteIndex
                );
            }
        }

        return section;
    }

    @Override
    public void write(ByteBuf buffer, ChunkSection value) {

    }
}
