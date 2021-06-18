package com.example.tabletennistournament.modules.cup.fixture.group.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FixtureViewModel extends ViewModel {
    private final MutableLiveData<FixtureGroupState> fixtureGroupState = new MutableLiveData<>();

    public LiveData<FixtureGroupState> getFixtureGroupState() {
        return fixtureGroupState;
    }

    public void setFixtureGroup(int totalMatches, int finishedMatches, boolean groupStageIsFinished) {
        fixtureGroupState.setValue(new FixtureGroupState(totalMatches, finishedMatches, groupStageIsFinished));
    }

    public void incrementFinishedMatches() {
        FixtureGroupState currentValue = fixtureGroupState.getValue();
        fixtureGroupState.setValue(new FixtureGroupState(currentValue.getTotalMatches(),
                currentValue.getFinishedMatches() + 1, false));
    }
}
