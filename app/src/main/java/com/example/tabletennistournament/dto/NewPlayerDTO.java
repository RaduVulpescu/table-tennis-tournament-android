package com.example.tabletennistournament.dto;

import com.example.tabletennistournament.enums.Level;

public class NewPlayerDTO {

    public String Name;
    public String City;
    public Integer BirthYear;
    public Integer Height;
    public Integer Weight;
    public Level CurrentLevel;

    public NewPlayerDTO(String name, String city, Integer birthYear, Integer height, Integer weight, Level currentLevel) {
        Name = name;
        City = city;
        BirthYear = birthYear;
        Height = height;
        Weight = weight;
        CurrentLevel = currentLevel;
    }

}
