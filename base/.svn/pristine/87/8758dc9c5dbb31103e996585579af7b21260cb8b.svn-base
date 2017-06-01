package com.kidney_hospital.base.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * GsonUtils
 * Created by Yuanyx on 2016/8/16.
 */
public class GsonUtils {

    private static Gson GSON = new Gson();

    /**
     * 解析Json
     * @param clazz 要转化的类型
     * @param json 待转换的Json
     * @return
     */
    public static <T> T parse( Class<T> clazz,String json) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * 将Json字符串转换为JsonObject对象
     * @param json 待转换的Json字符串
     * @return JsonObject对象
     */
    public static JsonObject toJson(String json) {
        if (json != null) {
            JsonParser parser = new JsonParser();
            return parser.parse(json).getAsJsonObject();
        }
        return null;
    }

}
