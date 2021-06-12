package com.example.tabletennistournament.models;

import com.example.tabletennistournament.enums.Level;

import java.util.UUID;

public class SeasonPlayerModel {
    public UUID SeasonId;
    public UUID PlayerId;

    public int Rank;
    public String Name;
    public Level Level;
    public double Quality;
    public double Top4;
    public double Score1;
    public double Score2;
    public double Score3;
    public double Score4;
    public double Shape;

    public SeasonPlayerModel() {
    }

    public int getRank() {
        return Rank;
    }
}
