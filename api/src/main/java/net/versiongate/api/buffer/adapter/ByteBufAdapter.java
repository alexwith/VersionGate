package net.versiongate.api.buffer.adapter;

import io.netty.buffer.ByteBuf;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.versiongate.api.buffer.BufferAdapter;

public abstract class ByteBufAdapter<T> implements BufferAdapter<T> {

    public static <T> ByteBufAdapter<T> of(Function<ByteBuf, T> reader, BiConsumer<ByteBuf, T> writer, Class<T> outputType) {
        return of(reader, writer, outputType, null);
    }

    public static <T> ByteBufAdapter<T> of(Function<ByteBuf, T> reader, BiConsumer<ByteBuf, T> writer, Class<T> outputType, Function<Object, T> transformer) {
        return new ByteBufAdapter<T>() {

            @Override
            public T read(ByteBuf buffer) {
                return reader.apply(buffer);
            }

            @Override
            public void write(ByteBuf buffer, T value) {
                writer.accept(buffer, value);
            }

            @Override
            public Class<T> outputType() {
                return outputType;
            }

            @Override
            public T transform(Object object) {
                return transformer == null ? super.transform(object) : transformer.apply(object);
            }
        };
    }
}
