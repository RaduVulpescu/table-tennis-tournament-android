package com.example.tabletennistournament.modules.cup.fixture.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FixtureViewModel extends ViewModel {
    private final MutableLiveData<FixtureGroupState> fixtureGroupState = new MutableLiveData<>();

    public LiveData<FixtureGroupState> getFixtureGroupState() {
        return fixtureGroupState;
    }

    public void setFixtureGroup(int totalMatches, int finishedMatches) {
        fixtureGroupState.setValue(new FixtureGroupState(totalMatches, finishedMatches));
    }

    public void incrementFinishedMatches() {
        FixtureGroupState currentValue = fixtureGroupState.getValue();
        fixtureGroupState.setValue(new FixtureGroupState(currentValue.getTotalMatches(),
                currentValue.getFinishedMatches() + 1));
    }
}
