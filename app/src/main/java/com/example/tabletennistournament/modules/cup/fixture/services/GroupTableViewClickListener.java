package com.example.tabletennistournament.modules.cup.fixture.services;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.listener.ITableViewListener;
import com.example.tabletennistournament.modules.cup.fixture.models.Cell;
import com.example.tabletennistournament.modules.cup.fixture.models.ScoreCell;

public class GroupTableViewClickListener implements ITableViewListener {

    private final ITableView tableView;

    public GroupTableViewClickListener(ITableView tableView) {
        this.tableView = tableView;
    }

    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
    }

    @Override
    public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
    }

    @Override
    public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
        Cell cell = (Cell) tableView.getAdapter().getCellItem(columnPosition, rowPosition);

        if (!(cell instanceof ScoreCell)) return;

        ((ScoreCell) cell).setScores(3, 1);
        ScoreCell oppositeCell = (ScoreCell) tableView.getAdapter().getCellItem(rowPosition, columnPosition);
        oppositeCell.setScores(1, 3);

        tableView.getAdapter().notifyDataSetChanged();
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
}
