package com.example.tabletennistournament.modules.cup.fixture.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.evrencoskun.tableview.TableView;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.models.FixtureModel;
import com.example.tabletennistournament.models.FixturePlayer;
import com.example.tabletennistournament.models.GroupMatch;
import com.example.tabletennistournament.models.PlayerMatchStats;
import com.example.tabletennistournament.modules.cup.fixture.group.models.Cell;
import com.example.tabletennistournament.modules.cup.fixture.group.models.ColumnHeader;
import com.example.tabletennistournament.modules.cup.fixture.group.models.NumberCell;
import com.example.tabletennistournament.modules.cup.fixture.group.models.RowHeader;
import com.example.tabletennistournament.modules.cup.fixture.group.models.ScoreCell;
import com.example.tabletennistournament.modules.cup.fixture.group.models.ScoreData;
import com.example.tabletennistournament.modules.cup.fixture.services.GroupTableViewAdapter;
import com.example.tabletennistournament.modules.cup.fixture.services.GroupTableViewClickListener;
import com.example.tabletennistournament.modules.cup.fixture.group.viewModels.FixtureViewModel;
import com.example.tabletennistournament.services.GsonSingleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    private static final String ARG_GROUP_FIXTURE_JSON = "ARG_GROUP_FIXTURE_JSON";
    private static final String ARG_GROUP_MATCHES_JSON = "ARG_GROUP_MATCHES_JSON";

    FixtureViewModel fixtureViewModel;

    FixtureModel fixture;
    List<GroupMatch> groupMatches;

    Gson gson;

    View fragmentView;
    TableView tableView;
    GroupTableViewAdapter adapter;
    List<RowHeader> mRowHeaderList;
    List<ColumnHeader> mColumnHeaderList;
    List<List<Cell>> mCellList;

    public GroupFragment() {
    }

    @NonNull
    public static GroupFragment newInstance(String fixtureJson, String groupMatchesJson) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();

        args.putString(ARG_GROUP_FIXTURE_JSON, fixtureJson);
        args.putString(ARG_GROUP_MATCHES_JSON, groupMatchesJson);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) return;

        gson = GsonSingleton.getInstance();

        String fixtureJson = getArguments().getString(ARG_GROUP_FIXTURE_JSON);
        String groupMatchesJson = getArguments().getString(ARG_GROUP_MATCHES_JSON);
        fixture = gson.fromJson(fixtureJson, FixtureModel.class);
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
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() == null) return;

        fixtureViewModel = new ViewModelProvider(requireParentFragment()).get(FixtureViewModel.class);

        fragmentView = view;
        tableView = fragmentView.findViewById(R.id.table_view_group);
        adapter = new GroupTableViewAdapter();
        tableView.setAdapter(adapter);

        inflateTableView();
    }

    private void inflateTableView() {
        List<PlayerMatchStats> players = new ArrayList<>();
        for (GroupMatch match : groupMatches) {
            boolean isAlreadyPresent = false;
            for (PlayerMatchStats player : players) {
                if (player.PlayerId.compareTo(match.PlayerOneStats.PlayerId) == 0) {
                    isAlreadyPresent = true;
                    break;
                }
            }
            if (!isAlreadyPresent) {
                players.add(match.PlayerOneStats);
            }
        }
        players.add(groupMatches.get(groupMatches.size() - 1).PlayerTwoStats);

        for (PlayerMatchStats player : players) {
            mColumnHeaderList.add(new ColumnHeader(player.PlayerName));
            mRowHeaderList.add(new RowHeader(player.PlayerName));
        }

        mColumnHeaderList.add(new ColumnHeader("Victories"));
        mColumnHeaderList.add(new ColumnHeader("Rank"));

        for (int i = 0; i < players.size(); i++) {
            List<Cell> row = new ArrayList<>();
            int numberOfVictories = 0;
            int finalI = i;

            for (int j = 0; j < players.size(); j++) {
                if (i == j) {
                    row.add(new Cell(""));
                } else {
                    int finalJ = j;
                    GroupMatch groupMatch = groupMatches.stream().filter(x ->
                            (x.PlayerOneStats.PlayerId.compareTo(players.get(finalI).PlayerId) == 0 ||
                                    x.PlayerOneStats.PlayerId.compareTo(players.get(finalJ).PlayerId) == 0) &&
                                    (x.PlayerTwoStats.PlayerId.compareTo(players.get(finalI).PlayerId) == 0 ||
                                            x.PlayerTwoStats.PlayerId.compareTo(players.get(finalJ).PlayerId) == 0)
                    ).findFirst().orElseGet(null);

                    ScoreData scoreData = new ScoreData(
                            groupMatch.MatchId,
                            groupMatch.PlayerOneStats.PlayerName,
                            groupMatch.PlayerTwoStats.PlayerName,
                            groupMatch.PlayerOneStats.SetsWon,
                            groupMatch.PlayerTwoStats.SetsWon,
                            i < j
                    );

                    if (groupMatch.PlayerOneStats.SetsWon != null && groupMatch.PlayerTwoStats.SetsWon != null &&
                            (i < j && groupMatch.PlayerOneStats.SetsWon > groupMatch.PlayerTwoStats.SetsWon ||
                                    i > j && groupMatch.PlayerOneStats.SetsWon < groupMatch.PlayerTwoStats.SetsWon)) {
                        numberOfVictories++;
                    }

                    row.add(new ScoreCell(scoreData));
                }
            }

            row.add(new NumberCell(numberOfVictories));

            FixturePlayer fixturePlayer = fixture.Players.stream()
                    .filter(x -> x.PlayerId.compareTo(players.get(finalI).PlayerId) == 0)
                    .findFirst().orElseGet(null);

            row.add(new Cell(fixturePlayer.GroupRank));

            mCellList.add(row);
        }

        adapter.setAllItems(mColumnHeaderList, mRowHeaderList, mCellList);
        tableView.setTableViewListener(new GroupTableViewClickListener(tableView,
                getLayoutInflater(), fixture.SeasonId.toString(), fixture.FixtureId.toString(),
                players.size() + 2, fixtureViewModel));

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int dps = 40 * (players.size() + 1) + 10;
        tableView.getLayoutParams().height = (int) (dps * scale + 0.5f);
    }
}
