
#define LOG_TAG "JniDemo"

#include <utils/Log.h>
#include <nativehelper/JNIHelp.h>

#include "jni.h"
#include "android_runtime/AndroidRuntime.h"

/*
void JniDemo_sayHello_(JNIEnv *env, jclass clazz, jobject context) {
    // native 层调用 Java 层，弹 Toast
    // Landroid/widget/Toast;
    jclass toastCls = env->FindClass("android/widget/Toast");
    if (toastCls == nullptr) {
        ALOGE("class android.widget.Toast not found!!");
        jniThrowRuntimeException(env, "class android.widget.Toast not found");
        return;
    }
    ALOGI("find class Toast");
    jmethodID makeText = env->GetStaticMethodID(toastCls, "makeText",
        "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
    if (makeText == nullptr) {
        ALOGE("method Toast#makeText(Context, CharSequence, int) not found!!");
        jniThrowRuntimeException(env, "method Toast#makeText(Context, CharSequence, int) not found");
        return;
    }
    ALOGI("find method Toast#makeText()");
    jobject toast = env->CallStaticObjectMethod(toastCls, makeText, context, "Hello Form Native", 0);
    jmethodID show = env->GetMethodID(toastCls, "show", "()V");
    if (show == nullptr) {
        ALOGE("method Toast#show() not found!!");
        jniThrowRuntimeException(env, "method Toast#show() not found");
        return;
    }
    ALOGI("find method Toast#show()");
    ALOGD("say hello to java from native!!!");
    env->CallVoidMethod(toast, show);
}
*/

void JniDemo_sayHelloToJava(JNIEnv *env, jclass clazz) {
    jclass jniDemo = env->FindClass("com/sleticalboy/sample/jnidemo/JniDemo");
    if (jniDemo == nullptr) {
        ALOGE("can not find class JniDemo");
        jniThrowRuntimeException(env, "class com.sleticalboy.sample.jnidemo.JniDemo not found");
        return;
    }
    // void sayHello(String hello)
    jmethodID sayHello = env->GetMethodID(jniDemo, "sayHello", "(Ljava/lang/String;)V");
    if (sayHello == nullptr) {
        ALOGE("can not find method void sayHello(String)");
        jniThrowRuntimeException(env, "method void sayHello(String) not found");
        return;
    }
    env->CallVoidMethod(jniDemo, sayHello, "Hello From Native!");
}

// const char* name;
// const char* signature;
// void*       fnPtr;
static const JNINativeMethod gMethods[] = {
    // void callNative()
    {"callNative", "()V", (void *) JniDemo_sayHelloToJava},
};

const char *className = "com/sleticalboy/sample/jnidemo/JniDemo";

int register_com_sleticalboy_sample_jnidemo_JniDemo(JNIEnv *env) {
    return jniRegisterNativeMethods(env, className, gMethods, NELEM(gMethods));
}