package com.example.tabletennistournament.models;

import java.util.Date;
import java.util.UUID;

public class SeasonModel {
    public UUID seasonId;
    public int number;
    public Date startDate;
    public Date endDate;

    public SeasonModel() { }

    public int getNumber() {
        return number;
    }
}
