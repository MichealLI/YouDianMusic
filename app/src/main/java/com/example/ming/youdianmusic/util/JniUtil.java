package com.example.ming.youdianmusic.util;

/**
 * Created by Ming on 2017/9/8.
 */

public class JniUtil {
    static {
        System.loadLibrary("MingJni");
    }
    //调用C++对密码字符串进行加密
    public static native String encryptPassword(String password);
}
