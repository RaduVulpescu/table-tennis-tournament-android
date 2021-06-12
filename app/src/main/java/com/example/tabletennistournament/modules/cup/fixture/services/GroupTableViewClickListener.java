package com.example.tabletennistournament.modules.cup.fixture.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.listener.ITableViewListener;

public class GroupTableViewClickListener implements ITableViewListener {

    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
        Log.d("GroupTableViewClickListener", "onCellClicked has been clicked for x= " + columnPosition + " y= " + rowPosition);
    }

    @Override
    public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
        Log.d("GroupTableViewClickListener", "onCellDoubleClicked has been clicked for x= " + columnPosition + " y= " + rowPosition);
    }

    @Override
    public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int columnPosition, int rowPosition) {
        Log.d("GroupTableViewClickListener", "onCellLongPressed has been clicked for x= " + columnPosition + " y= " + rowPosition);
    }

    @Override
    public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int columnPosition) {
        Log.d("GroupTableViewClickListener", "onColumnHeaderClicked has been clicked for x= " + columnPosition);
    }

    @Override
    public void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int columnPosition) {
        Log.d("GroupTableViewClickListener", "onColumnHeaderDoubleClicked has been clicked for x= " + columnPosition);
    }

    @Override
    public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int columnPosition) {
        Log.d("GroupTableViewClickListener", "onColumnHeaderLongPressed has been clicked for x= " + columnPosition);
    }

    @Override
    public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int rowPosition) {
        Log.d("GroupTableViewClickListener", "onRowHeaderClicked has been clicked for y= " + rowPosition);
    }

    @Override
    public void onRowHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int rowPosition) {
        Log.d("GroupTableViewClickListener", "onRowHeaderDoubleClicked has been clicked for y= " + rowPosition);
    }

    @Override
    public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int rowPosition) {
        Log.d("GroupTableViewClickListener", "onRowHeaderLongPressed has been clicked for y= " + rowPosition);
    }
}
