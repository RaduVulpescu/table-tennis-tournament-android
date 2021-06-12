package com.example.tabletennistournament.modules.cup.fixture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.evrencoskun.tableview.TableView;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.models.FixturePlayer;
import com.example.tabletennistournament.modules.cup.fixture.models.Cell;
import com.example.tabletennistournament.modules.cup.fixture.models.ColumnHeader;
import com.example.tabletennistournament.modules.cup.fixture.models.RowHeader;
import com.example.tabletennistournament.modules.cup.fixture.models.ScoreCell;
import com.example.tabletennistournament.modules.cup.fixture.models.ScoreData;
import com.example.tabletennistournament.modules.cup.fixture.services.GroupTableViewAdapter;
import com.example.tabletennistournament.modules.cup.fixture.services.GroupTableViewClickListener;
import com.example.tabletennistournament.services.GsonSingleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    private static final String ARG_FIXTURE_PLAYERS_JSON = "ARG_FIXTURE_PLAYERS_JSON";

    Gson gson;

    View fragmentView;
    TableView tableView;
    GroupTableViewAdapter adapter;
    List<FixturePlayer> players;
    List<RowHeader> mRowHeaderList;
    List<ColumnHeader> mColumnHeaderList;
    List<List<Cell>> mCellList;

    public GroupFragment() {
    }

    @NonNull
    public static GroupFragment newInstance(String fixturePlayersJson) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FIXTURE_PLAYERS_JSON, fixturePlayersJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) return;

        gson = GsonSingleton.getInstance();

        String fixturePlayersJson = getArguments().getString(ARG_FIXTURE_PLAYERS_JSON);
        players = gson.fromJson(fixturePlayersJson, new TypeToken<List<FixturePlayer>>() {
        }.getType());

        mRowHeaderList = new ArrayList<>();
        mColumnHeaderList = new ArrayList<>();
        mCellList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) return;
        super.onViewCreated(view, savedInstanceState);

        fragmentView = view;
        tableView = fragmentView.findViewById(R.id.table_view_group);
        adapter = new GroupTableViewAdapter();
        tableView.setAdapter(adapter);

        inflateTableView(players);
    }

    private void inflateTableView(@NonNull List<FixturePlayer> players) {
        tableView.setMinimumHeight(R.dimen.default_column_header_height * players.size() * 2);

        for (FixturePlayer player : players) {
            mColumnHeaderList.add(new ColumnHeader(player.Name));
            mRowHeaderList.add(new RowHeader(player.Name));
        }

        for (int i = 0; i < players.size(); i++) {
            List<Cell> row = new ArrayList<>();
            for (int j = 0; j < players.size(); j++) {

                if (i == j) {
                    row.add(new Cell(""));
                } else {
                    ScoreData scoreData = new ScoreData(players.get(i).Name, players.get(j).Name, 3, 0);

                    row.add(new ScoreCell(scoreData));
                }
            }

            mCellList.add(row);
        }

        adapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList);
        tableView.setTableViewListener(new GroupTableViewClickListener(tableView));

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int dps = 40 * (players.size() + 1) + 10;
        tableView.getLayoutParams().height = (int) (dps * scale + 0.5f);
    }
}
