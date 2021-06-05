package com.example.tabletennistournament.dto;

import java.time.ZonedDateTime;

public class NewFixtureDTO {

    public String Location;
    public ZonedDateTime Date;

    public NewFixtureDTO(String location, ZonedDateTime date) {
        Location = location;
        Date = date;
    }

}
