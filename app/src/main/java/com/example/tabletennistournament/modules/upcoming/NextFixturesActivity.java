package com.example.tabletennistournament.modules.upcoming;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.enums.FixtureType;
import com.example.tabletennistournament.models.FixtureModel;
import com.example.tabletennistournament.models.FixturePlayer;
import com.example.tabletennistournament.modules.cup.CupActivity;
import com.example.tabletennistournament.modules.players.PlayersActivity;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.GsonSingleton;
import com.example.tabletennistournament.services.LoginRepository;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.example.tabletennistournament.services.Common.getValueOrNA;
import static com.example.tabletennistournament.services.Common.increaseTimeout;

public class NextFixturesActivity extends AppCompatActivity {

    Gson gson;
    RequestQueueSingleton requestQueue;
    LoginRepository loginRepository;

    LinearProgressIndicator linearProgressIndicator;
    CircularProgressIndicator progressIndicator;
    TextView serverErrorTextView;
    Button reloadButton;

    String currentSeasonId;

    public static final String EXTRA_CURRENT_SEASON_ID = "EXTRA_CURRENT_SEASON_ID";
    public static final String EXTRA_FIXTURE_JSON = "EXTRA_FIXTURE_JSON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_fixtures);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        currentSeasonId = sharedPref.getString(getString(R.string.current_season_id), null);

        gson = GsonSingleton.getInstance();
        requestQueue = RequestQueueSingleton.getInstance(this);
        loginRepository = LoginRepository.getInstance();

        linearProgressIndicator = findViewById(R.id.linear_progress_indicator_start_fixture);
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

        FloatingActionButton addFixtureButton = findViewById(R.id.floating_action_button_add_fixture);
        reloadButton.setVisibility(View.GONE);
        serverErrorTextView.setVisibility(View.GONE);
        progressIndicator.show();

        String url = ApiRoutes.FIXTURES_ROUTE(currentSeasonId) + "?state=0";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<FixtureModel> fixtures = gson.fromJson(response.toString(), new TypeToken<List<FixtureModel>>() {
                    }.getType());

                    if (fixtures.size() == 0) {
                        findViewById(R.id.text_view_no_fixture_planned).setVisibility(View.VISIBLE);
                    }

                    boolean allFixturesHaveDateDefined = true;

                    for (int i = 0; i < fixtures.size() && allFixturesHaveDateDefined; i++) {
                        if (fixtures.get(i).Date == null) {
                            allFixturesHaveDateDefined = false;
                        }
                    }

                    if (allFixturesHaveDateDefined) {
                        fixtures.sort(Comparator.comparing(FixtureModel::getDate));
                    }

                    progressIndicator.hide();

                    if (loginRepository.isLoggedIn()) {
                        addFixtureButton.show();
                    }

                    createFixturesRecyclerView(fixtures);
                },
                error -> {
                    reloadButton.setVisibility(View.VISIBLE);
                    serverErrorTextView.setVisibility(View.VISIBLE);
                    progressIndicator.hide();
                }
        );

        requestQueue.add(increaseTimeout(jsonArrayRequest));
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

                if (fixture.Type != FixtureType.Normal) {
                    vh.fixtureType.setText(String.format("%s Final", fixture.Type));
                    vh.fixtureType.setVisibility(View.VISIBLE);
                }

                fixture.Players.sort(Comparator.comparing(FixturePlayer::getQuality).reversed());
                inflateRecyclerViewPlayers(vh.recyclerViewPlayers, fixture.Players);

                if (fixture.Players.size() == 0) {
                    vh.expandButton.setVisibility(View.GONE);
                }

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

                if (loginRepository.isLoggedIn()) {
                    vh.upcomingButtonsDelimiter.setVisibility(View.VISIBLE);
                    vh.upcomingButtonsLinearLayout.setVisibility(View.VISIBLE);

                    if (fixture.Location == null || fixture.Date == null || fixture.Players.size() > 2) {
                        vh.startFixtureButton.setVisibility(View.GONE);
                    } else {
                        vh.startFixtureButton.setOnClickListener(v -> startFixture(vh, fixture));
                    }

                    vh.editFixtureButton.setOnClickListener(v -> {
                        Intent intent = new Intent(getBaseContext(), EditFixtureActivity.class);
                        intent.putExtra(EXTRA_CURRENT_SEASON_ID, currentSeasonId);
                        intent.putExtra(EXTRA_FIXTURE_JSON, gson.toJson(fixture));

                        startActivity(intent);
                    });

                    vh.deleteFixtureButton.setOnClickListener(v -> {

                    });
                }
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
                vh.playerQuality.setText(String.format("Q: %s", getValueOrNA(player.Quality)));
            }
        };

        recyclerViewPlayers.setAdapter(fixturePlayerListAdapter);
    }

    private void startFixture(UpcomingFixtureListItemViewHolder vh, @NonNull FixtureModel fixture) {
        String url = ApiRoutes.START_FIXTURE_ROUTE(fixture.SeasonId.toString(), fixture.FixtureId.toString());
        linearProgressIndicator.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {
                    vh.setVisibilityOnGone();
                    linearProgressIndicator.hide();

                    Snackbar snackbar = Snackbar.make(findViewById(R.id.recycler_view_upcoming_fixtures), "Fixture started!", Snackbar.LENGTH_LONG);
                    snackbar.setAnchorView(findViewById(R.id.bottom_navigation_upcoming));
                    snackbar.show();
                },
                error -> {
                    linearProgressIndicator.hide();

                    Snackbar snackbar = Snackbar.make(findViewById(R.id.recycler_view_upcoming_fixtures), R.string.server_error, Snackbar.LENGTH_LONG);
                    snackbar.setAnchorView(findViewById(R.id.bottom_navigation_upcoming));
                    snackbar.show();
                }
        ) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(@NonNull NetworkResponse response) {
                try {
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

                    JSONObject result = null;
                    if (jsonString != null && jsonString.length() > 0)
                        result = new JSONObject(jsonString);

                    return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        requestQueue.add(increaseTimeout(jsonObjectRequest));
    }

    @NonNull
    private String extractDate(ZonedDateTime date) {
        if (date == null) {
            return "TBA";
        }

        return date.format(DateTimeFormatter.ofPattern("dd MMMM"));
    }

    @NonNull
    private String extractTime(ZonedDateTime date) {
        if (date == null) {
            return "TBA";
        }

        return formatTime(date.getHour(), date.getMinute());
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
                    Intent intent = new Intent(this, CupActivity.class);
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