package com.vampire.shareforwechat.model.material;

import java.util.List;

/**
 * Created by 焕焕 on 2017/5/14.
 */

public class GraphicEntity {

    /**
     * articleList : [{"article":{"answer":"","articleId":"c29124648c254f12a638df319d183436","companyId":1,"content":"肾病都会变成尿毒症？肾病假知识排行榜第二名，但愿惊不到你！","count":14,"createTime":1494841590000,"ctime":"2017-05-15 17:46:30","id":"c29124648c254f12a638df319d183436","picUrl":"http://222.222.24.136:8334/picResource/wxcloudCompany/2017-05/d6ecdd8855b540b6b1b603a1e7b514a8.png","title":"有些谣言害人真不浅，不过\u201c谣言止于智者\u201d，今天石头医生从专业的角度帮大家认清楚几个典型的肾病假知识的真面目。前方高能，请注意！！","type":2,"url":"http://mp.weixin.qq.com/s/s8lMNGKSu2wekGPDHfCsNg","userId":"234c8622788440a091ad723899fca3f1"},"pictures":"","type":2}]
     * result : 0000
     */

    private String result;
    /**
     * article : {"answer":"","articleId":"c29124648c254f12a638df319d183436","companyId":1,"content":"肾病都会变成尿毒症？肾病假知识排行榜第二名，但愿惊不到你！","count":14,"createTime":1494841590000,"ctime":"2017-05-15 17:46:30","id":"c29124648c254f12a638df319d183436","picUrl":"http://222.222.24.136:8334/picResource/wxcloudCompany/2017-05/d6ecdd8855b540b6b1b603a1e7b514a8.png","title":"有些谣言害人真不浅，不过\u201c谣言止于智者\u201d，今天石头医生从专业的角度帮大家认清楚几个典型的肾病假知识的真面目。前方高能，请注意！！","type":2,"url":"http://mp.weixin.qq.com/s/s8lMNGKSu2wekGPDHfCsNg","userId":"234c8622788440a091ad723899fca3f1"}
     * pictures :
     * type : 2
     */

    private List<ArticleListBean> articleList;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<ArticleListBean> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<ArticleListBean> articleList) {
        this.articleList = articleList;
    }

    public static class ArticleListBean {
        /**
         * answer :
         * articleId : c29124648c254f12a638df319d183436
         * companyId : 1
         * content : 肾病都会变成尿毒症？肾病假知识排行榜第二名，但愿惊不到你！
         * count : 14
         * createTime : 1494841590000
         * ctime : 2017-05-15 17:46:30
         * id : c29124648c254f12a638df319d183436
         * picUrl : http://222.222.24.136:8334/picResource/wxcloudCompany/2017-05/d6ecdd8855b540b6b1b603a1e7b514a8.png
         * title : 有些谣言害人真不浅，不过“谣言止于智者”，今天石头医生从专业的角度帮大家认清楚几个典型的肾病假知识的真面目。前方高能，请注意！！
         * type : 2
         * url : http://mp.weixin.qq.com/s/s8lMNGKSu2wekGPDHfCsNg
         * userId : 234c8622788440a091ad723899fca3f1
         */

        private ArticleBean article;
        private String pictures;
        private int type;

        public ArticleBean getArticle() {
            return article;
        }

        public void setArticle(ArticleBean article) {
            this.article = article;
        }

        public String getPictures() {
            return pictures;
        }

        public void setPictures(String pictures) {
            this.pictures = pictures;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public static class ArticleBean {
            private String answer;
            private String articleId;
            private int companyId;
            private String content;
            private int count;
            private long createTime;
            private String ctime;
            private String id;
            private String picUrl;
            private String title;
            private int type;
            private String url;
            private String userId;

            public String getAnswer() {
                return answer;
            }

            public void setAnswer(String answer) {
                this.answer = answer;
            }

            public String getArticleId() {
                return articleId;
            }

            public void setArticleId(String articleId) {
                this.articleId = articleId;
            }

            public int getCompanyId() {
                return companyId;
            }

            public void setCompanyId(int companyId) {
                this.companyId = companyId;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public String getCtime() {
                return ctime;
            }

            public void setCtime(String ctime) {
                this.ctime = ctime;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }
        }
    }
}
