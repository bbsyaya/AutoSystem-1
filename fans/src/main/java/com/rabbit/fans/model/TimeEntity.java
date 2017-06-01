package com.rabbit.fans.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Vampire on 2017/5/27.
 */

public class TimeEntity extends DataSupport {
    private int id;
    private int hour;
    private int minute;
    private int second;
    private int timeId;//暂时不用

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
