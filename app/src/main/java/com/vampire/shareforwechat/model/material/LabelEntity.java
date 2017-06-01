package com.vampire.shareforwechat.model.material;

import java.util.List;

/**
 * Created by 焕焕 on 2017/5/14.
 */

public class LabelEntity {

    /**
     * labelList : [{"companyId":1,"creatime":1488694609000,"labelId":"7373768340fd4ae7ac9570bef84d2490","name":"其他案例**"},{"companyId":1,"creatime":1484559891000,"labelId":"25eface714444affbe2425f7cb69b67c","name":"日常衣食住行"},{"companyId":1,"creatime":1484560413000,"labelId":"d0b8540999c242bb8b1b068242b17bfc","name":"糖肾案例"},{"companyId":1,"creatime":1487124764000,"labelId":"6a73c1e0a94f4fc98254e41170c4c458","name":"回复类互动"},{"companyId":1,"creatime":1488693441000,"labelId":"b31634fdc8be4678992de7817f4ffe9c","name":"食之有道**"}]
     * reMessage : 成功
     * result : 0000
     */

    private String reMessage;
    private String result;
    /**
     * companyId : 1
     * creatime : 1488694609000
     * labelId : 7373768340fd4ae7ac9570bef84d2490
     * name : 其他案例**
     */

    private List<LabelListBean> labelList;

    public String getReMessage() {
        return reMessage;
    }

    public void setReMessage(String reMessage) {
        this.reMessage = reMessage;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<LabelListBean> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<LabelListBean> labelList) {
        this.labelList = labelList;
    }

    public static class LabelListBean {
        private int companyId;
        private long creatime;
        private String labelId;
        private String name;

        public int getCompanyId() {
            return companyId;
        }

        public void setCompanyId(int companyId) {
            this.companyId = companyId;
        }

        public long getCreatime() {
            return creatime;
        }

        public void setCreatime(long creatime) {
            this.creatime = creatime;
        }

        public String getLabelId() {
            return labelId;
        }

        public void setLabelId(String labelId) {
            this.labelId = labelId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
