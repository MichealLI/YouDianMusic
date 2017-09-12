//
// Created by Ming on 2017/9/8.
//
#include "com_example_ming_youdianmusic_util_JniUtil.h"

JNIEXPORT jstring JNICALL Java_com_example_ming_youdianmusic_util_JniUtil_encryptPassword
        (JNIEnv *env, jclass obj, jstring jstr){
    return env->NewStringUTF("This just a test for Android Studio NDK JNI developer!");
}
