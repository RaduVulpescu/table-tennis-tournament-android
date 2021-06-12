package com.example.tabletennistournament.models;

import com.example.tabletennistournament.enums.FixtureState;
import com.example.tabletennistournament.enums.FixtureType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class FixtureModel {

    public UUID seasonId;
    public UUID fixtureId;
    public ZonedDateTime date;
    public String location;
    public double qualityAverage;
    public FixtureState state;
    public FixtureType type;
    public List<FixturePlayer> players;

    public FixtureModel() { }

    public ZonedDateTime getDate() {
        return date;
    }
}

