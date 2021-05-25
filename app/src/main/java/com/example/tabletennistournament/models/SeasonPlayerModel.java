package com.example.tabletennistournament.models;

import com.example.tabletennistournament.enums.Level;

import java.util.UUID;

public class SeasonPlayerModel {
    UUID SeasonId;
    UUID PlayerId;

    int Rank;
    String Name;
    Level Level;
    double Quality;
    double Top4;
    double Score1;
    double Score2;
    double Score3;
    double Score4;
    double Shape;

    public SeasonPlayerModel() { }

    public int getRank() {
        return Rank;
    }
}
