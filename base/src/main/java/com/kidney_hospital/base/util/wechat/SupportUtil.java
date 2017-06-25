package com.kidney_hospital.base.util.wechat;

/**
 * Created by Vampire on 2017/4/21.
 */

public class SupportUtil {
    private final String VERSION_6313 = "6.3.13";
    private final String VERSION_6325 = "6.3.25";
    private final String VERSION_657 = "6.5.7";
    private final String VERSION_654 = "6.5.4";
    private final String VERSION_658 = "6.5.8";
    private final String VERSION_653 = "6.5.3";
    private final String[] SUPPORT_VERSION = new String[]{
            VERSION_6325
    };
    private String searchItemId;

    public SupportUtil(String version) {
        if (version.equals(VERSION_6325)) {
            Ver6325();
        } else if (version.equals(VERSION_657)) {
            Ver657();
        } else if (version.equals(VERSION_654)) {
            Ver654();
        }else if (version.equals(VERSION_658)){
            Ver658();
        }else if (version.contains(VERSION_6313)){
            Ver6313();
        }else if (version.contains(VERSION_653)){
            Ver653();
        }
    }



    // 好友选择界面
    private final String selectUI = "com.tencent.mm.ui.contact.SelectContactUI";
    // 聊天界面
    private final String chattingUI = "com.tencent.mm.ui.chatting.ChattingUI";
    // 群组详情界面
    private final String chatRoomInfo = "com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI";
    // 首页
    private final String launcherUI = "com.tencent.mm.ui.LauncherUI";
    //加载弹窗
    private final String progressDialog = "com.tencent.mm.ui.base.p";
    //弹出 Dialog
    private final String dialog = "com.tencent.mm.ui.base.h";
    //发起群聊弹框
    private final String createGroup = "android.widget.FrameLayout";
    // 搜索页面
    private final String searchUI = "com.tencent.mm.plugin.search.ui.FTSMainUI";
    // 用户信息页面
    private final String contactInfoUI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
    //新的朋友页面
    private final String FMesssageConversationUI = "com.tencent.mm.plugin.subapp.ui.friend.FMessageConversationUI";
    //分享页面
    private  String snsUploadUi = "com.tencent.mm.plugin.sns.ui.SnsUploadUI";
    //选取谁可以看
    private final String snsLabelUi = "com.tencent.mm.plugin.sns.ui.SnsLabelUI";


    //备注信息页面
    private String contactRemarkInfoModUi = "com.tencent.mm.ui.contact.ContactRemarkInfoModUI";
    //备注信息页面
    private String modRemarkNameUI = "com.tencent.mm.ui.contact.ModRemarkNameUI";
    //隐私对话框
    private String privateDialog = "com.tencent.mm.ui.base.h";
    //隐私对话框的确定按钮
    private String privateDialogButtonOK = "com.tencent.mm:id/ad8";
    //删除对话框
    private  String deleteDialog = "com.tencent.mm.ui.base.k";
    //删除文本框
    private String tv_Delete = "com.tencent.mm:id/fi";
    //还没有推荐的朋友
    private String tv_noFans = "com.tencent.mm:id/axp";

    public String getTv_noFans() {
        return tv_noFans;
    }

    public String getTv_Delete() {
        return tv_Delete;
    }

    public String getDeleteDialog() {
        return deleteDialog;
    }

    public String getPrivateDialogButtonOK() {
        return privateDialogButtonOK;
    }

    public String getPrivateDialog() {
        return privateDialog;
    }

    public String getModRemarkNameUI() {
        return modRemarkNameUI;
    }

    public String getContactRemarkInfoModUi() {
        return contactRemarkInfoModUi;
    }

    private String dialogHintTextId;
    private String contactInfoUiNickNameId;
    private String contactInfoUIGenderId;
    private String fTSMainUICleanEtId;
    private String fTSMainUIItemId;
    private String fTSMainUISearchEtId;
    private String launcherPagerId;
    private String launcherNewFriendId;
    private String FMesssageConversationUI_LV_ID;
    private String FMesssageConversationUI_ADD_BTN_ID;
    private String whoCanSeeItemId;
    private String nickNameTvId;
    private String searchEditId;
    private String noSuchTextId;
    private String checkItemIvId;
    private String etContentId;


    private String tv_setRemark;//设置备注和标签
    private String tv_fillIn;//填入按钮
    private String tv_remark_save;//备注名的保存按钮

    public String getTv_remark_save() {
        return tv_remark_save;
    }

    public String getTv_fillIn() {
        return tv_fillIn;
    }

    public String getTv_setRemark() {
        return tv_setRemark;
    }

    public String getCreateGroup() {
        return createGroup;
    }

