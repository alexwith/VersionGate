package net.versiongate.api.minecraft.chunk;

// Credit ViaVersion
public enum PaletteType {
    BLOCKS(ChunkSection.SIZE, 8),
    BIOMES(ChunkSection.BIOME_SIZE, 3);

    private final int size;
    private final int highestBitsPerValue;

    PaletteType(int size, int highestBitsPerValue) {
        this.size = size;
        this.highestBitsPerValue = highestBitsPerValue;
    }

    public int size() {
        return this.size;
    }

    public int highestBitsPerValue() {
        return this.highestBitsPerValue;
    }
}