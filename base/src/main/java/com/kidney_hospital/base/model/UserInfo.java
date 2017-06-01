package com.kidney_hospital.base.model;

/**
 * Created by Administrator on 2017/5/3.
 */

public class UserInfo {

    /**
     * result : 0000
     * db : {"uid":"","phone":"18617599820","registrationId":"111111","cardPic":"http://222.222.24.133:8099/picResource/sbs/user/2017-02/36217e14bbd14bc9b9ee3803d88cfef5.jpg","remark":"你是我的眼。","hospitalId":"0e88de88467140538acb325aa9fa984d","type":0,"doctorPhone":"1234569870","password":"3d4f2bf07dc1be38b20cd6e46949a1071f9d0e3d","city":"石家庄","attpStates":2,"addtime":1486781193000,"username":"老苑","psan":"","userId":"27e1800f13a34698b58e4ad2d9b16c02","age":23,"goodNums":0,"province":"河北","laboratoryHave":0,"card":5994,"headUrl":"http://222.222.24.133:8099/picResource/sbs/user/2017-02/42482a1197ea4823b72878664098ffd5.png","appraiseNums":0,"honor":"让我们一起摇摆！看看","job":"住院医师","health":100,"country":"","sina":"","hospitalAddress":"","email":"","attp":"1234567890","userStatus":0,"attpUrl":"http://222.222.24.133:8099/picResource/sbs/user/2017-02/86fbd13e3ff249b0a4b669c9f8de20e6.png,http://222.222.24.133:8099/picResource/sbs/user/2017-02/b0e1c32611cc48cd97222b1440fee115.png","language":"zh","hospital":"呼和浩特市玉泉区妇幼保健院","sex":1,"webchat":"","submitTime":1486781579000,"disease":"歼击机","twoDimension":"http://222.222.24.133:8099/picResource/sbs/user/2017-02/eade1c14f92e4aea811fdbeeb2206a18104633.png","autoProvince":"河北","name":"苑亚欣","mycard":"","grade":1,"qq":"","goodPercent":"1.0000","SYMPTOM":"","isDoctor":1,"autoCity":"石家庄","attestation":1,"section":"内科 咽炎","intro":"1","sectionId":"11491793ba274e198bcae8d02940f7e9","address":"","secret":"88982011a1a05bb7202653717fe5a09799ba9cac","isShow":0}
     * returnMessage : 成功!
     */

