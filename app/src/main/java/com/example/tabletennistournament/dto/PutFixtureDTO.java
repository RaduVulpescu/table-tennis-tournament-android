package com.example.tabletennistournament.dto;

import com.example.tabletennistournament.models.FixturePlayer;

import java.time.ZonedDateTime;
import java.util.List;

public class PutFixtureDTO {

    public String Location;
    public ZonedDateTime Date;
    List<FixturePlayer> Players;

    public PutFixtureDTO(String location, ZonedDateTime date, List<FixturePlayer> fixturePlayers) {
        Location = location;
        Date = date;
        Players = fixturePlayers;
    }

}
