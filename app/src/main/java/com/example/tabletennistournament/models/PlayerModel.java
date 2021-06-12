package com.example.tabletennistournament.models;

import com.example.tabletennistournament.enums.Level;

import java.util.UUID;

public class PlayerModel {
    public UUID playerId;
    public String name;
    public String city;
    public Integer birthYear;
    public Integer height;
    public Integer weight;

    public Double quality;
    public Level currentLevel;

    public Double bestScore;
    public Integer bestRanking;
    public Double bestTop4;
    public Level bestLevel;

    public int openCups;
    public int advancedCups;
    public int intermediateCups;
    public int beginnerCups;

    public int openSeasons;
    public int advancedSeasons;
    public int intermediateSeasons;
    public int beginnerSeasons;

    public PlayerModel() { }

    public PlayerModel(UUID playerId, String name, Double quality) {
        this.playerId = playerId;
        this.name = name;
        this.quality = quality;
    }

    public String getName() {
        return name;
    }
}
