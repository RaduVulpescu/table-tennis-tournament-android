package com.example.tabletennistournament.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class GsonSingleton {
    private static Gson instance;

    private GsonSingleton() {
    }

    public static synchronized Gson getInstance() {
        if (instance == null) {
            instance = new GsonBuilder()
                    .registerTypeAdapter(ZonedDateTime.class, new TypeAdapter<ZonedDateTime>() {
                        @Override
                        public void write(JsonWriter out, ZonedDateTime value) throws IOException {
                            out.value(value.toInstant().toString());
                        }

                        @Override
                        public ZonedDateTime read(JsonReader in) throws IOException {
                            return ZonedDateTime.parse(in.nextString()).withZoneSameInstant(ZoneId.systemDefault());
                        }
                    })
                    .enableComplexMapKeySerialization()
                    .create();
        }

        return instance;
    }
}
