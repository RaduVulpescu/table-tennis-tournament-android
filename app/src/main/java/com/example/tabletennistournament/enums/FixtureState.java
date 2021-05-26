package com.example.tabletennistournament.enums;

import com.google.gson.annotations.SerializedName;

public enum FixtureState {
    @SerializedName("0") Upcoming,
    @SerializedName("1") GroupsSelection,
    @SerializedName("2") PyramidsStage,
    @SerializedName("3") Finished
}
