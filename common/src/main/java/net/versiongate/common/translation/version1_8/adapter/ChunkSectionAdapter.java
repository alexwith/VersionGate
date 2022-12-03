package net.versiongate.common.translation.version1_8.adapter;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.minecraft.chunk.IChunkSection;
import net.versiongate.api.minecraft.chunk.IDataPalette;
import net.versiongate.api.minecraft.chunk.PaletteType;
import net.versiongate.api.minecraft.chunk.simple.ChunkSection;

public class ChunkSectionAdapter implements BufferAdapter<IChunkSection> {

    @Override
    public IChunkSection read(ByteBuf buffer) {
        final IChunkSection section = new ChunkSection(true);
        final IDataPalette palette = section.getPalette(PaletteType.BLOCKS);

        palette.addId(0);

        for (int i = 0; i < IChunkSection.SIZE; i++) {
            final int mask = buffer.readShortLE();
            final int type = mask >> 4;
            final int data = mask & 0xF;
            palette.setIdAt(i, type << 4 | (data & 0xF));
        }

        return section;
    }

    @Override
    public void write(ByteBuf buffer, IChunkSection section) {
        final IDataPalette palette = section.getPalette(PaletteType.BLOCKS);

        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    final int block = palette.getIdAt(IChunkSection.sectionIndex(x, y, z));

                    buffer.writeByte(block);
                    buffer.writeByte(block >> 8);
                }
            }
        }
    }

    @Override
    public Class<IChunkSection> outputType() {
        return IChunkSection.class;
    }
}