    private String selectUIListViewId = "com.tencent.mm:id/bd3";
    private String selectUICheckBoxId = "com.tencent.mm:id/lg";
    private String selectUIItemId = "com.tencent.mm:id/a5s";
    private String selectUINickNameId = "com.tencent.mm:id/ib";
    private String chattingUIMessageId = "com.tencent.mm:id/h7";
    private String groupInfoListViewId = "android:id/list";
    private String sendRequestBtnId = "com.tencent.mm:id/eu";//确定按钮
    private String moreAcionBtnId = "com.tencent.mm:id/dq";
    private String createGroupLaunchBtnId = "com.tencent.mm:id/aes";
    private String quiteGroupBtnId = "android:id/title";
    private String makeSureQuiteId = "com.tencent.mm:id/dr";
    private void Ver6313() {
        sendRequestBtnId = "com.tencent.mm:id/cds";
        launcherPagerId = "com.tencent.mm:id/ig";
        FMesssageConversationUI_ADD_BTN_ID = "com.tencent.mm:id/ah1";//添加按钮
        FMesssageConversationUI_LV_ID = "com.tencent.mm:id/ahc";//通讯录 listview
        launcherNewFriendId = "com.tencent.mm:id/kr";
        etContentId = "com.tencent.mm:id/bo0";

        tv_setRemark = "com.tencent.mm:id/c6c";
        tv_fillIn = "com.tencent.mm:id/d3";
        tv_remark_save = "com.tencent.mm:id/cdr";

    }
    private void Ver6325() {
        selectUIListViewId = "com.tencent.mm:id/bd3";
        selectUICheckBoxId = "com.tencent.mm:id/lg";
        selectUIItemId = "com.tencent.mm:id/a5s";
        selectUINickNameId = "com.tencent.mm:id/ib";
        chattingUIMessageId = "com.tencent.mm:id/h7";
        groupInfoListViewId = "android:id/list";
        sendRequestBtnId = "com.tencent.mm:id/eu";
        moreAcionBtnId = "com.tencent.mm:id/dq";
        createGroupLaunchBtnId = "com.tencent.mm:id/aes";
        quiteGroupBtnId = "android:id/title";
        makeSureQuiteId = "com.tencent.mm:id/dr";


    }
    private void Ver658() {
        sendRequestBtnId = "com.tencent.mm:id/gl";
        snsUploadUi = "com.tencent.mm.plugin.sns.ui.En_c4f742e5";
        launcherPagerId = "com.tencent.mm:id/asa";
        launcherNewFriendId = "com.tencent.mm:id/ax4";
        etContentId = "com.tencent.mm:id/cst";

        FMesssageConversationUI_ADD_BTN_ID = "com.tencent.mm:id/axf";//添加按钮
        FMesssageConversationUI_LV_ID = "com.tencent.mm:id/axl";//通讯录 listview


        tv_setRemark = "com.tencent.mm:id/af6";
        tv_fillIn = "com.tencent.mm:id/agc";
        tv_remark_save = "com.tencent.mm:id/gk";

        privateDialogButtonOK = "com.tencent.mm:id/ad8";
        tv_Delete = "com.tencent.mm:id/fi";
        tv_noFans = "com.tencent.mm:id/axp";
    }
    private void Ver653(){
        sendRequestBtnId = "com.tencent.mm:id/g_";
        etContentId = "com.tencent.mm:id/cio";
        launcherPagerId = "com.tencent.mm:id/bgp";
        launcherNewFriendId = "com.tencent.mm:id/arm";
        FMesssageConversationUI_ADD_BTN_ID = "com.tencent.mm:id/ary";
        FMesssageConversationUI_LV_ID = "com.tencent.mm:id/as4";
        tv_setRemark = "com.tencent.mm:id/aby";
        tv_fillIn = "com.tencent.mm:id/ad9";
        tv_remark_save = "com.tencent.mm:id/g9";


        tv_Delete = "com.tencent.mm:id/f7";
    }
    private void Ver654() {
        dialogHintTextId = "com.tencent.mm:id/bpn";
        contactInfoUiNickNameId = "com.tencent.mm:id/la";
        contactInfoUIGenderId = "com.tencent.mm:id/ad_";
        fTSMainUICleanEtId = "com.tencent.mm:id/gs";
        fTSMainUISearchEtId = "com.tencent.mm:id/gr";
        fTSMainUIItemId = "com.tencent.mm:id/axr";
        // TODO: 2017/5/16  change
        launcherPagerId = "com.tencent.mm:id/bkg";
        launcherNewFriendId = "com.tencent.mm:id/aue";
        FMesssageConversationUI_LV_ID = "com.tencent.mm:id/auv";
        FMesssageConversationUI_ADD_BTN_ID = "com.tencent.mm:id/aup"; //添加按钮 text 值添加
        whoCanSeeItemId = "com.tencent.mm:id/cok";
        sendRequestBtnId = "com.tencent.mm:id/gd";//完成按钮
        nickNameTvId = "com.tencent.mm:id/gh";
        searchEditId = "com.tencent.mm:id/ag9";
        noSuchTextId = "com.tencent.mm:id/ez";
        checkItemIvId = "com.tencent.mm:id/cl1";
        searchItemId = "com.tencent.mm:id/axr";
        etContentId = "com.tencent.mm:id/cn4";

        tv_setRemark = "com.tencent.mm:id/adl";
        tv_fillIn = "com.tencent.mm:id/aer";
        tv_remark_save = "com.tencent.mm:id/gc";

        privateDialogButtonOK = "com.tencent.mm:id/abg";
        tv_Delete = "com.tencent.mm:id/fa";
    }

