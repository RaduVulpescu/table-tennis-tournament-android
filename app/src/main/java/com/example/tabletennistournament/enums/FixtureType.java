package com.example.tabletennistournament.enums;

import com.google.gson.annotations.SerializedName;

public enum FixtureType {
    @SerializedName("0") Normal,
    @SerializedName("1") Beginner,
    @SerializedName("2") Intermediate,
    @SerializedName("3") Advanced,
    @SerializedName("4") Open
}
