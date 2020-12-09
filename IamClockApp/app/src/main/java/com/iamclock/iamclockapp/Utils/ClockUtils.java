package com.iamclock.iamclockapp.Utils;

import java.text.SimpleDateFormat;

public class ClockUtils {
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm");

    private ClockUtils() {
        throw new AssertionError();
    }

    public static String GetReadableTime(long time) {
        return TIME_FORMAT.format(time);
    }

    public static String GetReadableDays(boolean[] days) {
        if (CommonUtils.CheckBooleanArrayAll(days, true)) {
            return "DAILY";
        } else if (CommonUtils.CheckBooleanArrayAll(days, false)) {
            return "ONCE";
        } else if (days[0]
                && !days[1]
                && !days[2]
                && !days[3]
                && !days[4]
                && !days[5]
                && days[6]) {
            return "WEEKEND";
        } else if (!days[0]
                && days[1]
                && days[2]
                && days[3]
                && days[4]
                && days[5]
                && !days[6]) {
            return "WORKDAY";
        } else {
            return CommonUtils.GenerateRepeatString(days);
        }
    }
}
