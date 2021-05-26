package com.example.tabletennistournament.modules.upcoming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.tabletennistournament.MainActivity;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.models.FixtureModel;
import com.example.tabletennistournament.modules.players.PlayersActivity;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class NextFixturesActivity extends AppCompatActivity {

    Gson gson;
    RequestQueueSingleton requestQueue;

    CircularProgressIndicator progressIndicator;
    TextView serverErrorTextView;
    Button reloadButton;
    String currentSeasonId;

    public static final String EXTRA_CURRENT_SEASON_ID = "EXTRA_CURRENT_SEASON_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_fixtures);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        currentSeasonId = sharedPref.getString(getString(R.string.current_season_id), null);

        gson = new Gson();
        requestQueue = RequestQueueSingleton.getInstance(this);

        progressIndicator = findViewById(R.id.circular_progress_indicator_upcoming);
        serverErrorTextView = findViewById(R.id.text_view_server_error_upcoming);
        reloadButton = findViewById(R.id.button_reload_upcoming);

        setBottomNavigationBar();
        getFixtures(null);
    }

    public void onClickAddFixture(View view) {
        Intent intent = new Intent(this, AddFixtureActivity.class);
        intent.putExtra(EXTRA_CURRENT_SEASON_ID, currentSeasonId);
        startActivity(intent);
    }

    public void getFixtures(View view) {

//        if (currentSeasonId == null) {
//            currentSeasonId = getCurrentSeasonId();
//        }

        reloadButton.setVisibility(View.GONE);
        serverErrorTextView.setVisibility(View.GONE);
        progressIndicator.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ApiRoutes.FIXTURES_ROUTE(currentSeasonId), null,
                response -> {
                    List<FixtureModel> fixtures = gson.fromJson(response.toString(), new TypeToken<List<FixtureModel>>() {
                    }.getType());
                    //fixtures.sort(Comparator.comparing(FixtureModel::getNumber).reversed());

                    progressIndicator.hide();
                },
                error -> {
                    reloadButton.setVisibility(View.VISIBLE);
                    serverErrorTextView.setVisibility(View.VISIBLE);
                    progressIndicator.hide();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    @NonNull
    private String getCurrentSeasonId() {
        // TODO: complete
        return "";
    }

    @SuppressLint("NonConstantResourceId")
    private void setBottomNavigationBar() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_upcoming);
        bottomNavigationView.setSelectedItemId(R.id.navigation_button_upcoming);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_button_upcoming: {
                    return true;
                }
                case R.id.navigation_button_ranking: {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                case R.id.navigation_button_players: {
                    Intent intent = new Intent(this, PlayersActivity.class);
                    startActivity(intent);
                    return true;
                }
                default: {
                    return false;
                }
            }
        });
    }
}