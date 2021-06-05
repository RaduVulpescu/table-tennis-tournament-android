package com.example.tabletennistournament.models;

import com.example.tabletennistournament.enums.FixtureState;
import com.example.tabletennistournament.enums.FixtureType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class FixtureModel {

    public UUID SeasonId;
    public UUID FixtureId;
    public ZonedDateTime Date;
    public String Location;
    public double QualityAverage;
    public FixtureState State;
    public FixtureType Type;
    public List<FixturePlayer> Players;

    public FixtureModel() { }

    public ZonedDateTime getDate() {
        return Date;
    }
}

