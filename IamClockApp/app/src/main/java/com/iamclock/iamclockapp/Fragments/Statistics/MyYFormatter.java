package com.iamclock.iamclockapp.Fragments.Statistics;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class MyYFormatter extends ValueFormatter {



    @Override
    public String getFormattedValue(float value) {
        int min = (int) (value / 60);
        int sec = (int) (value % 60);
        if(sec == 0){
            return min + ":00";
        }
        return min + ":" + sec;
    }
}
