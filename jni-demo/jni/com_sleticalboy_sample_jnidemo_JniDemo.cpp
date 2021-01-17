
#define LOG_TAG "JniDemo"

#include <utils/Log.h>
#include <nativehelper/JNIHelp.h>

#include "jni.h"
#include "android_runtime/AndroidRuntime.h"

void JniDemo_sayHello(JNIEnv *env, jclass clazz, jobject context) {
    // native 层调用 Java 层，弹 Toast
    jclass toastCls = env->FindClass("Landroid/widget/Toast;");
    if (toastCls == nullptr) {
        jniThrowRuntimeException(env, "class android.widget.Toast not found");
        return;
    }
    jmethodID methodId = env->GetStaticMethodID(toastCls, "makeText",
        "(Lcom/android/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;");
    if (methodId == nullptr) {
        jniThrowRuntimeException(env, "method Toast#makeText(Context, CharSequence, int) not found");
        return;
    }
    jobject toast = env->CallStaticObjectMethod(toastCls, methodId, context, "Hello Form Native", 0);
    methodId = env->GetMethodID(toastCls, "show", "()V");
    if (methodId == nullptr) {
        jniThrowRuntimeException(env, "method Toast#show() not found");
        return;
    }
    env->CallVoidMethod(toast, methodId);
    ALOGI("say hello to java from native!!!");
}

// const char* name;
// const char* signature;
// void*       fnPtr;
static const JNINativeMethod gMethods[] = {
        {"jniDemo", "(Lcom/android/content/Context;)V", (void *) JniDemo_sayHello},
};

const char *className = "com/sleticalboy/sample/jnidemo/JniDemo";

int register_com_sleticalboy_sample_jnidemo_JniDemo(JNIEnv *env) {
    return jniRegisterNativeMethods(env, className, gMethods, NELEM(gMethods));
}