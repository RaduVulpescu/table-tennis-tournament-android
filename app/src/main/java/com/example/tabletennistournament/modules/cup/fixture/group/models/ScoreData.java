package com.example.tabletennistournament.modules.cup.fixture.group.models;

import androidx.annotation.NonNull;

import java.util.Locale;
import java.util.UUID;

public class ScoreData {
    UUID matchId;
    String playerOne;
    String playerTwo;
    Integer playerOneScore;
    Integer playerTwoScore;
    boolean displayPlayerOneScoreFirst;

    public ScoreData(UUID matchId, String playerOne, String playerTwo, Integer playerOneScore, Integer playerTwoScore, boolean displayPlayerOneScoreFirst) {
        this.matchId = matchId;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.playerOneScore = playerOneScore;
        this.playerTwoScore = playerTwoScore;
        this.displayPlayerOneScoreFirst = displayPlayerOneScoreFirst;
    }

    @NonNull
    @Override
    public String toString() {
        if (playerOneScore == null && playerTwoScore == null) return "";

        return displayPlayerOneScoreFirst ?
                String.format(Locale.getDefault(), "%d - %d", playerOneScore, playerTwoScore) :
                String.format(Locale.getDefault(), "%d - %d", playerTwoScore, playerOneScore);
    }
}
