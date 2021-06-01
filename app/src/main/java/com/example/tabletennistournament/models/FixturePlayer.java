package com.example.tabletennistournament.models;

import java.util.UUID;

public class FixturePlayer {
    public UUID PlayerId;
    public String Name;
    public Double Quality;

    public FixturePlayer() { }

    public FixturePlayer(UUID playerId, String name, Double quality) {
        PlayerId = playerId;
        Name = name;
        Quality = quality;
    }

    public Double getQuality() {
        return Quality;
    }
}
