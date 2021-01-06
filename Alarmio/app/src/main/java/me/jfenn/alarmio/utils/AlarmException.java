package me.jfenn.alarmio.utils;

public class AlarmException extends Exception {
    private String msg;

    public AlarmException(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return msg;
    }
}
