package com.example.tabletennistournament.modules.cup.fixture.viewModels;

public class FixtureGroupState {
    private final int totalMatches;
    private final int finishedMatches;
    private final boolean isGroupStageComplete;

    FixtureGroupState(int totalMatches, int finishedMatches) {
        this.totalMatches = totalMatches;
        this.finishedMatches = finishedMatches;
        this.isGroupStageComplete = totalMatches == finishedMatches;
    }

    public boolean isGroupStageComplete() {
        return isGroupStageComplete;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public int getFinishedMatches() {
        return finishedMatches;
    }
}
