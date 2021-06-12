package com.example.tabletennistournament.enums;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public enum Level {
    @SerializedName("0") Undefined,
    @SerializedName("2") Beginner,
    @SerializedName("3") Intermediate,
    @SerializedName("4") Advanced,
    @SerializedName("5") Open;

    public static String[] getValues() {
        return Arrays.stream(Level.values()).map(Enum::name).toArray(String[]::new);
    }

}
