package com.example.tabletennistournament.services;

import androidx.annotation.NonNull;

public class ApiRoutes {
    private static final String API_BASE_URL = "https://w5p1w5sojb.execute-api.eu-west-1.amazonaws.com";

    public static final String PLAYERS_ROUTE = String.format("%s/players", API_BASE_URL);
    public static final String SEASONS_ROUTE = String.format("%s/seasons", API_BASE_URL);
    public static final String REGISTER_DEVICE_ROUTE = String.format("%s/aws/platformApplication/endpoints", API_BASE_URL);

    @NonNull
    public static String FIXTURES_ROUTE(@NonNull String seasonId) {
        return String.format("%s/%s/fixtures", SEASONS_ROUTE, seasonId);
    }

    @NonNull
    public static String START_FIXTURE_ROUTE(@NonNull String seasonId, @NonNull String fixtureId) {
        return String.format("%s/%s/fixtures/%s/start", SEASONS_ROUTE, seasonId, fixtureId);
    }

    @NonNull
    public static String PATCH_FIXTURE_GROUP_MATCH_ROUTE(@NonNull String seasonId, @NonNull String fixtureId, @NonNull String matchId) {
        return String.format("%s/%s/fixtures/%s/groupMatches/%s", SEASONS_ROUTE, seasonId, fixtureId, matchId);
    }

    @NonNull
    public static String END_GROUP_STAGE_ROUTE(@NonNull String seasonId, @NonNull String fixtureId) {
        return String.format("%s/%s/fixtures/%s/endGroupStage", SEASONS_ROUTE, seasonId, fixtureId);
    }

    @NonNull
    public static String PATCH_FIXTURE_DECIDER_MATCH_ROUTE(@NonNull String seasonId, @NonNull String fixtureId, @NonNull String matchId) {
        return String.format("%s/%s/fixtures/%s/deciderMatches/%s", SEASONS_ROUTE, seasonId, fixtureId, matchId);
    }
    @NonNull
    public static String END_FIXTURE_ROUTE(@NonNull String seasonId, @NonNull String fixtureId) {
        return String.format("%s/%s/fixtures/%s/endFixture", SEASONS_ROUTE, seasonId, fixtureId);
    }
}