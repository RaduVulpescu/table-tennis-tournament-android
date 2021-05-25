package com.example.tabletennistournament.modules.players;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tabletennistournament.MainActivity;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.models.PlayerModel;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

import static com.example.tabletennistournament.services.Common.getPlayerLevelIcon;

public class PlayerProfileActivity extends AppCompatActivity {

    Gson gson;
    RequestQueueSingleton requestQueue;
    String playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_profile);

        gson = new Gson();
        requestQueue = RequestQueueSingleton.getInstance(this);

        Intent intent = getIntent();
        playerId = intent.getStringExtra(PlayersActivity.EXTRA_PLAYER_ID);

        getPlayerProfile();
    }

    public void navigateToUpcomingFixturesActivity(@NonNull MenuItem item) {
        Intent intent = new Intent(this, PlayersActivity.class);
        startActivity(intent);
    }

    public void navigateToMainActivity(@NonNull MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickShowStats(View view) {
    }

    public void onClickShowMatches(View view) {
    }

    private void getPlayerProfile() {
        CircularProgressIndicator progressIndicator = findViewById(R.id.circular_progress_indicator_player_profile);
        ScrollView scrollView = findViewById(R.id.scroll_view_player_profile);

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, String.format("%s/%s", ApiRoutes.PLAYERS_ROUTE, playerId), null,
                response -> {
                    PlayerModel player = gson.fromJson(response.toString(), PlayerModel.class);
                    populateStatsValues(player);
                    progressIndicator.hide();
                    scrollView.setVisibility(View.VISIBLE);
                },
                error -> {
                    progressIndicator.hide();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void populateStatsValues(@NonNull PlayerModel player) {
        TextView currentRankingTextView = findViewById(R.id.text_view_player_profile_current_ranking);
        TextView playerNameTextView = findViewById(R.id.text_view_player_profile_name);
        ImageView playerLevelImageView = findViewById(R.id.image_view_player_profile_level);

        TextView bestScoreTextView = findViewById(R.id.text_view_player_profile_best_score);
        TextView bestRankingTextView = findViewById(R.id.text_view_player_profile_best_ranking);
        TextView bestTop4TextView = findViewById(R.id.text_view_player_profile_best_top4);
        TextView bestLevelTextView = findViewById(R.id.text_view_player_profile_best_level);

        TextView beginnerCupsTextView = findViewById(R.id.text_view_player_profile_beginner_cups);
        TextView intermediateCupsTextView = findViewById(R.id.text_view_player_profile_intermediate_cups);
        TextView advancedCupsTextView = findViewById(R.id.text_view_player_profile_advanced_cups);
        TextView openCupsTextView = findViewById(R.id.text_view_player_profile_open_cups);

        TextView beginnerSeasonsTextView = findViewById(R.id.text_view_player_profile_beginner_seasons);
        TextView intermediateSeasonsTextView = findViewById(R.id.text_view_player_profile_intermediate_seasons);
        TextView advancedSeasonsTextView = findViewById(R.id.text_view_player_profile_advanced_seasons);
        TextView openSeasonsTextView = findViewById(R.id.text_view_player_profile_open_seasons);

        playerNameTextView.setText(player.Name);
        playerLevelImageView.setImageResource(getPlayerLevelIcon(player.CurrentLevel));

        bestScoreTextView.setText(getValueOrNa(String.valueOf(player.BestScore)));
        bestRankingTextView.setText(getValueOrNa(String.valueOf(player.BestRanking)));
        bestTop4TextView.setText(getValueOrNa(String.valueOf(player.BestTop4)));
        bestLevelTextView.setText(getValueOrNa(String.valueOf(player.BestLevel)));

        beginnerCupsTextView.setText(String.valueOf(player.BeginnerCups));
        intermediateCupsTextView.setText(String.valueOf(player.IntermediateCups));
        advancedCupsTextView.setText(String.valueOf(player.AdvancedCups));
        openCupsTextView.setText(String.valueOf(player.OpenCups));

        beginnerSeasonsTextView.setText(String.valueOf(player.BeginnerSeasons));
        intermediateSeasonsTextView.setText(String.valueOf(player.IntermediateSeasons));
        advancedSeasonsTextView.setText(String.valueOf(player.AdvancedSeasons));
        openSeasonsTextView.setText(String.valueOf(player.OpenSeasons));
    }

    public String getValueOrNa(@NonNull String string) {
        if (string.equals("null")) {
            return "N/A";
        }

        return string;
    }
}
