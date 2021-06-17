package com.example.tabletennistournament.modules.cup.fixture.viewModels;

public class FixtureGroupState {
    private final int totalMatches;
    private final int finishedMatches;
    private final boolean displayEndGroupStageButton;

    FixtureGroupState(int totalMatches, int finishedMatches, boolean groupStageIsFinished) {
        this.totalMatches = totalMatches;
        this.finishedMatches = finishedMatches;
        this.displayEndGroupStageButton = !groupStageIsFinished && totalMatches == finishedMatches;
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
}
