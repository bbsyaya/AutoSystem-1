package com.rabbit.fans.model;

import java.util.List;

/**
 * Created by Vampire on 2017/5/27.
 */

public class PhoneBean {

    /**
     * time : 20
     * list : ["18034510181","18633840184","15176850188","15176850181","15176850180","15176850183","15175130187","15133100185","18034510188","13833380189","18633840185","15176850189","15133100183","18034510189","13833380183","15175130180","13833380186","15176850186","15176850181","18034510183","15175130181","13833380185","13833380187","15176850184","13833380186","15176850185","15133100182","15133100188","15133100180","15133100181","18633840188","13833380188","15175130182","13833380180","13833380185","15175130184","15176850189","18034510185","18034510180","15176850182","15175130183","13833380183","15133100188","18034510186","18633840187","15176850188","18034510180","13833380180","13833380188","13833380187","15133100180","15133100187","18034510184","15133100186","15133100185","15133100189","15133100186","18633840189","18034510188","18034510189","18633840180","13833380182","15133100183","18034510182","15175130188","15176850180","13833380184","18633840182","13833380182","18633840186","18034510187","15175130189","15175130185","18034510187","15176850186","15133100184","15176850187","15176850185","18034510183","13833380184","18034510185","15176850183","15175130186","15176850182","15176850184","18034510181","18034510184","18034510186","15133100182","13833380181","15133100184","13833380189","18034510182","15176850187","15133100187","18633840183","13833380181","15133100181","18633840181","15133100189"]
     */

    private String time;
    private List<String> list;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
