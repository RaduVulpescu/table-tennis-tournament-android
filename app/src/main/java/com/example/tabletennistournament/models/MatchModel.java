package com.example.tabletennistournament.models;

import java.util.UUID;

public class MatchModel {
    public UUID MatchId;
    public PlayerMatchStats PlayerOneStats;
    public PlayerMatchStats PlayerTwoStats;

    public MatchModel() {
    }
}
