package net.versiongate.api.minecraft.block.simple;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import net.versiongate.api.minecraft.block.BlockEntity;

// Credit ViaVersion
public class SimpleBlockEntity implements BlockEntity {
    private final byte xzIndex;
    private final short y;
    private final int typeId;
    private final CompoundTag tag;

    public SimpleBlockEntity(byte xzIndex, short y, int typeId, CompoundTag tag) {
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
