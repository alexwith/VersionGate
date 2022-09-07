package net.versiongate.api.minecraft.chunk.simple;

import java.util.EnumMap;
import net.versiongate.api.minecraft.chunk.ChunkSection;
import net.versiongate.api.minecraft.chunk.ChunkSectionLight;
import net.versiongate.api.minecraft.chunk.DataPalette;
import net.versiongate.api.minecraft.chunk.PaletteType;

// Credit ViaVersion
public class SimpleChunkSection implements ChunkSection {
    private final EnumMap<PaletteType, DataPalette> palettes = new EnumMap<>(PaletteType.class);

    private ChunkSectionLight light;
    private int nonAirBlockCount;

    public SimpleChunkSection() {}

    public SimpleChunkSection(boolean lightHolder) {
        this(lightHolder, 8);
    }

    public SimpleChunkSection(boolean lightHolder, int expectedPaletteLength) {
        new SimpleDataPalette(SIZE, expectedPaletteLength);

        if (lightHolder) {
            this.light = new SimpleChunkSectionLight();
        }
    }

    @Override
    public ChunkSectionLight getLight() {
        return this.light;
    }

    @Override
    public void setLight(ChunkSectionLight light) {
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
    public DataPalette getPalette(PaletteType type) {
        return this.palettes.get(type);
    }

    @Override
    public void addPalette(PaletteType type, DataPalette palette) {
        this.palettes.put(type, palette);
    }

    @Override
    public void removePalette(PaletteType type) {
        this.palettes.remove(type);
    }
}
