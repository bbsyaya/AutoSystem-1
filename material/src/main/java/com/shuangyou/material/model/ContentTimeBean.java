package com.shuangyou.material.model;

import java.util.List;

/**
 * Created by Vampire on 2017/5/27.
 */

public class ContentTimeBean {

    /**
     * result : 0000
     * list : [{"id":"c7460b5ed94b4dfdb077e1f4b47db544","time":"17:10","date":"2017-05-28"}]
     * retMessage : 操作成功！
     * size : 1
     */

    private String result;
    private String retMessage;
    private int size;
    private List<ListBean> list;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
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
         * id : c7460b5ed94b4dfdb077e1f4b47db544
         * time : 17:10
         * date : 2017-05-28
         */

        private String id;
        private String time;
        private String date;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
