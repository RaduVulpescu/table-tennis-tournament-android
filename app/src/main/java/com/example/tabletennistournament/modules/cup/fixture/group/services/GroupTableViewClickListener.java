package com.example.tabletennistournament.modules.cup.fixture.group.services;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.listener.ITableViewListener;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.dto.MatchPutDTO;
import com.example.tabletennistournament.modules.cup.fixture.group.models.Cell;
import com.example.tabletennistournament.modules.cup.fixture.group.models.NumberCell;
import com.example.tabletennistournament.modules.cup.fixture.group.models.ScoreCell;
import com.example.tabletennistournament.modules.cup.fixture.group.viewModels.FixtureViewModel;
import com.example.tabletennistournament.services.ApiRoutes;
import com.example.tabletennistournament.services.GsonSingleton;
import com.example.tabletennistournament.services.RequestQueueSingleton;
import com.example.tabletennistournament.services.Util;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static com.example.tabletennistournament.services.Common.increaseTimeout;

public class GroupTableViewClickListener implements ITableViewListener {

    Gson gson;
    RequestQueueSingleton requestQueue;

    private final ITableView tableView;
    private final LayoutInflater layoutInflater;
    private final String seasonId;
    private final String fixtureId;
    private final int rowLength;
    private final FixtureViewModel fixtureViewModel;

