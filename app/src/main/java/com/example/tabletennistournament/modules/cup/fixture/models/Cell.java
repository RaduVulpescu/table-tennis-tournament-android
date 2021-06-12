package com.example.tabletennistournament.modules.cup.fixture.models;

import androidx.annotation.Nullable;

public class Cell {
    @Nullable
    private final Object mData;

    public Cell(@Nullable Object data) {
        this.mData = data;
    }

    @Nullable
    public Object getData() {
        return mData;
    }
}