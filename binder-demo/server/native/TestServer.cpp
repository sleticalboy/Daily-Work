
#define LOG_TAG "TestService"

#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>

#include <utils/Log.h>
#include <utils/String8.h>

#include "ITest.h"

// 使用 android 命名空间
using namespace android;

int main(int count, char **argv) {
    ALOGI("sample service start count: %d, argv[0]: %s", count, argv[0]);

    // 创建单例
    sp<ProcessState> proc(ProcessState::self());

    // 获取 service manager
    sp<IServiceManager> sm(defaultServiceManager());
    ALOGI("defaultServiceManager(): %p", sm.get());

    // 注册服务
    sm->addService(String16("sample.service"), new BnTest());
    ALOGI("add sample.service to service manager");

    // 开启工作线程
    ProcessState::self()->startThreadPool();
    // 加入到主线程
    IPCThreadState::self()->joinThreadPool();

    return 0;
}