    private void Ver657() {
        selectUIListViewId = "com.tencent.mm:id/es";
        selectUICheckBoxId = "com.tencent.mm:id/nh";
        selectUIItemId = "com.tencent.mm:id/a_c";
        selectUINickNameId = "com.tencent.mm:id/j2";
        chattingUIMessageId = "com.tencent.mm:id/if";
        groupInfoListViewId = "android:id/list";
        sendRequestBtnId = "com.tencent.mm:id/gd";
        moreAcionBtnId = "com.tencent.mm:id/f_";
        createGroupLaunchBtnId = "com.tencent.mm:id/lm";
        quiteGroupBtnId = "android:id/title";
        makeSureQuiteId = "com.tencent.mm:id/abz";
        launcherPagerId = "com.tencent.mm:id/blx";
        snsUploadUi = "com.tencent.mm.plugin.sns.ui.En_c4f742e5";
        launcherNewFriendId = "com.tencent.mm:id/avn";//新的朋友那个
        FMesssageConversationUI_ADD_BTN_ID = "com.tencent.mm:id/avz"; //添加好友按钮
        FMesssageConversationUI_LV_ID = "com.tencent.mm:id/aw5"; //通讯录 ListView
        etContentId = "com.tencent.mm:id/cpe";


        tv_setRemark = "com.tencent.mm:id/ae5";
        tv_fillIn = "com.tencent.mm:id/afb";
        tv_remark_save = "com.tencent.mm:id/gc";
        tv_Delete = "com.tencent.mm:id/fa";

    }

    public String getEtContentId() {
        return etContentId;
    }

    public String getSearchItemId() {
        return searchItemId;
    }

    public String getDialog() {
        return dialog;
    }

    public String getSendRequestBtnId() {
        return sendRequestBtnId;
    }

    public String getMoreAcionBtnId() {
        return moreAcionBtnId;
    }

    public String getCreateGroupLaunchBtnId() {
        return createGroupLaunchBtnId;
    }

    public String getQuiteGroupBtnId() {
        return quiteGroupBtnId;
    }

    public String getSearchUI() {
        return searchUI;
    }

    public String getContactInfoUI() {
        return contactInfoUI;
    }

    public String getMakeSureQuiteId() {
        return makeSureQuiteId;
    }

    public String getSelectUI() {
        return selectUI;
    }

    public String getChattingUI() {
        return chattingUI;
    }

    public String getChatRoomInfo() {
        return chatRoomInfo;
    }

    public String getLauncherUI() {
        return launcherUI;
    }

    public String getProgressDialog() {
        return progressDialog;
    }

    public String getSelectUIListViewId() {
        return selectUIListViewId;
    }

    public String getSelectUICheckBoxId() {
        return selectUICheckBoxId;
    }

    public String getSelectUIItemId() {
        return selectUIItemId;
    }

    public String getSelectUINickNameId() {
        return selectUINickNameId;
    }

    public String getChattingUIMessageId() {
        return chattingUIMessageId;
    }

    public String getGroupInfoListViewId() {
        return groupInfoListViewId;
    }

    public String getDialogHintTextId() {
        return dialogHintTextId;
    }

    public String getContactInfoUiNickNameId() {
        return contactInfoUiNickNameId;
    }

    public String getContactInfoUIGenderId() {
        return contactInfoUIGenderId;
    }

    public String getfTSMainUICleanEtId() {
        return fTSMainUICleanEtId;
    }

    public String getfTSMainUIItemId() {
        return fTSMainUIItemId;
    }

    public String getfTSMainUISearchEtId() {
        return fTSMainUISearchEtId;
    }

    public String getLauncherPagerId() {
        return launcherPagerId;
    }

    public String getLauncherNewFriendId() {
        return launcherNewFriendId;
    }

    public String getFMesssageConversationUI() {
        return FMesssageConversationUI;
    }

    public String getFMesssageConversationUI_LV_ID() {
        return FMesssageConversationUI_LV_ID;
    }

    public String getFMesssageConversationUI_ADD_BTN_ID() {
        return FMesssageConversationUI_ADD_BTN_ID;
    }

    public String getSnsUploadUi() {
        return snsUploadUi;
    }

    public String getSnsLabelUi() {
        return snsLabelUi;
    }

    public String getWhoCanSeeItemId() {
        return whoCanSeeItemId;
    }

    public String getNickNameTvId() {
        return nickNameTvId;
    }

    public String getSearchEditId() {
        return searchEditId;
    }

    public String getNoSuchTextId() {
        return noSuchTextId;
    }

    public String getCheckItemIvId() {
        return checkItemIvId;
    }
}
