package com.example.tabletennistournament.modules.cup.fixture.services;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.modules.cup.fixture.group.models.Cell;
import com.example.tabletennistournament.modules.cup.fixture.group.models.ColumnHeader;
import com.example.tabletennistournament.modules.cup.fixture.group.models.RowHeader;

public class GroupTableViewAdapter extends AbstractTableAdapter<ColumnHeader, RowHeader, Cell> {
    static class CellViewHolder extends AbstractViewHolder {

        final LinearLayout cell_container;
        final TextView cell_textview;

        public CellViewHolder(View itemView) {
            super(itemView);
            cell_container = itemView.findViewById(R.id.cell_container);
            cell_textview = itemView.findViewById(R.id.cell_data);
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateCellViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_view_cell_layout, parent, false);

        return new CellViewHolder(layout);
    }


    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, Cell cellItemModel, int columnPosition, int rowPosition) {
        CellViewHolder viewHolder = (CellViewHolder) holder;
        String cellData = cellItemModel.getData() != null ? cellItemModel.getData().toString() : "";
        viewHolder.cell_textview.setText(cellData);

        if (columnPosition == rowPosition) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0, 0, 0);
            viewHolder.cell_container.setLayoutParams(params);
            viewHolder.cell_textview.setBackgroundColor(Color.LTGRAY);
        }

        viewHolder.cell_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        viewHolder.cell_textview.requestLayout();
    }


    static class ColumnHeaderViewHolder extends AbstractViewHolder {
        final LinearLayout column_header_container;
        final TextView column_header_textView;

        public ColumnHeaderViewHolder(View itemView) {
            super(itemView);
            column_header_container = itemView.findViewById(R.id.column_header_container);
            column_header_textView = itemView.findViewById(R.id.column_header_textView);
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateColumnHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_view_column_header_layout, parent, false);

        return new ColumnHeaderViewHolder(layout);
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, ColumnHeader columnHeaderItemModel, int position) {
        ColumnHeaderViewHolder columnHeaderViewHolder = (ColumnHeaderViewHolder) holder;
        columnHeaderViewHolder.column_header_textView.setText(columnHeaderItemModel.getData().toString());

        columnHeaderViewHolder.column_header_container.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        columnHeaderViewHolder.column_header_textView.requestLayout();
    }

    static class RowHeaderViewHolder extends AbstractViewHolder {
        final TextView row_header_textview;

        public RowHeaderViewHolder(View itemView) {
            super(itemView);
            row_header_textview = itemView.findViewById(R.id.row_header_textView);
        }
    }

    @NonNull
    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_view_row_header_layout, parent, false);

        return new RowHeaderViewHolder(layout);
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, RowHeader rowHeaderItemModel, int position) {
        RowHeaderViewHolder rowHeaderViewHolder = (RowHeaderViewHolder) holder;
        rowHeaderViewHolder.row_header_textview.setText(rowHeaderItemModel.getData().toString());
    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_view_corner_layout, parent, false);
    }
}
