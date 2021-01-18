
# 当前目录
LOCAL_PATH := $(call my-dir)
# 清除所有参数
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

# 所有的 java 文件
LOCAL_SRC_FILES := $(call all-java-files-under, $(LOCAL_PATH))

# apk 名字
LOCAL_PACKAGE_NAME := JniDemo
LOCAL_PRIVATE_PLATFORM_APIS := true
LOCAL_CERTIFICATE := platform

# 使用本地共享 so 库
LOCAL_JNI_SHARED_LIBRARIES := libjni_demo

# 编译 apk
include $(BUILD_PACKAGE)

include $(call all-makefiles-under, $(LOCAL_PATH))
