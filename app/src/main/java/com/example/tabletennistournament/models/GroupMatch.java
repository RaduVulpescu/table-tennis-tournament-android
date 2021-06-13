package com.example.tabletennistournament.models;

import com.example.tabletennistournament.enums.Group;

import java.util.UUID;

public class GroupMatch {
    public UUID MatchId;
    public Group Group;
    public PlayerMatchStats PlayerOneStats;
    public PlayerMatchStats PlayerTwoStats;
}
