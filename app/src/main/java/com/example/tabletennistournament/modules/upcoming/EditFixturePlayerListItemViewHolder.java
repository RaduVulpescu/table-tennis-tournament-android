package com.example.tabletennistournament.modules.upcoming;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabletennistournament.R;

public class EditFixturePlayerListItemViewHolder extends RecyclerView.ViewHolder {

    public final AutoCompleteTextView autoCompleteTextViewPlayer;

    public EditFixturePlayerListItemViewHolder(@NonNull View view) {
        super(view);

        this.autoCompleteTextViewPlayer = itemView.findViewById(R.id.auto_complete_text_view_edit_fixture_player);
    }

    @NonNull
    public static EditFixturePlayerListItemViewHolder create(@NonNull ViewGroup parent) {
        return new EditFixturePlayerListItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_fixture_players_list_item, parent, false));
    }

}
