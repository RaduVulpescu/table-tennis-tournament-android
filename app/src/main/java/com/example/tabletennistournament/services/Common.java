package com.example.tabletennistournament.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.example.tabletennistournament.R;
import com.example.tabletennistournament.enums.Level;

import java.util.Locale;

public class Common {

    private final static String REQUEST_TIMEOUT = "30000";

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

    public static Request<?> increaseTimeout(@NonNull Request<?> request) {
        return request.setRetryPolicy(new DefaultRetryPolicy(
                Integer.parseInt(REQUEST_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @NonNull
    public static String getValueOrNA(@Nullable Double quality) {
        if (quality == null) {
            return "N/A";
        }

        return String.format(Locale.getDefault(), "%.2f", quality);
    }

}
