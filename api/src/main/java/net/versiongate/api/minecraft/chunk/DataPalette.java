package net.versiongate.api.minecraft.chunk;

public interface DataPalette {

    int getBlockAt(int coordinate);

    void setBlockAt(int coordinate, int id);
}
