package net.versiongate.api.minecraft.chunk.simple;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.minecraft.chunk.ChunkSection;
import net.versiongate.api.minecraft.chunk.ChunkSectionLight;
import net.versiongate.api.minecraft.chunk.NibbleArray;

// Credit ViaVersion
public class SimpleChunkSectionLight implements ChunkSectionLight {
    private NibbleArray blockLight;
    private NibbleArray skyLight;

    public SimpleChunkSectionLight() {
        this.blockLight = new NibbleArray(ChunkSection.SIZE);
    }

    @Override
    public NibbleArray getBlockLightArray() {
        return this.blockLight;
    }

    @Override
    public NibbleArray getSkyLightArray() {
        return this.skyLight;
    }

    @Override
    public byte[] getBlockLight() {
        return this.blockLight == null ? null : this.blockLight.getHandle();
    }

    @Override
    public byte[] getSkyLight() {
        return this.skyLight == null ? null : this.skyLight.getHandle();
    }

    @Override
    public void setBlockLight(byte[] data) {
        if (data.length != LIGHT_LENGTH) {
            throw new IllegalArgumentException("Data length != " + LIGHT_LENGTH);
        }

        if (this.blockLight == null) {
            this.blockLight = new NibbleArray(data);
            return;
        }

        this.blockLight.setHandle(data);
    }

    @Override
    public void setSkyLight(byte[] data) {
        if (data.length != LIGHT_LENGTH) {
            throw new IllegalArgumentException("Data length != " + LIGHT_LENGTH);
        }

        if (this.skyLight == null) {
            this.skyLight = new NibbleArray(data);
            return;
        }

        this.skyLight.setHandle(data);
    }

    @Override
    public void readBlockLight(ByteBuf buffer) {
        if (this.blockLight == null) {
            this.blockLight = new NibbleArray(LIGHT_LENGTH * 2);
        }

        buffer.readBytes(this.blockLight.getHandle());
    }

    @Override
    public void readSkyLight(ByteBuf buffer) {
        if (this.skyLight == null) {
            this.skyLight = new NibbleArray(LIGHT_LENGTH * 2);
        }

        buffer.readBytes(this.skyLight.getHandle());
    }

    @Override
    public void writeBlockLight(ByteBuf buffer) {
        buffer.writeBytes(this.blockLight.getHandle());
    }

    @Override
    public void writeSkyLight(ByteBuf buffer) {
        buffer.writeBytes(this.skyLight.getHandle());
    }
}
