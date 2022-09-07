package net.versiongate.api.minecraft.chunk;

import io.netty.buffer.ByteBuf;

public interface ChunkSectionLight {

    int LIGHT_LENGTH = 16 * 16 * 16 / 2;

    NibbleArray getBlockLightArray();

    NibbleArray getSkyLightArray();

    byte[] getBlockLight();

    byte[] getSkyLight();

    void setBlockLight(byte[] data);

    void setSkyLight(byte[] data);

    void readBlockLight(ByteBuf buffer);

    void readSkyLight(ByteBuf buffer);

    void writeBlockLight(ByteBuf buffer);

    void writeSkyLight(ByteBuf buffer);

    default boolean hasBlockLight() {
        return this.getBlockLight() != null;
    }

    default boolean hasSkyLight() {
        return this.getSkyLight() != null;
    }
}
