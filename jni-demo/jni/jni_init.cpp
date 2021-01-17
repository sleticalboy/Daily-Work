#define LOG_TAG "JniDemo"

#include <utils/Log.h>
#include "jni.h"

extern int register_com_sleticalboy_sample_jnidemo_JniDemo(JNIEnv *env);

extern "C" jint JNI_OnLoad(JavaVM *vm, void * /*reversed*/) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        ALOGE("jni init error: get env failed");
        return -1;
    }
    register_com_sleticalboy_sample_jnidemo_JniDemo(env);
    return JNI_VERSION_1_6;
}

