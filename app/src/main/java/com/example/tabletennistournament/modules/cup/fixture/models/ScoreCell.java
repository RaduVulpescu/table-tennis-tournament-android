package com.example.tabletennistournament.modules.cup.fixture.models;

import androidx.annotation.Nullable;

public class ScoreCell extends Cell {

    ScoreData data;

    public ScoreCell(@Nullable ScoreData data) {
        super(data);

        this.data = (ScoreData) data;
    }

    public void setScores(int playerOneScore, int playerTwoScore) {
        this.data.playerOneScore = playerOneScore;
        this.data.playerTwoScore = playerTwoScore;
    }

    public String getPlayerOneName() {
        return data.playerOne;
    }

    public String getPlayerTwoName() {
        return data.playerTwo;
    }
}
