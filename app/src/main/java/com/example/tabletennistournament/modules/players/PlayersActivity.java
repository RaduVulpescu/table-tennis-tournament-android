package com.example.tabletennistournament.modules.players;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tabletennistournament.MainActivity;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.models.PlayerModel;
import com.example.tabletennistournament.services.ApiRoutes;
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
    RequestQueue requestQueue;

    public static final String EXTRA_PLAYER_ID = "EXTRA_PLAYER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);

        showPlayerAddedSnackbar();

        gson = new Gson();
        requestQueue = Volley.newRequestQueue(this);

        initiatePlayersView();
    }

    public void onClickAddPlayer(View view) {
        Intent intent = new Intent(this, AddPlayerActivity.class);
        startActivity(intent);
    }

    public void navigateToUpcomingFixturesActivity(@NonNull MenuItem item) {
        Intent intent = new Intent(this, PlayersActivity.class);
        startActivity(intent);
    }

    public void navigateToMainActivity(@NonNull MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
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
                vh.profilePicture.setImageResource(R.drawable.logo_avatar_anonymous_40dp);
                vh.playerName.setText(players.get(position).Name);
                vh.playerLevelIcon.setImageResource(getPlayerLevelIcon(players.get(position).CurrentLevel));
                vh.playerQuality.setText(getQualityOrDefault(players.get(position).Quality));
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

