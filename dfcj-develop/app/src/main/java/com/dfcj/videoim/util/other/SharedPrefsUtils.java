package com.dfcj.videoim.util.other;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gulong on 2016-10-10/0010.
 */
public class SharedPrefsUtils {

    private static Context context;

    public SharedPrefsUtils(Context mcon){
        super();

        this.context=mcon;

    }

    /**
     * 向SharedPreferences中写入int类型数据
     *
     * @param value 值
     */
    public static void putValue(String key, int value) {
        SharedPreferences.Editor sp = getEditor();
        sp.putInt(key, value);
        sp.commit();
    }

    /**
     * 向SharedPreferences中写入boolean类型的数据
     *
     * @param key 键
     * @param value 值
     */
    public static void putValue(String key, boolean value) {
        SharedPreferences.Editor sp = getEditor();
        sp.putBoolean(key, value);
        sp.commit();
    }

    /**
     * 向SharedPreferences中写入String类型的数据
     * @param key 键
     * @param value 值
     */
    public static void putValue(String key, String value) {
        SharedPreferences.Editor sp = getEditor();
        sp.putString(key, value);
        sp.commit();
    }


    /**
     * 向SharedPreferences中写入float类型的数据
     *
     * @param key 键
     * @param value 值
     */
    public static void putValue(String key,
                                float value) {
        SharedPreferences.Editor sp = getEditor();
        sp.putFloat(key, value);
        sp.commit();
    }

    /**
     * 向SharedPreferences中写入long类型的数据
     *
     * @param key 键
     * @param value 值
     */
    public static void putValue(String key,
                                long value) {
        SharedPreferences.Editor sp = getEditor();
        sp.putLong(key, value);
        sp.commit();
    }

    /**
     * 从SharedPreferences中读取int类型的数据
     *
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @return 返回读取的值
     */
    public static int getValue(String key, int defValue) {
        SharedPreferences sp = getSharedPreferences();
        int value = sp.getInt(key, defValue);
        return value;
    }

    /**
     * 从SharedPreferences中读取boolean类型的数据
     *
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @return 返回读取的值
     */
    public static boolean getValue(String key,
                                   boolean defValue) {
        SharedPreferences sp = getSharedPreferences();
        boolean value = sp.getBoolean(key, defValue);
        return value;
    }

    /**
     * 从SharedPreferences中读取String类型的数据
     *
     * @return 返回读取的值
     */
    public static String getValue(String key) {
        SharedPreferences sp = getSharedPreferences();
        String value = sp.getString(key, "");
        return value;
    }

    /**
     * 从SharedPreferences中读取float类型的数据
     *
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @return 返回读取的值
     */
    public static float getValue(String key, float defValue) {
        SharedPreferences sp = getSharedPreferences();
        float value = sp.getFloat(key, defValue);
        return value;
    }

    public static long getValue(String key, long defValue) {
        SharedPreferences sp = getSharedPreferences();
        long value = sp.getLong(key, defValue);
        return value;
    }

    /**
     * 从SharedPreferences中读取long类型的数据
     *
     * @param name 对应的xml文件名称
     * @param key 键
     * @param defValue 如果读取不成功则使用默认值
     * @return 返回读取的值
     */
    public static long getValue(String name, String key, long defValue) {
        SharedPreferences sp = getSharedPreferences();
        long value = sp.getLong(key, defValue);
        return value;
    }

    //获取Editor实例
    private static SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    //获取SharedPreferences实例
    private static SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences("xjdywappcacheinfo", Context.MODE_PRIVATE);
    }

    //清空
    public static  void clearShardInfo(){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.commit();
    }

}