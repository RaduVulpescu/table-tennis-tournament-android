package com.example.tabletennistournament.modules.upcoming;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tabletennistournament.MainActivity;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.dto.NewFixtureDTO;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddFixtureActivity extends AppCompatActivity {

    public static final String EXTRA_FIXTURE_ADDED = "EXTRA_FIXTURE_ADDED";

    Gson gson;
    RequestQueueSingleton requestQueue;
    int validationErrors;

    TextInputLayout locationTextInputLayout;
    TextInputLayout dateTextInputLayout;
    TextInputLayout timeTextInputLayout;
    MaterialDatePicker<Long> datePicker;

    Date selectedDate = null;
    Integer selectedHour = null;
    Integer selectedMinute = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fixture);

        gson = new Gson();
        requestQueue = RequestQueueSingleton.getInstance(this);
        validationErrors = 0;

        locationTextInputLayout = findViewById(R.id.text_layout_add_fixture_location);
        dateTextInputLayout = findViewById(R.id.text_layout_add_fixture_date);
        timeTextInputLayout = findViewById(R.id.text_layout_add_fixture_time);

        createDatePicker();
        setBottomNavigationBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_fixture, menu);
        return true;
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

    public void onClickSaveFixture(MenuItem item) throws JSONException {
        clearValidations();

        NewFixtureDTO newFixture = new NewFixtureDTO(
                getFixtureLocation(),
                getFixtureDateTime()
        );

        if (validationErrors > 0) return;

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

    @Nullable
    private String getFixtureLocation() {
        String location = getInputTextAsString(R.id.text_input_add_fixture_location);

        if (Util.isNullOrEmpty(location)) {
            locationTextInputLayout.setError(getString(R.string.text_layout_error_required));
            validationErrors++;
            return null;
        }

        return location;
    }

    @Nullable
    private Date getFixtureDateTime() {
        if (selectedDate == null || selectedHour == null) {
            if (selectedDate == null) {
                dateTextInputLayout.setError(getString(R.string.text_layout_error_required));
                validationErrors++;
            }

            if (selectedHour == null) {
                timeTextInputLayout.setError(getString(R.string.text_layout_error_required));
                validationErrors++;
            }

            return null;
        }

        return new Date(selectedDate.getYear(), selectedDate.getMonth(), selectedDate.getDay(), selectedHour, selectedMinute);
    }

    private void clearValidations() {
        locationTextInputLayout.setError(null);
        dateTextInputLayout.setError(null);
        timeTextInputLayout.setError(null);

        validationErrors = 0;
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
        dateTextInputLayout.setEnabled(enabled);
        timeTextInputLayout.setEnabled(enabled);
    }

    @Nullable
    private String getInputTextAsString(int textInputEditTextResId) {
        TextInputEditText textInputEditText = findViewById(textInputEditTextResId);

        if (textInputEditText.getText() == null) return null;

        return textInputEditText.getText().toString();
    }

}
