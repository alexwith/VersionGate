package net.versiongate.api.minecraft.chunk;

public interface ChunkSection {
    int SIZE = 16 * 16 * 16;
    int BIOME_SIZE = 4 * 4 * 4;

    static int sectionIndex(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    ChunkSectionLight getLight();

    void setLight(ChunkSectionLight light);

    int getNonAirBlocksCount();

    void setNonAirBlocksCount(int count);

    DataPalette getPalette(PaletteType type);

    void addPalette(PaletteType type, DataPalette palette);

    void removePalette(PaletteType type);
}
