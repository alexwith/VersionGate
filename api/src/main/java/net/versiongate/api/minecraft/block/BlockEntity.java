package net.versiongate.api.minecraft.block;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;

public interface BlockEntity {

    byte getXZIndex();

    short getY();

    int getTypeId();

    CompoundTag getTag();
}
