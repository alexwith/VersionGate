package net.versiongate.common.translation.version1_12.adapter;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.minecraft.chunk.IChunkSection;
import net.versiongate.api.minecraft.chunk.IDataPalette;
import net.versiongate.api.minecraft.chunk.PaletteType;
import net.versiongate.api.minecraft.chunk.simple.ChunkSection;
import net.versiongate.common.util.CompactArrayUtil;

public class ChunkSectionAdapter implements BufferAdapter<IChunkSection> {
    private static final int GLOBAL_PALETTE = 13;

    @Override
    public IChunkSection read(ByteBuf buffer) {
        int bitsPerBlock = buffer.readUnsignedByte();

        if (bitsPerBlock < 4) {
            bitsPerBlock = 4;
        }
        if (bitsPerBlock > 8) {
            bitsPerBlock = GLOBAL_PALETTE;
        }

        final int paletteLength = BufferAdapter.VAR_INT.read(buffer);
        final IChunkSection section = bitsPerBlock != GLOBAL_PALETTE ? new ChunkSection(true, paletteLength) : new ChunkSection(true);
        final IDataPalette blockPalette = section.getPalette(PaletteType.BLOCKS);

        for (int i = 0; i < paletteLength; i++) {
            if (bitsPerBlock != GLOBAL_PALETTE) {
                final int id = BufferAdapter.VAR_INT.read(buffer);
                blockPalette.addId(id);
                continue;
            }

            BufferAdapter.VAR_INT.read(buffer);
        }

        final long[] blockData = BufferAdapter.LONG_ARRAY.read(buffer);
        if (blockData.length > 0) {
            final int expectedLength = (int) Math.ceil(IChunkSection.SIZE * bitsPerBlock / 64.0);

            if (blockData.length == expectedLength) {
                CompactArrayUtil.iterateCompactArray(bitsPerBlock, IChunkSection.SIZE, blockData,
                    bitsPerBlock == GLOBAL_PALETTE ?
                    blockPalette::setIdAt :
                    blockPalette::setPaletteIndexAt
                );
            }
        }

        return section;
    }

    @Override
    public void write(ByteBuf buffer, IChunkSection section) {
        final int paletteSize = section.getPalette(PaletteType.BLOCKS).size();
        final IDataPalette blockPalette = section.getPalette(PaletteType.BLOCKS);

        int bitsPerBlock = 4;
        while (paletteSize > 1 << bitsPerBlock) {
            bitsPerBlock += 1;
        }

        if (bitsPerBlock > 8) {
            bitsPerBlock = GLOBAL_PALETTE;
        }

        buffer.writeByte(bitsPerBlock);

        if (bitsPerBlock != GLOBAL_PALETTE) {
            BufferAdapter.VAR_INT.write(buffer, paletteSize);
            for (int i = 0; i < paletteSize; i++) {
                BufferAdapter.VAR_INT.write(buffer, blockPalette.getIdAt(i));
            }
        } else {
            BufferAdapter.VAR_INT.write(buffer, 0);
        }

        final long[] data = CompactArrayUtil.createCompactArray(bitsPerBlock, IChunkSection.SIZE,
            bitsPerBlock == GLOBAL_PALETTE ?
            (sectionIndex) -> (long) blockPalette.getIdAt(sectionIndex) :
            (sectionIndex) -> (long) blockPalette.getPaletteIndexAt(sectionIndex)
        );
        BufferAdapter.LONG_ARRAY.write(buffer, data);
    }

    @Override
    public Class<IChunkSection> outputType() {
        return IChunkSection.class;
    }
}
