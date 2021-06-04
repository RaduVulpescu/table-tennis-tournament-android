package com.example.tabletennistournament.modules.upcoming;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.dto.PutFixtureDTO;
import com.example.tabletennistournament.models.FixtureModel;
import com.example.tabletennistournament.models.FixturePlayer;
import com.example.tabletennistournament.models.PlayerModel;
import com.example.tabletennistournament.modules.cup.CupActivity;
import com.example.tabletennistournament.modules.players.PlayersActivity;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.example.tabletennistournament.services.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.tabletennistournament.services.Common.getValueOrNA;
import static com.example.tabletennistournament.services.Common.increaseTimeout;

public class EditFixtureActivity extends AppCompatActivity {

    Gson gson;
    RequestQueueSingleton requestQueue;

    LinearProgressIndicator progressIndicator;
    TextInputLayout locationTextInputLayout;
    TextInputLayout dateTextInputLayout;
    TextInputLayout timeTextInputLayout;
    TextInputLayout playerTextInputLayout;
    AutoCompleteTextView autoCompleteTextViewPlayer;
    Button addPlayerButton;
    ChipGroup playersChipGroup;
    MaterialDatePicker<Long> datePicker;

    Date selectedDate = null;
    Integer selectedHour = null;
    Integer selectedMinute = null;
    DropDownItem selectedPlayer = null;
    List<DropDownItem> playersDropDown;

    FixtureModel fixtureModel;
    Intent currentIntent;

