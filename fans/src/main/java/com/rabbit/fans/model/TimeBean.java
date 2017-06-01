package com.rabbit.fans.model;

import java.util.List;

/**
 * Created by Vampire on 2017/5/27.
 */

public class TimeBean {

    /**
     * result : 0000
     * list : [{"timeContent":"17:00"}]
     * size : 1
     */

    private String result;
    private int size;
    private List<ListBean> list;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * timeContent : 17:00
         */

        private String timeContent;

        public String getTimeContent() {
            return timeContent;
        }

        public void setTimeContent(String timeContent) {
            this.timeContent = timeContent;
        }
    }
}
