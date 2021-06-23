package com.example.tabletennistournament.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;

public class GsonSingleton {
    private static Gson instance;

    private GsonSingleton() {
    }

    public static synchronized Gson getInstance() {
        if (instance == null) {
            GsonBuilder builder = new GsonBuilder();

            builder.registerTypeAdapter(ZonedDateTime.class,
                    (JsonDeserializer<ZonedDateTime>) (json, type, jsonDeserializationContext) ->
                            ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString())
            );

            builder.registerTypeAdapter(ZonedDateTime.class,
                    (JsonSerializer<ZonedDateTime>) (src, typeOfSrc, context) ->
                            new JsonPrimitive(src.toInstant().toString())
            );

            instance = builder.create();
        }

        return instance;
    }
}
