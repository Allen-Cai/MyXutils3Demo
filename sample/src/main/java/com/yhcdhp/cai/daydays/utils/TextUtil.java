package com.yhcdhp.cai.daydays.utils;


import android.text.TextUtils;

public class TextUtil {
    /**
     * 检查string参数中是否存在汉字和空格
     *
     * @param str
     * @return
     */
    public static boolean hasChinese(String str) {
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            String cStr = String.valueOf(chars[i]);
            if (cStr.matches("[\u4e00-\u9fa5]+")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查string参数是否为空或者“null”
     *
     * @param s
     * @return
     */
    public static boolean checkString(String s) {
        if (TextUtils.isEmpty(s) || s.equals("null"))
            return true;
        return false;
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }
}
