package com.example.tabletennistournament.models;

import java.util.Date;
import java.util.UUID;

public class SeasonModel {
    public UUID SeasonId;
    public int Number;
    public Date StartDate;
    public Date EndDate;

    public SeasonModel() { }

    public int getNumber() {
        return Number;
    }
}
