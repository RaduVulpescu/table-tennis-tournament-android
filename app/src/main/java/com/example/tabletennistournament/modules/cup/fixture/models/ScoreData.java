package com.example.tabletennistournament.modules.cup.fixture.models;

import androidx.annotation.NonNull;

import java.util.Locale;

public class ScoreData {
    String playerOne;
    String playerTwo;
    int playerOneScore;
    int playerTwoScore;

    public ScoreData(String playerOne, String playerTwo, int playerOneScore, int playerTwoScore) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.playerOneScore = playerOneScore;
        this.playerTwoScore = playerTwoScore;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%d - %d", playerOneScore, playerTwoScore);
    }
}
