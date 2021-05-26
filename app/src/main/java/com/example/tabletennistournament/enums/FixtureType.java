package com.example.tabletennistournament.enums;

import com.google.gson.annotations.SerializedName;

public enum FixtureType {
    @SerializedName("0") Normal,
    @SerializedName("1") BeginnerFinal,
    @SerializedName("2") IntermediateFinal,
    @SerializedName("3") AdvancedFinal,
    @SerializedName("4") OpenFinal
}
