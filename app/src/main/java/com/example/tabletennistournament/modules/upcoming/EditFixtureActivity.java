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
import com.example.tabletennistournament.modules.cup.CupActivity;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.dto.PutFixtureDTO;
import com.example.tabletennistournament.models.FixtureModel;
import com.example.tabletennistournament.models.FixturePlayer;
import com.example.tabletennistournament.models.PlayerModel;
import com.example.tabletennistournament.modules.players.PlayersActivity;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.example.tabletennistournament.services.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.tabletennistournament.services.Common.increaseTimeout;

public class EditFixtureActivity extends AppCompatActivity {

    Gson gson;
    RequestQueueSingleton requestQueue;

    TextInputLayout locationTextInputLayout;
    TextInputLayout dateTextInputLayout;
    TextInputLayout timeTextInputLayout;
    MaterialDatePicker<Long> datePicker;

    TextInputLayout player1TextInputLayout;
    TextInputLayout player2TextInputLayout;
    TextInputLayout player3TextInputLayout;
    TextInputLayout player4TextInputLayout;
    TextInputLayout player5TextInputLayout;
    TextInputLayout player6TextInputLayout;
    TextInputLayout player7TextInputLayout;
    TextInputLayout player8TextInputLayout;

    AutoCompleteTextView player1DropDown;
    AutoCompleteTextView player2DropDown;
    AutoCompleteTextView player3DropDown;
    AutoCompleteTextView player4DropDown;
    AutoCompleteTextView player5DropDown;
    AutoCompleteTextView player6DropDown;
    AutoCompleteTextView player7DropDown;
    AutoCompleteTextView player8DropDown;

    Date selectedDate = null;
    Integer selectedHour = null;
    Integer selectedMinute = null;

