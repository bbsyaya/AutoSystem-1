package com.shuangyou.material.model;

import java.util.List;

/**
 * Created by Vampire on 2017/5/27.
 */

public class ContentBean {

    /**
     * result : 0000
     * list : [{"content":"啊啊啊啊啊啊","id":11,"title":"11","picUrl":"http://222.222.24.149:9021/~zirui/images/2017522142729395.jpg","used":0,"type":1,"daily":false,"url":"https://v.qq.com/x/page/e0370ncajt3.html"}]
     * retMessage : 操作成功
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
         * content : 啊啊啊啊啊啊
         * id : 11
         * title : 11
         * picUrl : http://222.222.24.149:9021/~zirui/images/2017522142729395.jpg
         * used : 0
         * type : 1
         * daily : false
         * url : https://v.qq.com/x/page/e0370ncajt3.html
         */

        private String content;
        private String id;
        private String title;
        private String picUrl;
        private int used;
        private int type;
        private boolean daily;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public int getUsed() {
            return used;
        }

        public void setUsed(int used) {
            this.used = used;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public boolean isDaily() {
            return daily;
        }

        public void setDaily(boolean daily) {
            this.daily = daily;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
