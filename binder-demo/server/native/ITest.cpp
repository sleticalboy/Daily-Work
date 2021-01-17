
#define LOG_TAG "TestServer"

#include <binder/IPCThreadState.h>
#include <binder/IServiceManager.h>

#include <utils/Log.h>

#include "ITest.h"

namespace android {

    const uint32_t CODE_WRITE = 0x10;
    const uint32_t CODE_READ = 0x11;
    // 服务端代理类，负责转发
    class BpTest : public BpInterface<ITest> {
    public:
        BpTest(const sp<IBinder> &impl)
                : BpInterface<ITest>(impl) {}

        // 与 ITest 中声明的业务函数同名，原因是 BpInterface 是多继承类，其（模板类）原型为：
        // template<typename INTERFACE>
        // class BpInterface : public INTERFACE, public BpRefBase {...}
        void write() override {
            Parcel data, reply;
            data.writeInterfaceToken(ITest::getInterfaceDescriptor());
            // 转发到 BnTest::onTransact()
            ALOGD("BpTest::write() start transact %s cmd(%d)", "WRITE", CODE_WRITE);
            remote()->transact(CODE_WRITE, data, &reply);
        }

        void read() override {
            Parcel data, reply;
            data.writeInterfaceToken(ITest::getInterfaceDescriptor());
            // 转发到 BnTest::onTransact()
            ALOGD("BpTest::read() start transact %s cmd(%d)", "READ", CODE_READ);
            remote()->transact(CODE_READ, data, &reply);
        }
    };

    // 服端端 native 类，负责二次转发
    status_t BnTest::onTransact(uint32_t code, const Parcel &data, Parcel *reply, uint32_t flags) {
        switch (code) {
            case CODE_READ:
                CHECK_INTERFACE(ITest, data, reply)
                ALOGD("BnTest::%s() read(%d), flags(%d)", __FUNCTION__, code, flags);
                // 这里调用的是 ITest::read() 函数
                read();
                return NO_ERROR;
            case CODE_WRITE:
                CHECK_INTERFACE(ITest, data, reply)
                ALOGD("BnTest::%s() write(%d), flags(%d)", __FUNCTION__, code, flags);
                // 这里调用的是 ITest::write() 函数
                write();
                return NO_ERROR;
            default:
                return BnInterface::onTransact(code, data, reply);
        }
    }

    // 实现 ITest::read() 接口，处理实际的业务逻辑
    void ITest::read() {
        ALOGD("ITest::read() called, start do hard work");
    }

    // 实现 ITest::write() 接口，处理实际的业务逻辑
    void ITest::write() {
        ALOGD("ITest::write() called, start do hard work");
    }

    IMPLEMENT_META_INTERFACE(Test, "android.test.ITest")
} // namespace android