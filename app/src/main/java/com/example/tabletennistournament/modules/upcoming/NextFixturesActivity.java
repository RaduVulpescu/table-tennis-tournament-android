package com.example.tabletennistournament.modules.upcoming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.tabletennistournament.MainActivity;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.models.FixtureModel;
import com.example.tabletennistournament.models.FixturePlayer;
import com.example.tabletennistournament.models.PlayerModel;
import com.example.tabletennistournament.modules.players.PlayersActivity;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.tabletennistournament.services.Common.increaseTimeout;

public class NextFixturesActivity extends AppCompatActivity {

    Gson gson;
    RequestQueueSingleton requestQueue;

    CircularProgressIndicator progressIndicator;
    TextView serverErrorTextView;
    Button reloadButton;

    String currentSeasonId;
    List<PlayerModel> allPlayers = null;

    public static final String EXTRA_CURRENT_SEASON_ID = "EXTRA_CURRENT_SEASON_ID";
    public static final String EXTRA_FIXTURE_ID = "EXTRA_FIXTURE_ID";

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
                    fixtures.sort(Comparator.comparing(FixtureModel::getNumber));

                    progressIndicator.hide();
                    createFixturesRecyclerView(fixtures);
                },
                error -> {
                    reloadButton.setVisibility(View.VISIBLE);
                    serverErrorTextView.setVisibility(View.VISIBLE);
                    progressIndicator.hide();
                }
        );

        requestQueue.add(increaseTimeout(jsonArrayRequest));

        getAllPlayers();
    }

    private void createFixturesRecyclerView(List<FixtureModel> fixtures) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_upcoming_fixtures);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.Adapter<RecyclerView.ViewHolder> fixturesListAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return UpcomingFixtureListItemViewHolder.create(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
                bind((UpcomingFixtureListItemViewHolder) viewHolder, position);
            }

            @Override
            public int getItemCount() {
                return fixtures.size();
            }

            private void bind(@NonNull UpcomingFixtureListItemViewHolder vh, int position) {
                FixtureModel fixture = fixtures.get(position);

                vh.fixtureDate.setText(extractDate(fixture.Date));
                vh.fixtureTime.setText(extractTime(fixture.Date));
                vh.fixtureLocation.setText(extractLocation(fixture.Location));
                vh.fixtureQualityAvg.setText(String.format(Locale.getDefault(), "QAvg: %.2f", fixture.QualityAverage));

                inflateRecyclerViewPlayers(vh.recyclerViewPlayers, fixture.Players);

                vh.expandButton.setOnClickListener(v -> {
                    v.setVisibility(View.GONE);
                    vh.linearLayoutUpcomingFixturePlayers.setVisibility(View.VISIBLE);
                    vh.collapseButton.setVisibility(View.VISIBLE);
                });

                vh.collapseButton.setOnClickListener(v -> {
                    v.setVisibility(View.GONE);
                    vh.linearLayoutUpcomingFixturePlayers.setVisibility(View.GONE);
                    vh.expandButton.setVisibility(View.VISIBLE);
                });

                vh.startFixtureButton.setOnClickListener(v -> {

                });

                vh.editFixtureButton.setOnClickListener(v -> {
                    Intent intent = new Intent(getBaseContext(), EditFixtureActivity.class);
                    intent.putExtra(EXTRA_CURRENT_SEASON_ID, currentSeasonId);
                    intent.putExtra(EXTRA_FIXTURE_ID, fixture.FixtureId.toString());
                    startActivity(intent);
                });

                vh.deleteFixtureButton.setOnClickListener(v -> {

                });
            }
        };

        recyclerView.setAdapter(fixturesListAdapter);
    }

    private void inflateRecyclerViewPlayers(@NonNull RecyclerView recyclerViewPlayers, @NonNull List<FixturePlayer> players) {
        recyclerViewPlayers.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.Adapter<RecyclerView.ViewHolder> fixturePlayerListAdapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return UpcomingFixturePlayerListItemViewHolder.create(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
                bind((UpcomingFixturePlayerListItemViewHolder) viewHolder, position);
            }

            @Override
            public int getItemCount() {
                return players.size();
            }

            private void bind(@NonNull UpcomingFixturePlayerListItemViewHolder vh, int position) {
                FixturePlayer player = players.get(position);

                vh.playerName.setText(player.Name);
                vh.playerQuality.setText(String.format(Locale.getDefault(), "%.2f", player.Quality));
            }
        };

        recyclerViewPlayers.setAdapter(fixturePlayerListAdapter);
    }

    private void getAllPlayers() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ApiRoutes.PLAYERS_ROUTE, null,
                response -> {
                    List<PlayerModel> players = gson.fromJson(response.toString(), new TypeToken<List<PlayerModel>>() {
                    }.getType());
                    players.sort(Comparator.comparing(PlayerModel::getName));

                    allPlayers = players;
                },
                error -> Log.e("REQUEST-GET-PLAYERS_ROUTE", gson.toJson(error))
        );

        requestQueue.add(increaseTimeout(jsonArrayRequest));
    }

    @NonNull
    private String extractDate(Date date) {
        if (date == null) {
            return "TBA";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        return formatter.format(date);
    }

    @NonNull
    private String extractTime(Date date) {
        if (date == null) {
            return "TBA";
        }

        return formatTime(date.getHours(), date.getMinutes());
    }


    @NonNull
    private String formatTime(int hour, int minute) {
        String hourAsText;
        String minuteAsText;

        if (hour < 10) {
            hourAsText = String.format(Locale.getDefault(), "0%d", hour);
        } else {
            hourAsText = String.valueOf(hour);
        }

        if (minute < 10) {
            minuteAsText = String.format(Locale.getDefault(), "0%d", minute);
        } else {
            minuteAsText = String.valueOf(minute);
        }

        return String.format("%s:%s", hourAsText, minuteAsText);
    }

    @NonNull
    private String extractLocation(String location) {
        if (location == null) {
            return "Location: TBA";
        }

        return String.format("Location: %s", location);
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