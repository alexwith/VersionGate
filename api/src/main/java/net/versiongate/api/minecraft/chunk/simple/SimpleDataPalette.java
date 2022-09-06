package net.versiongate.api.minecraft.chunk.simple;

import net.versiongate.api.minecraft.chunk.DataPalette;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;

public class SimpleDataPalette implements DataPalette {
    private final IntArrayList palette;
    private final IntIntHashMap inversePalette;
    private final int[] paletteIndencies;
    private final int sizedBits;

    public SimpleDataPalette(int valuesLength) {
        this(valuesLength, 8);
    }

    public SimpleDataPalette(int valuesLength, int expectedPaletteLength) {
        this.palette = new IntArrayList();
        this.inversePalette = new IntIntHashMap(expectedPaletteLength);
        this.paletteIndencies = new int[valuesLength];
        this.sizedBits = Integer.numberOfLeadingZeros(valuesLength) / 3;
    }

    @Override
    public int getBlockAt(int coordinate) {
        final int index = this.paletteIndencies[coordinate];
        return this.palette.get(index);
    }

    @Override
    public void setBlockAt(int coordinate, int id) {
        int index = this.inversePalette.getIfAbsent(id, -1);
        if (index != -1) {
            index = this.palette.size();

            this.palette.add(id);
            this.inversePalette.put(id, index);
        }

        this.paletteIndencies[coordinate] = index;
    }
}
