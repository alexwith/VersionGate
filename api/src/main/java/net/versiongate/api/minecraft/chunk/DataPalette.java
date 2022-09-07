package net.versiongate.api.minecraft.chunk;

public interface DataPalette {

    int sectionIndex(int x, int y, int z);

    int getBlockAt(int sectionIndex);

    void setBlockAt(int sectionIndex, int id);

    int getBlockAtIndex(int index);

    void setBlockAtIndex(int index, int id);

    void addBlock(int id);

    void replaceBlock(int oldId, int newId);

    int getPaletteIndexAt(int sectionIndex);

    void setPaletteIndexAt(int sectionIndex, int index);

    int size();

    void clear();

    default int getBlockAt(int sectionX, int sectionY, int sectionZ) {
        final int sectionIndex = this.sectionIndex(sectionX, sectionY, sectionZ);
        return this.getBlockAt(sectionIndex);
    }

    default void setBlockAt(int sectionX, int sectionY, int sectionZ, int id) {
        final int sectionIndex = this.sectionIndex(sectionX, sectionY, sectionZ);
        this.setBlockAt(sectionIndex, id);
    }
}
