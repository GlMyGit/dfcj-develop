package com.dfcj.videoim.im.emo;

import android.graphics.Bitmap;

import com.dfcj.videoim.appconfig.AppApplicationMVVM;
import com.dfcj.videoim.util.ScreenUtil;

import java.io.Serializable;

/**
 * Describe: 表情的实体类
  */

public class EmojiBean implements Serializable {
    private int id;
    private int unicodeInt;
    private String emojiFilters;

    private static final int deaultSize = ScreenUtil.getPxByDp(AppApplicationMVVM.getInstance(),32);
    private String desc;
    private String filter;
    private Bitmap icon;
    private int width = deaultSize;
    private int height = deaultSize;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }




    public String getEmojiFilters() {
        return emojiFilters;
    }

    public void setEmojiFilters(String emojiFilters) {
        this.emojiFilters = emojiFilters;
    }

    public String getEmojiFilters_values() {
        return emojiFilters_values;
    }

    public void setEmojiFilters_values(String emojiFilters_values) {
        this.emojiFilters_values = emojiFilters_values;
    }

    private String emojiFilters_values;

    public String getEmojiString() {
        return  getEmojiStringByUnicode(unicodeInt);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnicodeInt() {
        return getEmojiStringByUnicode(unicodeInt);
    }

    public void setUnicodeInt(int unicodeInt) {
        this.unicodeInt = unicodeInt;
    }

    public static String getEmojiStringByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    @Override
    public String toString() {
        return "EmojiBean{" +
                "id=" + id +
                ", unicodeInt=" + unicodeInt +
                '}';
    }
}
