package com.example.tabletennistournament.modules.cup.fixture.services;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.listener.ITableViewListener;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.models.GroupMatch;
import com.example.tabletennistournament.modules.cup.fixture.models.Cell;
import com.example.tabletennistournament.modules.cup.fixture.models.ScoreCell;
import com.example.tabletennistournament.services.Util;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class GroupTableViewClickListener implements ITableViewListener {

    private final ITableView tableView;
    private final LayoutInflater layoutInflater;
    private List<GroupMatch> groupMatches;

    public GroupTableViewClickListener(ITableView tableView, LayoutInflater layoutInflater, List<GroupMatch> groupMatches) {
        this.tableView = tableView;
        this.layoutInflater = layoutInflater;
        this.groupMatches = groupMatches;
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

        final View alertDialogView = layoutInflater.inflate(R.layout.alert_dialog_edit_group_match_score, null);

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
                cell.setScores(p1Score, p2Score);
                oppositeCell.setScores(p1Score, p2Score);

                tableView.getAdapter().notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
    }
}
