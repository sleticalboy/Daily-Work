
#ifndef ANDROID_ITESTSERVICE_H
#define ANDROID_ITESTSERVICE_H

#include <binder/IInterface.h>
#include <binder/Parcel.h>

namespace android {

    // 声明业务接口，子类实现接口处理具体业务
    class ITest : public IInterface {
    public:
        // 一定不要忘记了这里
        DECLARE_META_INTERFACE(Test)

        // 子类实现以下几口处理业务函数
        virtual void write();
        virtual void read();
    };

    // 子类在这里根据请求码 code 对客户端的请求进行转发，实际工作由 ITest 子类来完成
    class BnTest : public BnInterface<ITest> {
    public:
        virtual status_t onTransact(uint32_t code, const Parcel &data,
                                    Parcel *reply, uint32_t flags = 0);
    };

} // namespace android

#endif // ANDROID_ITESTSERVICE_H