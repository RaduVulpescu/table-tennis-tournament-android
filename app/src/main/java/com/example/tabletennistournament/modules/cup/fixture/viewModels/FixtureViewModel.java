package com.example.tabletennistournament.modules.cup.fixture.viewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tabletennistournament.models.FixtureModel;

public class FixtureViewModel extends ViewModel {
    private final MutableLiveData<FixtureData> fixtureData = new MutableLiveData<>();
    private final MutableLiveData<FixtureGroupState> fixtureGroupState = new MutableLiveData<>();

    public LiveData<FixtureGroupState> getFixtureGroupState() {
        return fixtureGroupState;
    }

    public LiveData<FixtureData> getFixtureData() {
        return fixtureData;
    }

    public void setFixtureData(@NonNull FixtureModel fixture) {
        fixtureData.setValue(new FixtureData(fixture.SeasonId.toString(), fixture.FixtureId.toString(), fixture.Players));
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
