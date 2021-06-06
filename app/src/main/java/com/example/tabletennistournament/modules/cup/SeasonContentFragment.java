package com.example.tabletennistournament.modules.cup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.models.FixtureModel;
import com.example.tabletennistournament.models.SeasonModel;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.GsonSingleton;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.example.tabletennistournament.services.Common.increaseTimeout;

public class SeasonContentFragment extends Fragment {

    private static final String ARG_SEASON_JSON = "seasonJson";

    FragmentActivity fragmentActivity;
    Gson gson;
    RequestQueueSingleton requestQueue;

    CircularProgressIndicator progressIndicator;
    TextView serverErrorTextView;
    Button reloadButton;
    TabLayout fixturesTabLayout;

    public SeasonContentFragment() {
    }

    @NonNull
    public static SeasonContentFragment newInstance(String seasonJson) {
        SeasonContentFragment fragment = new SeasonContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEASON_JSON, seasonJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null) return;

        fragmentActivity = getActivity();
        gson = GsonSingleton.getInstance();
        requestQueue = RequestQueueSingleton.getInstance(fragmentActivity.getBaseContext());

        progressIndicator = fragmentActivity.findViewById(R.id.circular_progress_indicator_main);
        serverErrorTextView = fragmentActivity.findViewById(R.id.text_view_server_error_main);
        reloadButton = fragmentActivity.findViewById(R.id.button_reload_main);

        String seasonJson = getArguments().getString(ARG_SEASON_JSON);
        SeasonModel season = gson.fromJson(seasonJson, SeasonModel.class);

        getFixtures(season.SeasonId.toString());
        displayRanking(season.SeasonId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_season_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fixturesTabLayout = view.findViewById(R.id.tab_layout_main);
    }

    private void getFixtures(String seasonId) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, ApiRoutes.FIXTURES_ROUTE(seasonId), null,
                response -> {
                    List<FixtureModel> fixtures = gson.fromJson(response.toString(), new TypeToken<List<FixtureModel>>() {
                    }.getType());
                    fixtures.sort(Comparator.comparing(FixtureModel::getDate).reversed());

                    populateTabLayout(fixtures);
                    progressIndicator.hide();
                },
                error -> {
                    reloadButton.setVisibility(View.VISIBLE);
                    serverErrorTextView.setVisibility(View.VISIBLE);
                    progressIndicator.hide();
                }
        );

        requestQueue.add(increaseTimeout(jsonArrayRequest));
    }

    private void populateTabLayout(@NonNull List<FixtureModel> fixtures) {
        fixturesTabLayout.addTab(fixturesTabLayout.newTab().setText("Ranking").setTag("Ranking"));

        for (int i = 0, fixturesSize = fixtures.size(); i < fixturesSize; i++) {
            FixtureModel fixture = fixtures.get(i);

            String tabTitle = fixture.Date.format(DateTimeFormatter.ofPattern("dd MMM"));
            if (tabTitle.startsWith("0")) tabTitle = tabTitle.substring(1);

            TabLayout.Tab tab = fixturesTabLayout.newTab()
                    .setText(tabTitle)
                    .setTag(i);

            fixturesTabLayout.addTab(tab);
        }

        fixturesTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getTag().toString().equals("Ranking")) {
                    displayRanking(fixtures.get(0).SeasonId);
                } else {
                    int fixtureIndex = Integer.parseInt(tab.getTag().toString());
                    displayFixture(fixtures.get(fixtureIndex));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void displayRanking(@NonNull UUID seasonId) {
        fragmentActivity.getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view_season_content, RankingFragment.newInstance(seasonId.toString()))
                .commit();
    }

    private void displayFixture(FixtureModel fixture) {
        fragmentActivity.getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view_season_content, FixtureFragment.newInstance(gson.toJson(fixture)))
                .commit();
    }
}
