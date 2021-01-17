
#define LOG_TAG "TestClient"

#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>

#include <utils/Log.h>
#include <utils/String8.h>

#include "ITest.h"

// 使用 android 命名空间
using namespace android;

int main(int count, char **argv) {
    ALOGI("sample client start count: %d, argv[0]: %s", count, argv[0]);

    // 获取 service manager
    sp<IServiceManager> sm(defaultServiceManager());
    ALOGI("defaultServiceManager(): %p", sm.get());

    // 获取注册的 sample.service 服务
    sp<IBinder> binder = sm->getService(String16("sample.service"));
    if (binder == NULL) {
        ALOGE("sample service binder is null, abort...");
        return -1;
    }
    ALOGI("sample service binder is %p", binder.get());

    // 通过 interface_cast 还原 ITest 接口
    sp<ITest> service = interface_cast<ITest>(binder);
    ALOGI("sample service is %p", service.get());

    // 使用服务提供的业务接口
    service->write();
    service->read();
    return 0;
}