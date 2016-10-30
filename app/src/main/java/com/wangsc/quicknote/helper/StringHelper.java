package com.wangsc.quicknote.helper;

/**
 * Created by 阿弥陀佛 on 2015/6/23.
 */
public class StringHelper {
    public static String concat(String... strings) {
        StringBuilder sb = new StringBuilder();
        for (String str : strings){
            sb.append(str);
        }
        return sb.toString();
    }
    public static boolean IsNullOrEmpty(String str){
        if(str==null||str.length()==0)
            return true;
        else
            return false;
    }

    /**
     * 时间字段，月、日、时、分、秒，小于10的，设置前缀‘0’。
     * @param x
     * @return
     */
    public static String format(int x) {
        String s = "" + x;
        if (s.length() == 1)
            s = "0" + s;
        return s;
    }
}