    FixtureModel fixtureModel;
    Intent currentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fixture);

        gson = new Gson();
        requestQueue = RequestQueueSingleton.getInstance(this);

        locationTextInputLayout = findViewById(R.id.text_layout_edit_fixture_location);
        dateTextInputLayout = findViewById(R.id.text_layout_edit_fixture_date);
        timeTextInputLayout = findViewById(R.id.text_layout_edit_fixture_time);

        player1TextInputLayout = findViewById(R.id.text_input_upcoming_edit_fixture_player1);
        player2TextInputLayout = findViewById(R.id.text_input_upcoming_edit_fixture_player2);
        player3TextInputLayout = findViewById(R.id.text_input_upcoming_edit_fixture_player3);
        player4TextInputLayout = findViewById(R.id.text_input_upcoming_edit_fixture_player4);
        player5TextInputLayout = findViewById(R.id.text_input_upcoming_edit_fixture_player5);
        player6TextInputLayout = findViewById(R.id.text_input_upcoming_edit_fixture_player6);
        player7TextInputLayout = findViewById(R.id.text_input_upcoming_edit_fixture_player7);
        player8TextInputLayout = findViewById(R.id.text_input_upcoming_edit_fixture_player8);

        currentIntent = getIntent();
        String fixtureJson = currentIntent.getStringExtra(NextFixturesActivity.EXTRA_FIXTURE_JSON);
        fixtureModel = gson.fromJson(fixtureJson, FixtureModel.class);

        populateFields();
        getAllPlayers();
        createDatePicker();
        setBottomNavigationBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_fixture, menu);
        return true;
    }

    private void populateFields() {
        locationTextInputLayout.getEditText().setText(fixtureModel.Location);
    }

    public void onClickEditFixture(MenuItem item) throws JSONException {

        PutFixtureDTO putFixtureDTO = new PutFixtureDTO(
                getFixtureLocation(),
                getFixtureDateTime(),
                getFixturePlayers()
        );

        LinearProgressIndicator progressIndicator = findViewById(R.id.linear_progress_indicator_edit_fixture);
        progressIndicator.show();
        setUserInputEnabled(false);

        String currentSeasonId = currentIntent.getStringExtra(NextFixturesActivity.EXTRA_CURRENT_SEASON_ID);
        String fixtureId = currentIntent.getStringExtra(NextFixturesActivity.EXTRA_FIXTURE_ID);

        final String putFixtureUrl = String.format("%s/%s", ApiRoutes.FIXTURES_ROUTE(currentSeasonId), fixtureId);
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

    private void inflatePlayersDropdowns(@NonNull List<PlayerModel> allPlayers) {
        String[] items = allPlayers.stream().map(x -> String.format("%s (%s)", x.Name, getValueOrNA(x.Quality))).toArray(String[]::new);

        player1DropDown = findViewById(R.id.auto_complete_text_view_edit_fixture_player1);
        player2DropDown = findViewById(R.id.auto_complete_text_view_edit_fixture_player2);
        player3DropDown = findViewById(R.id.auto_complete_text_view_edit_fixture_player3);
        player4DropDown = findViewById(R.id.auto_complete_text_view_edit_fixture_player4);
        player5DropDown = findViewById(R.id.auto_complete_text_view_edit_fixture_player5);
        player6DropDown = findViewById(R.id.auto_complete_text_view_edit_fixture_player6);
        player7DropDown = findViewById(R.id.auto_complete_text_view_edit_fixture_player7);
        player8DropDown = findViewById(R.id.auto_complete_text_view_edit_fixture_player8);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, items);

        player1DropDown.setAdapter(adapter);
        player2DropDown.setAdapter(adapter);
        player3DropDown.setAdapter(adapter);
        player4DropDown.setAdapter(adapter);
        player5DropDown.setAdapter(adapter);
        player6DropDown.setAdapter(adapter);
        player7DropDown.setAdapter(adapter);
        player8DropDown.setAdapter(adapter);
    }

    @NonNull
    private String getValueOrNA(@Nullable Double quality) {
        if (quality == null) {
            return "N/A";
        }

        return String.valueOf(quality);
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

    @NonNull
    private List<FixturePlayer> getFixturePlayers() {
        String player1 = getInputFromDropDown(player1DropDown);
        String player2 = getInputFromDropDown(player2DropDown);
        String player3 = getInputFromDropDown(player3DropDown);
        String player4 = getInputFromDropDown(player4DropDown);
        String player5 = getInputFromDropDown(player5DropDown);
        String player6 = getInputFromDropDown(player6DropDown);
        String player7 = getInputFromDropDown(player7DropDown);
        String player8 = getInputFromDropDown(player8DropDown);

        List<FixturePlayer> list = new ArrayList<>();
        addPlayer(list, player1);
        addPlayer(list, player2);
        addPlayer(list, player3);
        addPlayer(list, player4);
        addPlayer(list, player5);
        addPlayer(list, player6);
        addPlayer(list, player7);
        addPlayer(list, player8);

        return list;
    }

    private void addPlayer(List<FixturePlayer> players, String player) {
        if (player == null) {
            return;
        }

        String[] x = player.split("\\(");
        String name = x[0].trim();
        String quality = x[1].split("\\)")[0];
        double qlt;
        try {
            qlt = Double.parseDouble(quality);
        } catch (Exception e) {
            qlt = 0d;
        }

        players.add(new FixturePlayer(null, name, qlt));
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

                    inflatePlayersDropdowns(players);
                },
                error -> Log.e("REQUEST-GET-PLAYERS_ROUTE", gson.toJson(error))
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
        player1TextInputLayout.setEnabled(enabled);
        player2TextInputLayout.setEnabled(enabled);
        player3TextInputLayout.setEnabled(enabled);
        player4TextInputLayout.setEnabled(enabled);
        player5TextInputLayout.setEnabled(enabled);
        player6TextInputLayout.setEnabled(enabled);
        player7TextInputLayout.setEnabled(enabled);
        player8TextInputLayout.setEnabled(enabled);
    }

    @Nullable
    private String getInputTextAsString(int textInputEditTextResId) {
        TextInputEditText textInputEditText = findViewById(textInputEditTextResId);

        if (textInputEditText.getText() == null) return null;

        return textInputEditText.getText().toString();
    }

    @Nullable
    private String getInputFromDropDown(AutoCompleteTextView textView) {
        String result = textView.getText().toString();

        if (Util.isNullOrEmpty(result)) {
            return null;
        }

        return result;
    }
}