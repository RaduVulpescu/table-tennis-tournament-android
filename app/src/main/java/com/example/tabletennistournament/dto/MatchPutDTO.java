package com.example.tabletennistournament.dto;

public class MatchPutDTO {

    public int SetsWonByPlayerOne;
    public int SetsWonByPlayerTwo;

    public MatchPutDTO(int setsWonByPlayerOne, int setsWonByPlayerTwo) {
        SetsWonByPlayerOne = setsWonByPlayerOne;
        SetsWonByPlayerTwo = setsWonByPlayerTwo;
    }
}
