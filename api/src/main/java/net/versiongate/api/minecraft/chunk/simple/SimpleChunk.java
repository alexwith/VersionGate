package net.versiongate.api.minecraft.chunk.simple;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import java.util.BitSet;
import java.util.List;
import net.versiongate.api.minecraft.block.BlockEntity;
import net.versiongate.api.minecraft.chunk.Chunk;
import net.versiongate.api.minecraft.chunk.ChunkSection;

// Credit ViaVersion
public class SimpleChunk implements Chunk {
    protected final int x;
    protected final int z;
    protected boolean fullChunk;

    protected boolean ignoreOldLightData;
    protected BitSet chunkSectionBitSet;
    protected int bitmask;
    protected ChunkSection[] sections;
    protected int[] biomeData;
    protected CompoundTag heightMap;
    protected final List<CompoundTag> blockEntities;

    public SimpleChunk(
        int x,
        int z,
        boolean fullChunk,
        boolean ignoreOldLightData,
        BitSet chunkSectionBitSet,
        ChunkSection[] sections,
        int[] biomeData,
        CompoundTag heightMap,
        List<CompoundTag> blockEntities
    ) {
        this.x = x;
        this.z = z;
        this.fullChunk = fullChunk;
        this.ignoreOldLightData = ignoreOldLightData;
        this.chunkSectionBitSet = chunkSectionBitSet;
        this.sections = sections;
        this.biomeData = biomeData;
        this.heightMap = heightMap;
        this.blockEntities = blockEntities;
    }

    public SimpleChunk(
        int x,
        int z,
        boolean fullChunk,
        boolean ignoreOldLightData,
        BitSet chunkSectionBitSet,
        ChunkSection[] sections,
        int[] biomeData,
        CompoundTag heightMap,
        List<CompoundTag> blockEntities,
        int bitmask
    ) {
        this(x, z, fullChunk, ignoreOldLightData, chunkSectionBitSet, sections, biomeData, heightMap, blockEntities);
        this.bitmask = bitmask;
    }

    public SimpleChunk(
        int x,
        int z,
        boolean fullChunk,
        boolean ignoreOldLightData,
        BitSet chunkSectionBitSet,
        ChunkSection[] sections,
        int[] biomeData,
        List<CompoundTag> blockEntities
    ) {
        this(x, z, fullChunk, ignoreOldLightData, chunkSectionBitSet, sections, biomeData, null, blockEntities);
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    public boolean isBiomeData() {
        return this.biomeData != null;
    }

    @Override
    public boolean isFullChunk() {
        return this.fullChunk;
    }

    @Override
    public boolean isIgnoreOldLightData() {
        return this.ignoreOldLightData;
    }

    @Override
    public void setIgnoreOldLightData(boolean ignoreOldLightData) {
        this.ignoreOldLightData = ignoreOldLightData;
    }

    @Override
    public int getBitmask() {
        return this.bitmask;
    }

    @Override
    public void setBitmask(int bitmask) {
        this.bitmask = bitmask;
    }

    @Override
    public void setChunkMask(BitSet chunkSectionMask) {
        this.chunkSectionBitSet = chunkSectionMask;
    }

    @Override
    public ChunkSection[] getSections() {
        return this.sections;
    }

    @Override
    public void setSections(ChunkSection[] sections) {
        this.sections = sections;
    }

    @Override
    public int[] getBiomeData() {
        return this.biomeData;
    }

    @Override
    public void setBiomeData(int[] biomeData) {
        this.biomeData = biomeData;
    }

    @Override
    public CompoundTag getHeightMap() {
        return this.heightMap;
    }

    @Override
    public void setHeightMap(CompoundTag heightMap) {
        this.heightMap = heightMap;
    }

    @Override
    public List<CompoundTag> getBlockEntities() {
        return this.blockEntities;
    }

    @Override
    public List<BlockEntity> blockEntities() {
        return null;
    }
}
