#define LOG_TAG "JniDemo"

#include <utils/Log.h>
#include <nativehelper/JNIHelp.h>

#include "jni.h"

/**
 * 返回字符串到 Java 层
 */
jstring JniDemo_generateString(JNIEnv *env, jclass clazz) {
    // ALOGD("%s run", __FUNCTION__);
    return env->NewStringUTF("String from native");
}

/**
 * native 调用 Java 层方法
 */
void JniDemo_sayHelloToJava(JNIEnv *env, jclass clazz, jobject context) {
    // ALOGD("%s start to find method sayHello", __FUNCTION__);
    jmethodID sayHello = env->GetStaticMethodID(
            clazz, "sayHello", "(Landroid/content/Context;Ljava/lang/String;)V");
    if (sayHello == nullptr) {
        ALOGE("can not find method void sayHello(String)");
        return;
    }
    // ALOGD("%s start to call method sayHello", __FUNCTION__);
    jstring text = env->NewStringUTF("Hello From Native!");
    env->CallStaticVoidMethod(clazz, sayHello, context, text);
    if (text != nullptr) {
        env->DeleteLocalRef(text);
    }
}

// const char* name;
// const char* signature;
// void*       fnPtr;
static const JNINativeMethod gMethods[] = {
        // void callNative()
        {"nCallJava",  "(Lcom/sleticalboy/sample/jnidemo/JniDemo;)V",
                                               (void *) JniDemo_sayHelloToJava},
        {"nGetString", "()Ljava/lang/String;", (void *) JniDemo_generateString},
};

int register_com_sleticalboy_sample_jnidemo_JniDemo(JNIEnv *env) {
    // ALOGD("%s run", __FUNCTION__);
    const char *className = "com/sleticalboy/sample/jnidemo/JniDemo";
    return jniRegisterNativeMethods(env, className, gMethods, NELEM(gMethods));
}