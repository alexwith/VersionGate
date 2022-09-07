package net.versiongate.api.minecraft.chunk;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import java.util.BitSet;
import java.util.List;
import net.versiongate.api.minecraft.block.BlockEntity;

public interface Chunk {

    int getX();

    int getZ();

    boolean isBiomeData();

    boolean isFullChunk();

    boolean isIgnoreOldLightData();

    void setIgnoreOldLightData(boolean ignoreOldLightData);

    int getBitmask();

    void setBitmask(int bitmask);

    void setChunkMask(BitSet chunkSectionMask);

    ChunkSection[] getSections();

    void setSections(ChunkSection[] sections);

    int[] getBiomeData();

    void setBiomeData(int[] biomeData);

    CompoundTag getHeightMap();

    void setHeightMap(CompoundTag heightMap);

    List<CompoundTag> getBlockEntities();

    List<BlockEntity> blockEntities();
}
