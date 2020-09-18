# Daily-Work
> daily work demo

## Android 原生知识
### 四大组件
- Activity
- BroadcastReceiver
- Service
    - start
    - bind
- ContentProvider
### Context
### Bitmap
- 如何加载 bitmap
- 将 bitmap 转换成 base64 字符串
### Drawable
- BitmapDrawable
- ColorDrawable
### 系统 View/ViewGroup
- RecyclerView
  - RecyclerView 分类别显示
  - RecyclerView 添加 item 分割线 / 拖拽排序
  - RecyclerView 实现轮播图效果
- ViewSwitcher
  - ListView
  - GridView
### animation
  - 帧动画
  - 属性动画
  - 补间动画
  - 转场动画
### 设备管理
- 打开设置中设备管理页面： `adb shell am start -n com.android.settings/.DeviceAdminSettings`
- `Context.getSystemService(Context.DEVICE_POLICY_SERVICE)[DevicePolicyManager]`
- `DeviceAdminReceiver`
- ```xml
  <uses-policies>
    <limit-password />
    <expire-password />
    <reset-password />
    <watch-login />
    <disable-camera />
    <encrypted-storage />
    <wipe-data />
    <force-lock />
    <set-global-proxy />
  </uses-policies>
  ```
### 账户管理
- 添加
- 同步
- 删除
### 系统蓝牙
- 经典蓝牙
- Bluetooth 低功耗（BLE）
### 系统工具
- 打开 Settings 页面：`adb shell am start -n com.android.settings/.Settings`
- 打开布局边界
### 自定义 View/ViewGroup
- Paint
- Path
- EmojiPanel 仿微信聊天表情输入组件
### SurfaceView/TextureView + Camera 实现相机预览
- SurfaceView
- SurfaceHolder
- TextureView
### notification & status bar
- notification
- status bar
### Jetpack 组件
- Lifecycle
- LiveData
- AndroidX

## gradle 编译系统
- task
- exclude
- 自定义插件

## Java 基础
- 泛型
- 注解
- 集合
- 并发
- 反射
- 类加载

## 加密/解密
- 对称加密
- 非对称加密

## 开源框架
- GreenDao 数据库使用（ORM 数据库）
- ObjectBox 数据库使用（no sql 数据库）
- OkHttp（适用于 Java 和 Android 的高效网络请求框架）
- Glide（高效的图片加载框架）
- Lottie（使用 json 优雅地实现动画）
- LeakCanary（内存泄露检测工具）
