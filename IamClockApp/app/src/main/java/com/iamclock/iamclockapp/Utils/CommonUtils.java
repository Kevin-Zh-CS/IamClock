package com.iamclock.iamclockapp.Utils;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class CommonUtils {
    public static boolean CheckBooleanArrayAll(boolean[] array, boolean condition) {
        for (boolean b : array) if (b != condition) return false;
        return true;
    }

    public static String GenerateRepeatString(boolean[] array) {
        ArrayList<String> repeat_array = new ArrayList<>();

        if (array[0])
            repeat_array.add("SUN");
        if (array[1])
            repeat_array.add("MON");
        if (array[2])
            repeat_array.add("TUES");
        if (array[3])
            repeat_array.add("WED");
        if (array[4])
            repeat_array.add("THURS");
        if (array[5])
            repeat_array.add("FRI");
        if (array[6])
            repeat_array.add("SAT");

        return String.join(",", repeat_array);
    }
}