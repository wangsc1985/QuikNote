package com.wangsc.quicknote.helper;

import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/21.
 */
public class _Session {
    public static final UUID UUID_EMPTY = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static UUID CurrentUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public static final String ServerIp = "192.168.1.7";

    public static int default_size_model=3;

    // 普通大小字体
    public static int home_text_size_normal=14;
    public static int details_title_size_normal=18;
    public static int details_content_size_normal=16;

    // 小字体
    public static int home_text_size_small=12;
    public static int details_title_size_small=16;
    public static int details_content_size_small=14;

    // 极小字体
    public static int home_text_size_smaller=10;
    public static int details_title_size_smaller=14;
    public static int details_content_size_smaller=12;

    // 大字体
    public static int home_text_size_big=16;
    public static int details_title_size_big=20;
    public static int details_content_size_big=18;

    // 极大字体
    public static int home_text_size_biger=18;
    public static int details_title_size_biger=22;
    public static int details_content_size_biger=20;
}
