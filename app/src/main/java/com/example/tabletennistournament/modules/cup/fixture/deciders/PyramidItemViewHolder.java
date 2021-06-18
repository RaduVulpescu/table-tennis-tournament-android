package com.example.tabletennistournament.modules.cup.fixture.deciders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabletennistournament.R;

public class PyramidItemViewHolder extends RecyclerView.ViewHolder {

    public final TextView pyramidTitle;
    public final RecyclerView pyramidMatches;

    public PyramidItemViewHolder(@NonNull View view) {
        super(view);
        this.pyramidTitle = itemView.findViewById(R.id.text_view_pyramid_title);
        this.pyramidMatches = itemView.findViewById(R.id.recycler_view_pyramid_matches);
    }

    @NonNull
    public static PyramidItemViewHolder create(@NonNull ViewGroup parent) {
        return new PyramidItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pyramid_list_item, parent, false));
    }

}
