package com.example.tabletennistournament.services;

import com.example.tabletennistournament.R;
import com.example.tabletennistournament.enums.Level;

public class Common {

    public static int getPlayerLevelIcon(Level level) {
        if (level == null) {
            return 0;
        }

        switch (level) {
            case Beginner:
                return R.drawable.ic_outline_looks_two_24;
            case Intermediate:
                return R.drawable.ic_outline_looks_3_24;
            case Advanced:
                return R.drawable.ic_outline_looks_4_24;
            case Open:
                return R.drawable.ic_outline_looks_5_24;
            default:
                return 0;
        }
    }

}
