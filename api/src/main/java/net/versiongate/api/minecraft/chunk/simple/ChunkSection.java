package net.versiongate.api.minecraft.chunk.simple;

import java.util.EnumMap;
import net.versiongate.api.minecraft.chunk.IChunkSection;
import net.versiongate.api.minecraft.chunk.IChunkSectionLight;
import net.versiongate.api.minecraft.chunk.IDataPalette;
import net.versiongate.api.minecraft.chunk.PaletteType;

// Credit ViaVersion
public class ChunkSection implements IChunkSection {
    private final EnumMap<PaletteType, IDataPalette> palettes = new EnumMap<>(PaletteType.class);

    private IChunkSectionLight light;
    private int nonAirBlockCount;

    public ChunkSection() {}

    public ChunkSection(boolean lightHolder) {
        this(lightHolder, 8);
    }

    public ChunkSection(boolean lightHolder, int expectedPaletteLength) {
        this.addPalette(PaletteType.BLOCKS, new DataPalette(SIZE, expectedPaletteLength));

        if (lightHolder) {
            this.light = new ChunkSectionLight();
        }
    }

    @Override
    public IChunkSectionLight getLight() {
        return this.light;
    }

    @Override
    public void setLight(IChunkSectionLight light) {
        this.light = light;
    }

    @Override
    public int getNonAirBlocksCount() {
        return this.nonAirBlockCount;
    }

    @Override
    public void setNonAirBlocksCount(int count) {
        this.nonAirBlockCount = count;
    }

    @Override
    public IDataPalette getPalette(PaletteType type) {
        return this.palettes.get(type);
    }

    @Override
    public void addPalette(PaletteType type, IDataPalette palette) {
        this.palettes.put(type, palette);
    }

    @Override
    public void removePalette(PaletteType type) {
        this.palettes.remove(type);
    }
}
