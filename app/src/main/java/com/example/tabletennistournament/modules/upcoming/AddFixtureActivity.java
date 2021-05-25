package com.example.tabletennistournament.modules.upcoming;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tabletennistournament.MainActivity;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.models.FixtureModel;
import com.example.tabletennistournament.modules.players.PlayersActivity;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFixtureActivity extends AppCompatActivity {

    public static final String EXTRA_FIXTURE_ADDED = "EXTRA_FIXTURE_ADDED";

    Gson gson;
    RequestQueueSingleton requestQueue;

    TextInputLayout locationTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fixture);

        gson = new Gson();
        requestQueue = RequestQueueSingleton.getInstance(this);

        locationTextInputLayout = findViewById(R.id.text_layout_add_fixture_location);

        setBottomNavigationBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_fixture, menu);
        return true;
    }

    public void onClickSaveFixture(MenuItem item) throws JSONException {
        FixtureModel newFixture = new FixtureModel();

        LinearProgressIndicator progressIndicator = findViewById(R.id.linear_progress_indicator_add_fixture);
        progressIndicator.show();
        setUserInputEnabled(false);

        Intent currentIntent = getIntent();
        String currentSeasonId = currentIntent.getStringExtra(NextFixturesActivity.EXTRA_CURRENT_SEASON_ID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiRoutes.FIXTURES_ROUTE(currentSeasonId), new JSONObject(gson.toJson(newFixture)),
                response -> {
                    Intent intent = new Intent(this, NextFixturesActivity.class);
                    intent.putExtra(EXTRA_FIXTURE_ADDED, true);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    progressIndicator.hide();
                    setUserInputEnabled(true);

                    Snackbar snackbar = Snackbar.make(findViewById(R.id.constraint_layout_add_fixture), R.string.server_error, Snackbar.LENGTH_LONG);
                    snackbar.setAnchorView(findViewById(R.id.bottom_navigation_add_fixture));
                    snackbar.show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    @SuppressLint("NonConstantResourceId")
    private void setBottomNavigationBar() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_add_fixture);
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

    private void setUserInputEnabled(boolean enabled) {
        locationTextInputLayout.setEnabled(enabled);
    }
}
