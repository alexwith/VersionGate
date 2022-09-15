package net.versiongate.api.minecraft.chunk;

public interface IDataPalette {

    int sectionIndex(int x, int y, int z);

    int getIdAt(int sectionIndex);

    void setIdAt(int sectionIndex, int id);

    int getIdAtIndex(int index);

    void setIdAtIndex(int index, int id);

    void addId(int id);

    void replaceId(int oldId, int newId);

    int getPaletteIndexAt(int sectionIndex);

    void setPaletteIndexAt(int sectionIndex, int index);

    int size();

    void clear();

    default int getIdAt(int sectionX, int sectionY, int sectionZ) {
        final int sectionIndex = this.sectionIndex(sectionX, sectionY, sectionZ);
        return this.getIdAt(sectionIndex);
    }

    default void setIdAt(int sectionX, int sectionY, int sectionZ, int id) {
        final int sectionIndex = this.sectionIndex(sectionX, sectionY, sectionZ);
        this.setIdAt(sectionIndex, id);
    }
}
