package com.example.tabletennistournament.modules.upcoming;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tabletennistournament.MainActivity;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.modules.players.PlayersActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NextFixturesActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_fixtures);

        bottomNavigationView = findViewById(R.id.bottom_navigation_upcoming);

        setBottomNavigationBar();
    }

    private void setBottomNavigationBar() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_button_next_fixtures);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_button_next_fixtures: {
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
                default:{
                    return false;
                }
            }
        });
    }
}