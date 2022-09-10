package net.versiongate.common.translation.version1_9.buffer;

import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferAdapter;
import net.versiongate.api.minecraft.chunk.Chunk;

public class ChunkAdapter implements BufferAdapter<Chunk> {

    @Override
    public Chunk read(ByteBuf buffer) {
        return null;
    }

    @Override
    public void write(ByteBuf buffer, Chunk value) {

    }
}
