package com.example.tabletennistournament.modules.players;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tabletennistournament.MainActivity;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.dto.NewPlayerDTO;
import com.example.tabletennistournament.enums.Level;
import com.example.tabletennistournament.modules.upcoming.NextFixturesActivity;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.example.tabletennistournament.services.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AddPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_PLAYER_ADDED = "EXTRA_PLAYER_ADDED";

    TextInputLayout nameTextInputLayout;
    TextInputLayout cityTextInputLayout;
    TextInputLayout levelTextInputLayout;
    TextInputLayout birthYearTextInputLayout;
    TextInputLayout heightTextInputLayout;
    TextInputLayout weightTextInputLayout;

    Gson gson;
    RequestQueueSingleton requestQueue;
    int validationErrors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        nameTextInputLayout = findViewById(R.id.text_layout_add_player_name);
        cityTextInputLayout = findViewById(R.id.text_layout_add_player_city);
        levelTextInputLayout = findViewById(R.id.text_layout_add_player_level);
        birthYearTextInputLayout = findViewById(R.id.text_layout_add_player_birth_year);
        heightTextInputLayout = findViewById(R.id.text_layout_add_player_height);
        weightTextInputLayout = findViewById(R.id.text_layout_add_player_weight);

        gson = new Gson();
        requestQueue = RequestQueueSingleton.getInstance(this);
        validationErrors = 0;

        setBottomNavigationBar();
        inflateLevelDropdown();
        inflateBirthYearDropdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_player, menu);
        return true;
    }

    public void onClickSavePlayer(MenuItem item) throws JSONException {
        clearValidations();

        NewPlayerDTO newPlayer = new NewPlayerDTO(
                getPlayerName(),
                getPlayerCity(),
                getPlayerBirthYear(),
                getPlayerHeight(),
                getPlayerWeight(),
                getPlayerLevel()
        );

        if (validationErrors > 0) return;

        LinearProgressIndicator progressIndicator = findViewById(R.id.linear_progress_indicator_add_player);
        progressIndicator.show();
        setUserInputEnabled(false);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiRoutes.PLAYERS_ROUTE, new JSONObject(gson.toJson(newPlayer)),
                response -> {
                    Intent intent = new Intent(this, PlayersActivity.class);
                    intent.putExtra(EXTRA_PLAYER_ADDED, true);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    progressIndicator.hide();
                    setUserInputEnabled(true);

                    Snackbar snackbar = Snackbar.make(findViewById(R.id.constraint_layout_add_player), R.string.server_error, Snackbar.LENGTH_LONG);
                    snackbar.setAnchorView(findViewById(R.id.bottom_navigation_add_player));
                    snackbar.show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    @SuppressLint("NonConstantResourceId")
    private void setBottomNavigationBar() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_add_player);
        bottomNavigationView.setSelectedItemId(R.id.bottom_navigation_add_player);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_button_upcoming: {
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
                default: {
                    return false;
                }
            }
        });
    }

    private void inflateLevelDropdown() {
        AutoCompleteTextView levelDropdown = findViewById(R.id.auto_complete_text_view_add_player_level);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, Level.getValues());

        levelDropdown.setAdapter(adapter);
    }

    private void inflateBirthYearDropdown() {
        AutoCompleteTextView birthYearDropdown = findViewById(R.id.auto_complete_text_view_add_player_birth_year);

        List<String> dropdownValues = new ArrayList<>();
        dropdownValues.add("");
        for (int i = LocalDateTime.now().getYear(); i >= 1900; i--) {
            dropdownValues.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, dropdownValues);

        birthYearDropdown.setAdapter(adapter);
    }

    private void clearValidations() {
        nameTextInputLayout.setError(null);
        cityTextInputLayout.setError(null);
        levelTextInputLayout.setError(null);
        heightTextInputLayout.setError(null);
        weightTextInputLayout.setError(null);

        validationErrors = 0;
    }

    private void setUserInputEnabled(boolean enabled) {
        nameTextInputLayout.setEnabled(enabled);
        cityTextInputLayout.setEnabled(enabled);
        levelTextInputLayout.setEnabled(enabled);
        birthYearTextInputLayout.setEnabled(enabled);
        heightTextInputLayout.setEnabled(enabled);
        weightTextInputLayout.setEnabled(enabled);
    }

    @Nullable
    private String getPlayerName() {
        String playerName = getInputTextAsString(R.id.text_input_add_player_name);

        if (Util.isNullOrEmpty(playerName)) {
            nameTextInputLayout.setError(getString(R.string.text_layout_error_required));
            validationErrors++;
            return null;
        }

        return playerName;
    }

    @Nullable
    private String getPlayerCity() {
        String playerCity = getInputTextAsString(R.id.text_input_add_player_city);

        if (Util.isNullOrEmpty(playerCity)) {
            cityTextInputLayout.setError(getString(R.string.text_layout_error_required));
            validationErrors++;
            return null;
        }

        return playerCity;
    }

    @Nullable
    private Level getPlayerLevel() {
        AutoCompleteTextView textView = findViewById(R.id.auto_complete_text_view_add_player_level);
        String levelStringValue = textView.getText().toString();

        try {
            return Level.valueOf(levelStringValue);
        } catch (IllegalArgumentException e) {
            levelTextInputLayout.setError(getString(R.string.text_layout_error_required));
            validationErrors++;
            return null;
        }
    }

    @Nullable
    private Integer getPlayerBirthYear() {
        AutoCompleteTextView textView = findViewById(R.id.auto_complete_text_view_add_player_birth_year);
        String birthYear = textView.getText().toString();

        if (Util.isNullOrEmpty(birthYear)) {
            return null;
        }

        return Integer.parseInt(birthYear);
    }

    @Nullable
    private Integer getPlayerHeight() {
        Integer playerHeight = getInputTextAsInteger(R.id.text_input_add_player_height);

        if (playerHeight != null && (playerHeight < 20 || playerHeight > 250)) {
            heightTextInputLayout.setError(getString(R.string.text_layout_error_height));
            validationErrors++;
            return null;
        }

        return playerHeight;
    }

    @Nullable
    private Integer getPlayerWeight() {
        Integer playerWeight = getInputTextAsInteger(R.id.text_input_add_player_weight);

        if (playerWeight != null && (playerWeight < 10 || playerWeight > 200)) {
            weightTextInputLayout.setError(getString(R.string.text_layout_error_weight));
            validationErrors++;
            return null;
        }

        return playerWeight;
    }

    @Nullable
    private String getInputTextAsString(int textInputEditTextResId) {
        TextInputEditText textInputEditText = findViewById(textInputEditTextResId);

        if (textInputEditText.getText() == null) return null;

        return textInputEditText.getText().toString();
    }

    @Nullable
    private Integer getInputTextAsInteger(int textInputLayoutResourceId) {
        try {
            return Integer.parseInt(getInputTextAsString(textInputLayoutResourceId));
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
