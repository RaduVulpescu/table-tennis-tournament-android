package com.example.tabletennistournament.enums;

import com.google.gson.annotations.SerializedName;

public enum FixtureState {
    @SerializedName("0") Upcoming,
    @SerializedName("1") GroupsSelection,
    @SerializedName("2") GroupsStage,
    @SerializedName("3") PyramidsStage,
    @SerializedName("4") ReadyToFinish,
    @SerializedName("5") Finished
}
