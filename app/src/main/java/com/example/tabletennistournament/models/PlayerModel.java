package com.example.tabletennistournament.models;

import com.example.tabletennistournament.enums.Level;

import java.util.UUID;

public class PlayerModel {
    public UUID PlayerId;
    public String Name;
    public String City;
    public Integer BirthYear;
    public Integer Height;
    public Integer Weight;

    public Double Quality;
    public Level CurrentLevel;

    public Double BestScore;
    public Integer BestRanking;
    public Double BestTop4;
    public Level BestLevel;

    public int OpenCups;
    public int AdvancedCups;
    public int IntermediateCups;
    public int BeginnerCups;

    public int OpenSeasons;
    public int AdvancedSeasons;
    public int IntermediateSeasons;
    public int BeginnerSeasons;

    public PlayerModel() { }

    public PlayerModel(UUID playerId, String name, Double quality) {
        this.PlayerId = playerId;
        this.Name = name;
        this.Quality = quality;
    }

    public String getName() {
        return Name;
    }
}
