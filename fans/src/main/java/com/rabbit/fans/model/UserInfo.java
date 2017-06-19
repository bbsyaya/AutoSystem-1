package com.rabbit.fans.model;

/**
 * 登录返回
 * Created by Vampire on 2017/6/12.
 */

public class UserInfo {

    /**
     * result : 0000
     * db : {"createtime":1496635226000,"registrationId":"120c83f76015842441b","companyPhone":"ceshi","club":"46","province":"","missPass":"","registrationIdDaoHao":"1a0018970a909a0fd2c","companyId":"46","companyuserclubId":0,"passWord":"123456","city":"","companyuserId":"ceshi"}
     * retMessage : 登录成功
     */

    private String result;
    private DbBean db;
    private String retMessage;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public DbBean getDb() {
        return db;
    }

    public void setDb(DbBean db) {
        this.db = db;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
    }

    public static class DbBean {
        /**
         * createtime : 1496635226000
         * registrationId : 120c83f76015842441b
         * companyPhone : ceshi
         * club : 46
         * province :
         * missPass :
         * registrationIdDaoHao : 1a0018970a909a0fd2c
         * companyId : 46
         * companyuserclubId : 0
         * passWord : 123456
         * city :
         * companyuserId : ceshi
         */

        private long createtime;
        private String registrationId;
        private String companyPhone;
        private String club;
        private String province;
        private String missPass;
        private String registrationIdDaoHao;
        private String companyId;
        private String companyuserclubId;
        private String passWord;
        private String city;
        private String companyuserId;
        private String companyClubId;

        public String getCompanyClubId() {
            return companyClubId;
        }

        public void setCompanyClubId(String companyClubId) {
            this.companyClubId = companyClubId;
        }

        public String getCompanyuserclubId() {
            return companyuserclubId;
        }

        public void setCompanyuserclubId(String companyuserclubId) {
            this.companyuserclubId = companyuserclubId;
        }

        public long getCreatetime() {
            return createtime;
        }

        public void setCreatetime(long createtime) {
            this.createtime = createtime;
        }

        public String getRegistrationId() {
            return registrationId;
        }

        public void setRegistrationId(String registrationId) {
            this.registrationId = registrationId;
        }

        public String getCompanyPhone() {
            return companyPhone;
        }

        public void setCompanyPhone(String companyPhone) {
            this.companyPhone = companyPhone;
        }

        public String getClub() {
            return club;
        }

        public void setClub(String club) {
            this.club = club;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getMissPass() {
            return missPass;
        }

        public void setMissPass(String missPass) {
            this.missPass = missPass;
        }

        public String getRegistrationIdDaoHao() {
            return registrationIdDaoHao;
        }

        public void setRegistrationIdDaoHao(String registrationIdDaoHao) {
            this.registrationIdDaoHao = registrationIdDaoHao;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }



        public String getPassWord() {
            return passWord;
        }

        public void setPassWord(String passWord) {
            this.passWord = passWord;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCompanyuserId() {
            return companyuserId;
        }

        public void setCompanyuserId(String companyuserId) {
            this.companyuserId = companyuserId;
        }
    }
}
