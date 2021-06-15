package com.example.tabletennistournament.modules.cup.fixture.models;

public class NumberCell extends Cell {
    private Integer number;

    public NumberCell(int number) {
        super(number);
        this.number = number;
    }

    public void increment() {
        this.number++;
        super.setData(number);
    }

    public void decrement() {
        this.number--;
        super.setData(number);
    }
}
