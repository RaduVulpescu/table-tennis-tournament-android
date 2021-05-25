package com.example.tabletennistournament.services;

public class ApiRoutes {
    private static final String API_BASE_URL = "https://68a1rvei12.execute-api.eu-west-1.amazonaws.com";

    public static final String PLAYERS_ROUTE = String.format("%s/players", API_BASE_URL);
    public static final String SEASONS_ROUTE = String.format("%s/seasons", API_BASE_URL);
}