    ArrayAdapter<DropDownItem> adapter;
    List<FixturePlayer> fixturePlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fixture);

        gson = new Gson();
        requestQueue = RequestQueueSingleton.getInstance(this);

        progressIndicator = findViewById(R.id.linear_progress_indicator_edit_fixture);
        locationTextInputLayout = findViewById(R.id.text_layout_edit_fixture_location);
        dateTextInputLayout = findViewById(R.id.text_layout_edit_fixture_date);
        timeTextInputLayout = findViewById(R.id.text_layout_edit_fixture_time);
        playerTextInputLayout = findViewById(R.id.text_layout_edit_fixture_player);
        autoCompleteTextViewPlayer = findViewById(R.id.auto_complete_text_view_edit_fixture_player);
        addPlayerButton = findViewById(R.id.button_edit_fixture_add_player_button);
        playersChipGroup = findViewById(R.id.chip_group_edit_fixture_players);

        currentIntent = getIntent();
        String fixtureJson = currentIntent.getStringExtra(NextFixturesActivity.EXTRA_FIXTURE_JSON);
        fixtureModel = gson.fromJson(fixtureJson, FixtureModel.class);
        fixturePlayers = fixtureModel.Players;

        getAllPlayers();
        createDatePicker();
        setBottomNavigationBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_fixture, menu);
        return true;
    }

    private void populateFields(List<PlayerModel> players) {
        locationTextInputLayout.getEditText().setText(fixtureModel.Location);

        inflateChipGroup(players);
    }

    public void onClickEditFixture(MenuItem item) throws JSONException {

        PutFixtureDTO putFixtureDTO = new PutFixtureDTO(
                getFixtureLocation(),
                getFixtureDateTime(),
                fixturePlayers
        );

        progressIndicator.show();
        setUserInputEnabled(false);

        String currentSeasonId = currentIntent.getStringExtra(NextFixturesActivity.EXTRA_CURRENT_SEASON_ID);

        final String putFixtureUrl = String.format("%s/%s", ApiRoutes.FIXTURES_ROUTE(currentSeasonId), fixtureModel.FixtureId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, putFixtureUrl, new JSONObject(gson.toJson(putFixtureDTO)),
                response -> {
                    Intent intent = new Intent(this, NextFixturesActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    progressIndicator.hide();
                    setUserInputEnabled(true);

                    Snackbar snackbar = Snackbar.make(findViewById(R.id.constraint_layout_edit_fixture), R.string.server_error, Snackbar.LENGTH_LONG);
                    snackbar.setAnchorView(findViewById(R.id.bottom_navigation_edit_fixture));
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

    public void onAddPlayer(View view) {
        if (selectedPlayer == null) return;

        playersDropDown.remove(selectedPlayer);
        adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, playersDropDown);
        autoCompleteTextViewPlayer.setText(null);
        autoCompleteTextViewPlayer.setAdapter(adapter);

        Chip chip = new Chip(this);
        chip.setText(selectedPlayer.name);
        chip.setTag(selectedPlayer.playerId);
        chip.setCloseIconVisible(true);

        DropDownItem newDropDownItem = new DropDownItem(selectedPlayer.playerId, selectedPlayer.name, selectedPlayer.quality);
        chip.setOnCloseIconClickListener(v -> {
            v.setVisibility(View.GONE);
            playersDropDown.add(newDropDownItem);
            adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, playersDropDown);
            adapter.sort(Comparator.comparing(DropDownItem::getName));
            autoCompleteTextViewPlayer.setAdapter(adapter);
            fixturePlayers.removeIf(fp -> fp.PlayerId == newDropDownItem.playerId);
        });

        playersChipGroup.addView(chip);
        fixturePlayers.add(new FixturePlayer(selectedPlayer.playerId, selectedPlayer.name, selectedPlayer.quality));
        selectedPlayer = null;
    }

    private void inflateChipGroup(@NonNull List<PlayerModel> players) {
        List<UUID> fixturePlayersIds = fixtureModel.Players.stream().map(fp -> fp.PlayerId).collect(Collectors.toList());
        List<PlayerModel> remainingPlayers = players.stream().filter(x -> !fixturePlayersIds.contains(x.PlayerId)).collect(Collectors.toList());
        playersDropDown = remainingPlayers.stream().map(x -> new DropDownItem(x.PlayerId, x.Name, x.Quality)).collect(Collectors.toList());

        adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, playersDropDown);
        autoCompleteTextViewPlayer.setAdapter(adapter);
        autoCompleteTextViewPlayer.setOnItemClickListener((parent, view, position, id) -> selectedPlayer = (DropDownItem) parent.getItemAtPosition(position));

        for (FixturePlayer fixturePlayer : fixtureModel.Players) {
            Chip chip = new Chip(this);
            chip.setText(fixturePlayer.Name);
            chip.setTag(fixturePlayer.PlayerId);
            chip.setCloseIconVisible(true);

            DropDownItem newDropDownItem = new DropDownItem(fixturePlayer.PlayerId, fixturePlayer.Name, fixturePlayer.Quality);
            chip.setOnCloseIconClickListener(v -> {
                v.setVisibility(View.GONE);
                playersDropDown.add(newDropDownItem);
                adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, playersDropDown);
                adapter.sort(Comparator.comparing(DropDownItem::getName));
                autoCompleteTextViewPlayer.setAdapter(adapter);
                fixturePlayers.remove(fixturePlayer);
            });

            playersChipGroup.addView(chip);
        }
    }

    public void showDatePicker(View view) {
        datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    public void showTimePicker(View view) {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(18)
                .setMinute(0)
                .setTitleText("Select fixture time")
                .build();

        timePicker.addOnPositiveButtonClickListener(v -> {
            selectedHour = timePicker.getHour();
            selectedMinute = timePicker.getMinute();

            timeTextInputLayout.getEditText().setText(formatTime(selectedHour, selectedMinute));
        });

        timePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }


    @Nullable
    private String getFixtureLocation() {
        String location = getInputTextAsString(R.id.text_input_edit_fixture_location);

        if (Util.isNullOrEmpty(location)) {
            locationTextInputLayout.setError(getString(R.string.text_layout_error_required));
            return null;
        }

        return location;
    }

    @Nullable
    private Date getFixtureDateTime() {
        if (selectedDate == null || selectedHour == null) {
            if (selectedDate == null) {
                dateTextInputLayout.setError(getString(R.string.text_layout_error_required));
            }

            if (selectedHour == null) {
                timeTextInputLayout.setError(getString(R.string.text_layout_error_required));
            }

            return null;
        }

        return new Date(selectedDate.getYear(), selectedDate.getMonth(), selectedDate.getDay(), selectedHour, selectedMinute);
    }

    private void createDatePicker() {
        datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Fixture date").build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate = new Date(selection);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            dateTextInputLayout.getEditText().setText(formatter.format(selectedDate));
        });
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

    private void getAllPlayers() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ApiRoutes.PLAYERS_ROUTE, null,
                response -> {
                    List<PlayerModel> players = gson.fromJson(response.toString(), new TypeToken<List<PlayerModel>>() {
                    }.getType());
                    players.sort(Comparator.comparing(PlayerModel::getName));

                    progressIndicator.hide();
                    playerTextInputLayout.setEnabled(true);
                    addPlayerButton.setEnabled(true);

                    populateFields(players);
                },
                error -> {
                    progressIndicator.hide();
                    Log.e("REQUEST-GET-PLAYERS_ROUTE", gson.toJson(error));
                }
        );

        requestQueue.add(increaseTimeout(jsonArrayRequest));
    }


    @SuppressLint("NonConstantResourceId")
    private void setBottomNavigationBar() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_edit_fixture);
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

    private void setUserInputEnabled(boolean enabled) {
        locationTextInputLayout.setEnabled(enabled);
        dateTextInputLayout.setEnabled(enabled);
        timeTextInputLayout.setEnabled(enabled);
        playerTextInputLayout.setEnabled(enabled);
        addPlayerButton.setEnabled(enabled);
        playersChipGroup.setEnabled(enabled);

        for (int i = 0; i < playersChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) playersChipGroup.getChildAt(i);
            chip.setEnabled(enabled);
        }
    }

    @Nullable
    private String getInputTextAsString(int textInputEditTextResId) {
        TextInputEditText textInputEditText = findViewById(textInputEditTextResId);

        if (textInputEditText.getText() == null) return null;

        return textInputEditText.getText().toString();
    }

    private static class DropDownItem {
        private final UUID playerId;
        private final String name;
        private final Double quality;

        public DropDownItem(UUID playerId, String name, Double quality) {
            this.playerId = playerId;
            this.name = name;
            this.quality = quality;
        }

        public String getName() {
            return name;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format("%s (%s)", name, getValueOrNA(quality));
        }
    }
}
