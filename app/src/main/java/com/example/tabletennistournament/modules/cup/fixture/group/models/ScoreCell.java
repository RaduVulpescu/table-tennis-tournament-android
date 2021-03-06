package com.example.tabletennistournament.modules.cup.fixture.group.models;

import androidx.annotation.Nullable;

import java.util.UUID;

public class ScoreCell extends Cell {

    ScoreData data;

    public ScoreCell(@Nullable ScoreData data) {
        super(data);

        this.data = data;
    }

    public void setScores(int playerOneScore, int playerTwoScore) {
        this.data.playerOneScore = playerOneScore;
        this.data.playerTwoScore = playerTwoScore;
    }

    public UUID getMatchId() {
        return data.matchId;
    }

    public String getPlayerOneName() {
        return data.playerOne;
    }

    public String getPlayerTwoName() {
        return data.playerTwo;
    }

    public Integer getPlayerOneScore() {
        return data.playerOneScore;
    }

    public Integer getPlayerTwoScore() {
        return data.playerTwoScore;
    }
}
