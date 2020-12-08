package com.iamclock.iamclockapp.Fragments.Dashboard;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

public class MyXFormatter extends ValueFormatter {

    private List<String> xData;
    public MyXFormatter(List<String> xData) {
        this.xData = xData;
    }

    @Override
    public String getFormattedValue(float value) {
        if(((int)value >=0 && (int)value < xData.size()))
            return xData.get((int) value);
        else
            return "";
    }
}
