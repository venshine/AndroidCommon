package com.wx.android.common.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Json
 *
 * @author fengwx
 */
public class JsonUtils {

    /**
     * Convert object, list, ... to Json
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    /**
     * Convert a json string to Object
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T jsonToObject(String json, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }

    /**
     * Convert a json string to List<?>
     *
     * @param json
     * @return
     */
    public static <T> List<T> jsonToList(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<T>>() {
        }.getType());
    }

    /**
     * Convert a json string to ArrayList<?>
     *
     * @param json
     * @return
     */
    public static <T> ArrayList<T> jsonToArrayList(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<ArrayList<T>>() {
        }.getType());
    }

    /**
     * Convert a json string to Map<?, ?>
     *
     * @param json
     * @return
     */
    public static <K, V> Map<K, V> jsonToMap(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<Map<K, V>>() {
        }.getType());
    }

    /**
     * Convert a json string to Generic<T>
     *
     * @param json
     * @param <T>
     * @return
     */
    public static <T> T jsonToGeneric(String json, TypeToken<T> token) {
        Gson gson = new Gson();
        return gson.fromJson(json, token.getType());
    }

}
