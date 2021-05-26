package com.example.tabletennistournament.dto;

import com.example.tabletennistournament.models.FixturePlayer;

import java.util.Date;
import java.util.List;

public class PutFixtureDTO {

    public String Location;
    public Date Date;
    List<FixturePlayer> Players;

    public PutFixtureDTO(String location, Date date, List<FixturePlayer> fixturePlayers) {
        Location = location;
        Date = date;
        Players = fixturePlayers;
    }

}
