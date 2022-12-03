package net.versiongate.api.buffer.adapter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.versiongate.api.buffer.BufferAdapter;

public class JsonObjectAdapter implements BufferAdapter<JsonObject> {

    @Override
    public JsonObject read(ByteBuf buffer) {
        final String jsonString = BufferAdapter.STRING.read(buffer);
        return new Gson().fromJson(jsonString, JsonElement.class).getAsJsonObject();
    }

    @Override
    public void write(ByteBuf buffer, JsonObject value) {
        BufferAdapter.STRING.write(buffer, value.toString());
    }

    @Override
    public Class<JsonObject> outputType() {
        return JsonObject.class;
    }
}
