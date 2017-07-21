package com.shuangyou.material.model;

/**
 * Created by Vampire on 2017/7/14.
 */

public class UserInfo {

    /**
     * result : 0000
     * db : {"registrationId":"120c83f760159356b46","companyPhone":"ceshi","missPass":"","xAddress":"","passWord":"123456","companyuserclubId":"16cfce2bd751445bad3df26885f88d73","version":"0","city":"吉林","createtime":1496635226000,"companyClubId":1,"club":"46","province":"吉林","yAddress":"","registrationIdDaoHao":"13065ffa4e39808b7fc","companyId":"46","clubName":"22","companyuserId":"ceshi"}
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
         * registrationId : 120c83f760159356b46
         * companyPhone : ceshi
         * missPass :
         * xAddress :
         * passWord : 123456
         * companyuserclubId : 16cfce2bd751445bad3df26885f88d73
         * version : 0
         * city : 吉林
         * createtime : 1496635226000
         * companyClubId : 1
         * club : 46
         * province : 吉林
         * yAddress :
         * registrationIdDaoHao : 13065ffa4e39808b7fc
         * companyId : 46
         * clubName : 22
         * companyuserId : ceshi
         */

        private String registrationId;
        private String companyPhone;
        private String missPass;
        private String xAddress;
        private String passWord;
        private String companyuserclubId;
        private String version;
        private String city;
        private long createtime;
        private int companyClubId;
        private String club;
        private String province;
        private String yAddress;
        private String registrationIdDaoHao;
        private String companyId;
        private String clubName;
        private String companyuserId;

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

        public String getMissPass() {
            return missPass;
        }

        public void setMissPass(String missPass) {
            this.missPass = missPass;
        }

        public String getXAddress() {
            return xAddress;
        }

        public void setXAddress(String xAddress) {
            this.xAddress = xAddress;
        }

        public String getPassWord() {
            return passWord;
        }

        public void setPassWord(String passWord) {
            this.passWord = passWord;
        }

        public String getCompanyuserclubId() {
            return companyuserclubId;
        }

        public void setCompanyuserclubId(String companyuserclubId) {
            this.companyuserclubId = companyuserclubId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public long getCreatetime() {
            return createtime;
        }

        public void setCreatetime(long createtime) {
            this.createtime = createtime;
        }

        public int getCompanyClubId() {
            return companyClubId;
        }

        public void setCompanyClubId(int companyClubId) {
            this.companyClubId = companyClubId;
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

        public String getYAddress() {
            return yAddress;
        }

        public void setYAddress(String yAddress) {
            this.yAddress = yAddress;
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

        public String getClubName() {
            return clubName;
        }

        public void setClubName(String clubName) {
            this.clubName = clubName;
        }

        public String getCompanyuserId() {
            return companyuserId;
        }

        public void setCompanyuserId(String companyuserId) {
            this.companyuserId = companyuserId;
        }
    }
}
