package com.convallyria.queste.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.util.Map;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    private final Gson gson;

    public LocationAdapter() {
        this.gson = new GsonBuilder().create();
    }

    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        return Location.deserialize(gson.fromJson(jsonElement, new TypeToken<Map<String, Object>>(){}.getType()));
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {
        return gson.toJsonTree(location.serialize());
    }
}
