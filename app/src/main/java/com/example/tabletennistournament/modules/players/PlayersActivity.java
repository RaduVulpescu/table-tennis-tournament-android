package com.example.tabletennistournament.modules.players;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.tabletennistournament.MainActivity;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.models.PlayerModel;
import com.example.tabletennistournament.modules.upcoming.NextFixturesActivity;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.example.tabletennistournament.services.Common.getPlayerLevelIcon;

public class PlayersActivity extends AppCompatActivity {

    Gson gson;
    RequestQueueSingleton requestQueue;
    BottomNavigationView bottomNavigationView;

    public static final String EXTRA_PLAYER_ID = "EXTRA_PLAYER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        showPlayerAddedSnackbar();

        gson = new Gson();
        requestQueue = RequestQueueSingleton.getInstance(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation_players);

        setBottomNavigationBar();
        initiatePlayersView();
    }

    public void onClickAddPlayer(View view) {
        Intent intent = new Intent(this, AddPlayerActivity.class);
        startActivity(intent);
    }

    private void showPlayerAddedSnackbar() {
        Intent intent = getIntent();
        boolean playerAdded = intent.getBooleanExtra(AddPlayerActivity.EXTRA_PLAYER_ADDED, false);

        if (playerAdded) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.recycler_view_player_list), "Player added", Snackbar.LENGTH_LONG);
            snackbar.setAnchorView(findViewById(R.id.floating_action_button_add_player));
            snackbar.show();
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void setBottomNavigationBar() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_button_players);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_button_next_fixtures: {
                    Intent intent = new Intent(this, NextFixturesActivity.class);
                    startActivity(intent);
                    return true;
                }
                case R.id.navigation_button_ranking: {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                case R.id.navigation_button_players: {
                    return true;
                }
                default:{
                    return false;
                }
            }
        });
    }

    private void initiatePlayersView() {
        CircularProgressIndicator progressIndicator = findViewById(R.id.circular_progress_indicator);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ApiRoutes.PLAYERS_ROUTE, null,
                response -> {
                    List<PlayerModel> players = gson.fromJson(response.toString(), new TypeToken<List<PlayerModel>>() {
                    }.getType());
                    players.sort(Comparator.comparing(PlayerModel::getName));
                    createPlayersRecyclerView(players);
                    progressIndicator.hide();
                },
                error -> {
                    TextView serverErrorTextView = findViewById(R.id.text_view_server_error);
                    serverErrorTextView.setText(R.string.server_error);
                    progressIndicator.hide();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void createPlayersRecyclerView(List<PlayerModel> players) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_player_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Adapter<ViewHolder> playerListAdapter = new Adapter<ViewHolder>() {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return PlayerListItemViewHolder.create(parent);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
                bind((PlayerListItemViewHolder) viewHolder, position);

                viewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(PlayersActivity.this, PlayerProfileActivity.class);
                    intent.putExtra(EXTRA_PLAYER_ID, players.get(position).PlayerId.toString());
                    startActivity(intent);
                });
            }

            @Override
            public int getItemCount() {
                return players.size();
            }

            private void bind(@NonNull PlayerListItemViewHolder vh, int position) {
                PlayerModel player = players.get(position);

                vh.profilePicture.setImageResource(R.drawable.logo_avatar_anonymous_40dp);
                vh.playerName.setText(player.Name);
                vh.playerLevelIcon.setImageResource(getPlayerLevelIcon(player.CurrentLevel));
                vh.playerQuality.setText(getQualityOrDefault(player.Quality));
            }
        };

        recyclerView.setAdapter(playerListAdapter);
    }

    @NonNull
    private String getQualityOrDefault(Double quality) {
        if (quality == null) {
            return "N/A";
        }

        return String.format(Locale.getDefault(), "%.2f", quality);
    }

}

