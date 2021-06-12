package com.example.tabletennistournament.models;

import java.util.UUID;

public class FixturePlayer {
    public UUID playerId;
    public String name;
    public Double quality;

    public FixturePlayer() { }

    public FixturePlayer(UUID playerId, String name, Double quality) {
        this.playerId = playerId;
        this.name = name;
        this.quality = quality;
    }

    public Double getQuality() {
        return quality;
    }
}
