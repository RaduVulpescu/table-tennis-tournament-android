package com.example.tabletennistournament;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tabletennistournament.models.FixtureModel;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;

public class FixtureFragment extends Fragment {

    Gson gson;
    RequestQueueSingleton requestQueue;
    CircularProgressIndicator progressIndicator;
    TextView serverErrorTextView;
    View view;

    public static final String FIXTURE_JSON = "FIXTURE_JSON";

    public FixtureFragment() {
        super(R.layout.fragment_main_fixture);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle argumentsBundle = getArguments();
        if (argumentsBundle == null) return;

        gson = new Gson();
        requestQueue = RequestQueueSingleton.getInstance(getActivity().getBaseContext());
        progressIndicator = view.findViewById(R.id.circular_progress_indicator_fixture);
        serverErrorTextView = view.findViewById(R.id.text_view_server_error_fixture);

        String fixtureJson = argumentsBundle.getString(FIXTURE_JSON);

        FixtureModel fixture = gson.fromJson(fixtureJson, FixtureModel.class);
        this.view = view;

    }
}
