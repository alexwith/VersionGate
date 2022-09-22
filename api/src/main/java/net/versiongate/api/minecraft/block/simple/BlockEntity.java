package net.versiongate.api.minecraft.block.simple;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import net.versiongate.api.minecraft.block.IBlockEntity;

// Credit ViaVersion
public class BlockEntity implements IBlockEntity {
    private final byte xzIndex;
    private final short y;
    private final int typeId;
    private final CompoundTag tag;

    public BlockEntity(byte xzIndex, short y, int typeId, CompoundTag tag) {
        this.xzIndex = xzIndex;
        this.y = y;
        this.typeId = typeId;
        this.tag = tag;
    }

    @Override
    public byte getXZIndex() {
        return this.xzIndex;
    }

    @Override
    public short getY() {
        return this.y;
    }

    @Override
    public int getTypeId() {
        return this.typeId;
    }

    @Override
    public CompoundTag getTag() {
        return this.tag;
    }
}
