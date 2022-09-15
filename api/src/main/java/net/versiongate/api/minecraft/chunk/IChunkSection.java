package net.versiongate.api.minecraft.chunk;

public interface IChunkSection {
    int SIZE = 16 * 16 * 16;
    int BIOME_SIZE = 4 * 4 * 4;

    static int sectionIndex(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    IChunkSectionLight getLight();

    void setLight(IChunkSectionLight light);

    int getNonAirBlocksCount();

    void setNonAirBlocksCount(int count);

    IDataPalette getPalette(PaletteType type);

    void addPalette(PaletteType type, IDataPalette palette);

    void removePalette(PaletteType type);
}
