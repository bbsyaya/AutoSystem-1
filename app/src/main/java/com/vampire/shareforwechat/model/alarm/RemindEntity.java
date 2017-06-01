package com.vampire.shareforwechat.model.alarm;

import org.litepal.crud.DataSupport;

/**
 * Created by 焕焕 on 2017/5/18.
 */

public class RemindEntity extends DataSupport{
    private int id;
    private String content;//药品名称
    private boolean isRemind;//是否提醒
    private int treatment;//疗程
    private int countAll;//总次数
    private int dose;//每日服药剂量
    private int firstHour;
    private int firstMinute;
    private int secondHour;
    private int secondMinute;
    private int thirdHour;
    private int thirdMinute;
    private int forthHour;
    private int forthMinute;
    private int fifthHour;
    private int fifthMinute;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getThirdHour() {
        return thirdHour;
    }

    public void setThirdHour(int thirdHour) {
        this.thirdHour = thirdHour;
    }

    public int getThirdMinute() {
        return thirdMinute;
    }

    public void setThirdMinute(int thirdMinute) {
        this.thirdMinute = thirdMinute;
    }

    public int getForthHour() {
        return forthHour;
    }

    public void setForthHour(int forthHour) {
        this.forthHour = forthHour;
    }

    public int getForthMinute() {
        return forthMinute;
    }

    public void setForthMinute(int forthMinute) {
        this.forthMinute = forthMinute;
    }

    public int getFifthHour() {
        return fifthHour;
    }

    public void setFifthHour(int fifthHour) {
        this.fifthHour = fifthHour;
    }

    public int getFifthMinute() {
        return fifthMinute;
    }

    public void setFifthMinute(int fifthMinute) {
        this.fifthMinute = fifthMinute;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRemind() {
        return isRemind;
    }

    public void setRemind(boolean remind) {
        isRemind = remind;
    }

    public int getTreatment() {
        return treatment;
    }

    public void setTreatment(int treatment) {
        this.treatment = treatment;
    }

    public int getCountAll() {
        return countAll;
    }

    public void setCountAll(int countAll) {
        this.countAll = countAll;
    }

    public int getDose() {
        return dose;
    }

    public void setDose(int dose) {
        this.dose = dose;
    }

    public int getFirstHour() {
        return firstHour;
    }

    public void setFirstHour(int firstHour) {
        this.firstHour = firstHour;
    }

    public int getFirstMinute() {
        return firstMinute;
    }

    public void setFirstMinute(int firstMinute) {
        this.firstMinute = firstMinute;
    }

    public int getSecondHour() {
        return secondHour;
    }

    public void setSecondHour(int secondHour) {
        this.secondHour = secondHour;
    }

    public int getSecondMinute() {
        return secondMinute;
    }

    public void setSecondMinute(int secondMinute) {
        this.secondMinute = secondMinute;
    }
}