    private String result;
    private DbBean db;
    private String returnMessage;

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

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public static class DbBean {
        /**
         * uid :
         * phone : 18617599820
         * registrationId : 111111
         * cardPic : http://222.222.24.133:8099/picResource/sbs/user/2017-02/36217e14bbd14bc9b9ee3803d88cfef5.jpg
         * remark : 你是我的眼。
         * hospitalId : 0e88de88467140538acb325aa9fa984d
         * type : 0
         * doctorPhone : 1234569870
         * password : 3d4f2bf07dc1be38b20cd6e46949a1071f9d0e3d
         * city : 石家庄
         * attpStates : 2
         * addtime : 1486781193000
         * username : 老苑
         * psan :
         * userId : 27e1800f13a34698b58e4ad2d9b16c02
         * age : 23
         * goodNums : 0
         * province : 河北
         * laboratoryHave : 0
         * card : 5994
         * headUrl : http://222.222.24.133:8099/picResource/sbs/user/2017-02/42482a1197ea4823b72878664098ffd5.png
         * appraiseNums : 0
         * honor : 让我们一起摇摆！看看
         * job : 住院医师
         * health : 100.0
         * country :
         * sina :
         * hospitalAddress :
         * email :
         * attp : 1234567890
         * userStatus : 0
         * attpUrl : http://222.222.24.133:8099/picResource/sbs/user/2017-02/86fbd13e3ff249b0a4b669c9f8de20e6.png,http://222.222.24.133:8099/picResource/sbs/user/2017-02/b0e1c32611cc48cd97222b1440fee115.png
         * language : zh
         * hospital : 呼和浩特市玉泉区妇幼保健院
         * sex : 1
         * webchat :
         * submitTime : 1486781579000
         * disease : 歼击机
         * twoDimension : http://222.222.24.133:8099/picResource/sbs/user/2017-02/eade1c14f92e4aea811fdbeeb2206a18104633.png
         * autoProvince : 河北
         * name : 苑亚欣
         * mycard :
         * grade : 1
         * qq :
         * goodPercent : 1.0000
         * SYMPTOM :
         * isDoctor : 1
         * autoCity : 石家庄
         * attestation : 1
         * section : 内科 咽炎
         * intro : 1
         * sectionId : 11491793ba274e198bcae8d02940f7e9
         * address :
         * secret : 88982011a1a05bb7202653717fe5a09799ba9cac
         * isShow : 0
         */

        private String uid;
        private String phone;
        private String registrationId;
        private String cardPic;
        private String remark;
        private String hospitalId;
        private int type;
        private String doctorPhone;
        private String password;
        private String city;
        private int attpStates;
        private long addtime;
        private String username;
        private String psan;
        private String userId;
        private int age;
        private int goodNums;
        private String province;
        private int laboratoryHave;
        private int card;
        private String headUrl;
        private int appraiseNums;
        private String honor;
        private String job;
        private double health;
        private String country;
        private String sina;
        private String hospitalAddress;
        private String email;
        private String attp;
        private int userStatus;
        private String attpUrl;
        private String language;
        private String hospital;
        private int sex;
        private String webchat;
        private long submitTime;
        private String disease;
        private String twoDimension;
        private String autoProvince;
        private String name;
        private String mycard;
        private int grade;
        private String qq;
        private String goodPercent;
        private String SYMPTOM;
        private int isDoctor;
        private String autoCity;
        private int attestation;
        private String section;
        private String intro;
        private String sectionId;
        private String address;
        private String secret;
        private int isShow;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getRegistrationId() {
            return registrationId;
        }

        public void setRegistrationId(String registrationId) {
            this.registrationId = registrationId;
        }

        public String getCardPic() {
            return cardPic;
        }

        public void setCardPic(String cardPic) {
            this.cardPic = cardPic;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getHospitalId() {
            return hospitalId;
        }

        public void setHospitalId(String hospitalId) {
            this.hospitalId = hospitalId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDoctorPhone() {
            return doctorPhone;
        }

        public void setDoctorPhone(String doctorPhone) {
            this.doctorPhone = doctorPhone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getAttpStates() {
            return attpStates;
        }

        public void setAttpStates(int attpStates) {
            this.attpStates = attpStates;
        }

        public long getAddtime() {
            return addtime;
        }

        public void setAddtime(long addtime) {
            this.addtime = addtime;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPsan() {
            return psan;
        }

        public void setPsan(String psan) {
            this.psan = psan;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getGoodNums() {
            return goodNums;
        }

        public void setGoodNums(int goodNums) {
            this.goodNums = goodNums;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public int getLaboratoryHave() {
            return laboratoryHave;
        }

        public void setLaboratoryHave(int laboratoryHave) {
            this.laboratoryHave = laboratoryHave;
        }

        public int getCard() {
            return card;
        }

        public void setCard(int card) {
            this.card = card;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public int getAppraiseNums() {
            return appraiseNums;
        }

        public void setAppraiseNums(int appraiseNums) {
            this.appraiseNums = appraiseNums;
        }

        public String getHonor() {
            return honor;
        }

        public void setHonor(String honor) {
            this.honor = honor;
        }

        public String getJob() {
            return job;
        }

        public void setJob(String job) {
            this.job = job;
        }

        public double getHealth() {
            return health;
        }

        public void setHealth(double health) {
            this.health = health;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getSina() {
            return sina;
        }

        public void setSina(String sina) {
            this.sina = sina;
        }

        public String getHospitalAddress() {
            return hospitalAddress;
        }

        public void setHospitalAddress(String hospitalAddress) {
            this.hospitalAddress = hospitalAddress;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAttp() {
            return attp;
        }

        public void setAttp(String attp) {
            this.attp = attp;
        }

        public int getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(int userStatus) {
            this.userStatus = userStatus;
        }

        public String getAttpUrl() {
            return attpUrl;
        }

        public void setAttpUrl(String attpUrl) {
            this.attpUrl = attpUrl;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getHospital() {
            return hospital;
        }

        public void setHospital(String hospital) {
            this.hospital = hospital;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getWebchat() {
            return webchat;
        }

        public void setWebchat(String webchat) {
            this.webchat = webchat;
        }

        public long getSubmitTime() {
            return submitTime;
        }

        public void setSubmitTime(long submitTime) {
            this.submitTime = submitTime;
        }

        public String getDisease() {
            return disease;
        }

        public void setDisease(String disease) {
            this.disease = disease;
        }

        public String getTwoDimension() {
            return twoDimension;
        }

        public void setTwoDimension(String twoDimension) {
            this.twoDimension = twoDimension;
        }

        public String getAutoProvince() {
            return autoProvince;
        }

        public void setAutoProvince(String autoProvince) {
            this.autoProvince = autoProvince;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMycard() {
            return mycard;
        }

        public void setMycard(String mycard) {
            this.mycard = mycard;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public String getQq() {
            return qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public String getGoodPercent() {
            return goodPercent;
        }

        public void setGoodPercent(String goodPercent) {
            this.goodPercent = goodPercent;
        }

        public String getSYMPTOM() {
            return SYMPTOM;
        }

        public void setSYMPTOM(String SYMPTOM) {
            this.SYMPTOM = SYMPTOM;
        }

        public int getIsDoctor() {
            return isDoctor;
        }

        public void setIsDoctor(int isDoctor) {
            this.isDoctor = isDoctor;
        }

        public String getAutoCity() {
            return autoCity;
        }

        public void setAutoCity(String autoCity) {
            this.autoCity = autoCity;
        }

        public int getAttestation() {
            return attestation;
        }

        public void setAttestation(int attestation) {
            this.attestation = attestation;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getSectionId() {
            return sectionId;
        }

        public void setSectionId(String sectionId) {
            this.sectionId = sectionId;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public int getIsShow() {
            return isShow;
        }

        public void setIsShow(int isShow) {
            this.isShow = isShow;
        }
    }
}
