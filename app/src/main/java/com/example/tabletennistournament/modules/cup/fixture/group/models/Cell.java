package com.example.tabletennistournament.modules.cup.fixture.group.models;

import androidx.annotation.Nullable;

public class Cell {
    @Nullable
    private Object mData;

    public Cell(@Nullable Object data) {
        this.mData = data;
    }

    @Nullable
    public Object getData() {
        return mData;
    }

    public void setData(@Nullable Object data) {
        mData = data;
    }

}
