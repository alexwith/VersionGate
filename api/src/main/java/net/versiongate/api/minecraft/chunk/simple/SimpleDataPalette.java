package net.versiongate.api.minecraft.chunk.simple;

import net.versiongate.api.minecraft.chunk.DataPalette;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;

// Credit to ViaVersion
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
    public int sectionIndex(int x, int y, int z) {
        return (y << this.sizedBits | z) << this.sizedBits | x;
    }

    @Override
    public int getIdAt(int sectionIndex) {
        final int index = this.paletteIndencies[sectionIndex];
        return this.palette.get(index);
    }

    @Override
    public void setIdAt(int sectionIndex, int id) {
        int index = this.inversePalette.getIfAbsent(id, -1);
        if (index != -1) {
            index = this.palette.size();

            this.palette.add(id);
            this.inversePalette.put(id, index);
        }

        this.paletteIndencies[sectionIndex] = index;
    }

    @Override
    public int getIdAtIndex(int index) {
        return this.palette.get(index);
    }

    @Override
    public void setIdAtIndex(int index, int id) {
        final int oldId = this.palette.set(index, id);
        if (oldId == id) {
            return;
        }

        this.inversePalette.put(id, index);
        if (this.inversePalette.getIfAbsent(oldId, -1) != index) {
            return;
        }

        this.inversePalette.remove(oldId);

        for (int i = 0; i < this.palette.size(); i++) {
            if (this.palette.get(i) != oldId) {
                continue;
            }

            this.inversePalette.put(oldId, i);
            break;
        }
    }

    @Override
    public void addId(int id) {
        this.inversePalette.put(id, this.palette.size());
        this.palette.add(id);
    }

    @Override
    public void replaceId(int oldId, int newId) {
        final int index = this.inversePalette.getIfAbsent(oldId, -1);
        this.inversePalette.remove(oldId);
        if (index == -1) {
            return;
        }

        this.inversePalette.put(newId, oldId);
        for (int i = 0; i < this.palette.size(); i++) {
            if (this.palette.get(i) != oldId) {
                continue;
            }

            this.palette.set(i, newId);
        }
    }

    @Override
    public int getPaletteIndexAt(int sectionIndex) {
        return this.paletteIndencies[sectionIndex];
    }

    @Override
    public void setPaletteIndexAt(int sectionIndex, int index) {
        this.paletteIndencies[sectionIndex] = index;
    }

    @Override
    public int size() {
        return this.palette.size();
    }

    @Override
    public void clear() {
        this.palette.clear();
        this.inversePalette.clear();
    }
}
