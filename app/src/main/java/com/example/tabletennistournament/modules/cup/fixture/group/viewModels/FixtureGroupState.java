package com.example.tabletennistournament.modules.cup.fixture.group.viewModels;

public class FixtureGroupState {
    private final int totalMatches;
    private final int finishedMatches;
    private final boolean displayEndGroupStageButton;
    private final boolean isGroupStage;

    FixtureGroupState(int totalMatches, int finishedMatches, boolean isGroupStage) {
        this.totalMatches = totalMatches;
        this.finishedMatches = finishedMatches;
        this.isGroupStage = isGroupStage;
        this.displayEndGroupStageButton = totalMatches == finishedMatches && isGroupStage;
    }

    public boolean displayEndGroupStageButton() {
        return displayEndGroupStageButton;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public int getFinishedMatches() {
        return finishedMatches;
    }

    public boolean getIsGroupStage() {
        return isGroupStage;
    }
}
