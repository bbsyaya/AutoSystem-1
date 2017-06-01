package com.kidney_hospital.base.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 方便构造Map<String,String>参数
 * Created by Lianxw on 2016/8/25.
 */
public class ParamBuilder {
    private Map<String, String> mParam = new HashMap<>();

    public ParamBuilder append(String key, String value) {
        mParam.put(key, value);
        return this;
    }

    public Map<String, String> build() {
        return mParam;
    }

    public static ParamBuilder newInstance() {
        return new ParamBuilder();
    }

    @Override
    public String toString() {
        if (mParam.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : mParam.entrySet()) {
            if (builder.length()>0){
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.toString();
    }
}