    public GroupTableViewClickListener(ITableView tableView, LayoutInflater layoutInflater,
                                       String seasonId, String fixtureId, int rowLength,
                                       FixtureViewModel fixtureViewModel) {
        this.tableView = tableView;
        this.layoutInflater = layoutInflater;
        this.seasonId = seasonId;
        this.fixtureId = fixtureId;
        this.rowLength = rowLength;
        this.fixtureViewModel = fixtureViewModel;

        gson = GsonSingleton.getInstance();
        requestQueue = RequestQueueSingleton.getInstance(this.tableView.getContext().getApplicationContext());
    }

    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
    }

    @Override
    public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
    }

    @Override
    public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
        if (columnPosition == rowPosition) return;
        Cell cell = (Cell) tableView.getAdapter().getCellItem(columnPosition, rowPosition);

        if (!(cell instanceof ScoreCell)) return;

        ScoreCell oppositeCell = (ScoreCell) tableView.getAdapter().getCellItem(rowPosition, columnPosition);

        createAlertDialog(((ScoreCell) cell), oppositeCell, columnPosition, rowPosition);
    }

    @Override
    public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int columnPosition) {
    }

    @Override
    public void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int columnPosition) {
    }

    @Override
    public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int columnPosition) {
    }

    @Override
    public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int rowPosition) {
    }

    @Override
    public void onRowHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int rowPosition) {
    }

    @Override
    public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int rowPosition) {
    }

    private void createAlertDialog(@NonNull ScoreCell cell, @NonNull ScoreCell oppositeCell, int columnPosition, int rowPosition) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(tableView.getContext());

        final View alertDialogView = layoutInflater.inflate(R.layout.alert_dialog_edit_match_score, null);

        builder.setTitle("Set score");
        builder.setView(alertDialogView);
        AlertDialog alertDialog = builder.create();

        alertDialog.show();

        TextView playerOneNameTextView = alertDialogView.findViewById(R.id.text_view_match_score_player_one_name);
        TextView playerTwoNameTextView = alertDialogView.findViewById(R.id.text_view_match_score_player_two_name);
        EditText playerOneScoreEditText = alertDialogView.findViewById(R.id.edit_text_match_score_player_one_score);
        EditText playerTwoScoreEditText = alertDialogView.findViewById(R.id.edit_text_match_score_player_two_score);
        Button cancelButton = alertDialogView.findViewById(R.id.button_set_score_dismiss);
        Button saveButton = alertDialogView.findViewById(R.id.button_set_score_save);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                saveButton.setEnabled(!Util.isNullOrEmpty(playerOneScoreEditText.getText().toString()) &&
                        !Util.isNullOrEmpty(playerTwoScoreEditText.getText().toString()));
            }
        };

        playerOneScoreEditText.addTextChangedListener(afterTextChangedListener);
        playerTwoScoreEditText.addTextChangedListener(afterTextChangedListener);

        playerOneNameTextView.setText(columnPosition > rowPosition ? cell.getPlayerOneName() : cell.getPlayerTwoName());
        playerTwoNameTextView.setText(columnPosition > rowPosition ? cell.getPlayerTwoName() : cell.getPlayerOneName());

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());

        saveButton.setOnClickListener(v -> {
            NumberCell victoriesCell = (NumberCell) tableView.getAdapter().getCellItem(rowLength - 2, rowPosition);

            String playerOneScore = playerOneScoreEditText.getText().toString();
            String playerTwoScore = playerTwoScoreEditText.getText().toString();

            int p1Score;
            int p2Score;

            if (columnPosition > rowPosition) {
                p1Score = Integer.parseInt(playerOneScore);
                p2Score = Integer.parseInt(playerTwoScore);
            } else {
                p1Score = Integer.parseInt(playerTwoScore);
                p2Score = Integer.parseInt(playerOneScore);
            }

            if (p1Score == p2Score) {
                playerTwoScoreEditText.setError("Scores must be different");
            } else {
                Integer initialPlayerOneScore = cell.getPlayerOneScore();
                Integer initialPlayerTwoScore = cell.getPlayerTwoScore();

                if (initialPlayerOneScore == null && initialPlayerTwoScore == null){
                    fixtureViewModel.incrementFinishedMatches();
                }

                if (initialPlayerOneScore == null && initialPlayerTwoScore == null &&
                        (columnPosition > rowPosition && p1Score > p2Score ||
                                columnPosition < rowPosition && p1Score < p2Score)) {
                    victoriesCell.increment();
                } else if (initialPlayerOneScore != null && initialPlayerTwoScore != null) {
                    if (columnPosition > rowPosition && p1Score > p2Score && initialPlayerOneScore < initialPlayerTwoScore ||
                            columnPosition < rowPosition && p1Score < p2Score && initialPlayerOneScore > initialPlayerTwoScore) {
                        victoriesCell.increment();
                    } else if (columnPosition > rowPosition && p1Score < p2Score && initialPlayerOneScore > initialPlayerTwoScore ||
                            columnPosition < rowPosition && p1Score > p2Score && initialPlayerOneScore < initialPlayerTwoScore) {
                        victoriesCell.decrement();
                    }
                }

                cell.setScores(p1Score, p2Score);
                oppositeCell.setScores(p1Score, p2Score);

                try {
                    updateMatch(cell.getMatchId(), p1Score, p2Score);
                } catch (JSONException e) {
                    Log.e("GroupTableViewClickListener",
                            String.format("Updating group match %s failed.", cell.getMatchId()), e);
                }

                tableView.getAdapter().notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
    }

    private void updateMatch(@NonNull UUID matchId, int p1Score, int p2Score) throws JSONException {
        final String patchGroupMatchURL =
                ApiRoutes.PATCH_FIXTURE_GROUP_MATCH_ROUTE(seasonId, fixtureId, matchId.toString());

        MatchPutDTO matchPutDTO = new MatchPutDTO(p1Score, p2Score);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH,
                patchGroupMatchURL, new JSONObject(gson.toJson(matchPutDTO)),
                response -> {
                },
                error -> Log.e("GroupTableViewClickListener", error != null & error.getMessage() != null ?
                        error.getMessage() :
                        "PATCH match crashed. Check: https://eu-west-1.console.aws.amazon.com/cloudwatch/home?region=eu-west-1#logsV2:log-groups/log-group/$252Faws$252Flambda$252Fpatch-group-match-function")
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
}
