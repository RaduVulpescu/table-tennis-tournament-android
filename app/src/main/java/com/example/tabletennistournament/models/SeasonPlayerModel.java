package com.example.tabletennistournament.models;

import com.example.tabletennistournament.enums.Level;

import java.util.UUID;

public class SeasonPlayerModel {
    public UUID seasonId;
    public UUID playerId;

    public int rank;
    public String name;
    public Level level;
    public double quality;
    public double top4;
    public double score1;
    public double score2;
    public double score3;
    public double score4;
    public double shape;

    public SeasonPlayerModel() {
    }

    public int getRank() {
        return rank;
    }
}
