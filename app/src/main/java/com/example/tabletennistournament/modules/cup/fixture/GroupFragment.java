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
import com.example.tabletennistournament.models.GroupMatch;
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
    private static final String ARG_GROUP_MATCHES_JSON = "ARG_GROUP_MATCHES_JSON";

    Gson gson;

    View fragmentView;
    TableView tableView;
    GroupTableViewAdapter adapter;
    List<FixturePlayer> fixturePlayers;
    List<GroupMatch> groupMatches;
    List<RowHeader> mRowHeaderList;
    List<ColumnHeader> mColumnHeaderList;
    List<List<Cell>> mCellList;

    public GroupFragment() {
    }

    @NonNull
    public static GroupFragment newInstance(String groupMatchesJson, String playersJson) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_MATCHES_JSON, groupMatchesJson);
        args.putString(ARG_FIXTURE_PLAYERS_JSON, playersJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) return;

        gson = GsonSingleton.getInstance();

        String playersJson = getArguments().getString(ARG_GROUP_MATCHES_JSON);
        String groupMatchesJson = getArguments().getString(ARG_GROUP_MATCHES_JSON);
        fixturePlayers = gson.fromJson(playersJson, new TypeToken<List<FixturePlayer>>() {
        }.getType());
        groupMatches = gson.fromJson(groupMatchesJson, new TypeToken<List<GroupMatch>>() {
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

        inflateTableView();
    }

    private void inflateTableView() {
        List<String> playerNames = new ArrayList<>();
        for (GroupMatch match : groupMatches) {
            if (!playerNames.contains(match.PlayerOneStats.PlayerName)) {
                playerNames.add(match.PlayerOneStats.PlayerName);
            }
        }
        playerNames.add(groupMatches.get(groupMatches.size() - 1).PlayerTwoStats.PlayerName);

        for (String name : playerNames) {
            mColumnHeaderList.add(new ColumnHeader(name));
            mRowHeaderList.add(new RowHeader(name));
        }

        mColumnHeaderList.add(new ColumnHeader("Victories"));
        mColumnHeaderList.add(new ColumnHeader("Rank"));

        for (int i = 0; i < playerNames.size(); i++) {
            List<Cell> row = new ArrayList<>();
            for (int j = 0; j < playerNames.size(); j++) {
                if (i == j) {
                    row.add(new Cell(""));
                } else {
                    int finalI = i;
                    int finalJ = j;
                    GroupMatch groupMatch = groupMatches.stream().filter(x ->
                            (x.PlayerOneStats.PlayerName.equals(playerNames.get(finalI)) ||
                                    x.PlayerOneStats.PlayerName.equals(playerNames.get(finalJ))) &&
                                    (x.PlayerTwoStats.PlayerName.equals(playerNames.get(finalI)) ||
                                            x.PlayerTwoStats.PlayerName.equals(playerNames.get(finalJ)))
                    ).findFirst().orElseGet(null);

                    ScoreData scoreData = new ScoreData(
                            groupMatch.MatchId,
                            groupMatch.PlayerOneStats.PlayerName,
                            groupMatch.PlayerTwoStats.PlayerName,
                            groupMatch.PlayerOneStats.SetsWon,
                            groupMatch.PlayerTwoStats.SetsWon,
                            i < j
                    );

                    row.add(new ScoreCell(scoreData));
                }
            }

            row.add(new Cell("")); // Victories
            row.add(new Cell("")); // Rank

            mCellList.add(row);
        }

        adapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList);
        tableView.setTableViewListener(new GroupTableViewClickListener(tableView, getLayoutInflater(), groupMatches));

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int dps = 40 * (playerNames.size() + 1) + 10;
        tableView.getLayoutParams().height = (int) (dps * scale + 0.5f);
    }
}
