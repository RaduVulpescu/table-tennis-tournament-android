package com.example.tabletennistournament.modules.upcoming;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tabletennistournament.MainActivity;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.dto.PutFixtureDTO;
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

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.tabletennistournament.services.Common.increaseTimeout;

public class EditFixtureActivity extends AppCompatActivity {

    Gson gson;
    RequestQueueSingleton requestQueue;

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
        setContentView(R.layout.activity_edit_fixture);

        gson = new Gson();
        requestQueue = RequestQueueSingleton.getInstance(this);

        locationTextInputLayout = findViewById(R.id.text_layout_edit_fixture_location);
        dateTextInputLayout = findViewById(R.id.text_layout_edit_fixture_date);
        timeTextInputLayout = findViewById(R.id.text_layout_edit_fixture_time);

        createDatePicker();
        setBottomNavigationBar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_fixture, menu);
        return true;
    }

    public void onClickEditFixture(MenuItem item) throws JSONException {

        PutFixtureDTO putFixtureDTO = new PutFixtureDTO(
                getFixtureLocation(),
                getFixtureDateTime(),
                new ArrayList<>()
        );

        LinearProgressIndicator progressIndicator = findViewById(R.id.linear_progress_indicator_edit_fixture);
        progressIndicator.show();
        setUserInputEnabled(false);

        Intent currentIntent = getIntent();
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
                    Log.e("CEVA", gson.toJson(error));

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

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                Integer.parseInt(getString(R.string.volley_request_timeout)),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(increaseTimeout(jsonObjectRequest));
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