package com.dfcj.videoim.util.other;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.LogStrategy;

public class CustomLogCatStrategy implements LogStrategy {

    @Override
    public void log(int priority, @Nullable String tag, @NonNull String message) {
        Log.println(priority, randomKey() + tag, message);
    }

    private int last;

    private String randomKey() {
        int random = (int) (10 * Math.random());
        if (random == last) {
            random = (random + 1) % 10;
        }
        last = random;
        return String.valueOf(random);
    }


}
