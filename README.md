# Daily-Work
> daily work demo

## Android 原生知识

### 四大组件
- Activity
- BroadcastReceiver
- Service
  - start
    - Android 8.0+ 前台服务
      - `<uses-permission
        android:name="android.permission.FOREGROUND_SERVICE" />`
      - `Context.startForegroundService()`
      - Service#onCreate() `startForeground()`
  - bind
    - `LocalBinder`
    - `RemoteBinder`
  - permissions
- ContentProvider
  - `onCreate()` 先于 `Application#onCreate()` 执行
  - `getType()/insert()/delete()/update()/query()/onCreate()`

### Context

### SqlLiteDatabase

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
- AbsListView
  - ListView
  - GridView
- VideoView
  - MediaMetadataRetriever
- SurfaceView
  - SurfaceView/TextureView + Camera 实现相机预览
  - SurfaceHolder
- TextureView
- LayoutInflater
- Layout.Factory

### animation
- 帧动画
- 属性动画
- 补间动画
- 转场动画

### 设备管理

- 打开设置中设备管理页面： `adb shell am start -n
  com.android.settings/.DeviceAdminSettings`
- `Context.getSystemService(Context.DEVICE_POLICY_SERVICE)[DevicePolicyManager]`
- `DeviceAdminReceiver`
- 
  ```xml
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
  - `<uses-permission
    android:name="android.permission.ACCESS_COARSE_LOCATION" />`
  - `<uses-permission
    android:name="android.permission.ACCESS_FINE_LOCATION" />`
  - `BluetoothDevice.createBond()/removeBond()`
- Bluetooth 低功耗（BLE）
  - GATT
    - `BluetoothDevice.connectGatt()`
  - Hid

### 系统工具
- 打开 Settings 页面：`adb shell am start -n
  com.android.settings/.Settings`
- 打开布局边界

### 自定义 View/ViewGroup
- Paint
- Path
- EmojiPanel 仿微信聊天表情输入组件
- ProgressView

### notification & status bar
- notification
  - NotificationManager:
    Context.getSystemService(Context.NOTIFICATION_SERVICE)
- status bar

### Android 6.0+ 动态权限申请
- ActivityCompat.requestPermission()
- ActivityCompat.checkSelfPermissions()

### Jetpack 组件
- Lifecycle
- LiveData
- AndroidX

## gradle 编译系统
- task
  - 

    ```grovvy
    // 定义一个 task
    // group: 对任务进行分组(否则就出现在 other 分组中)
    // description: 描述任务的职责
    task printBuildDir(description: 'print build files', group: 'custom') {
        println 'printBuildDir() started'
        printRecurse(rootProject.buildDir)
        println 'printBuildDir() finished'
    }
    ```
- exclude
- 自定义插件

## Java 基础

### 泛型
- 泛型擦除
- 上下边界
- 通配符与嵌套

### 注解
- Annotation

### 集合
- List
  - ArrayList
  - LinkedList
- Map
  - HashMap
  - TreeMap

### 并发
- synchronized exception：`object not locked by thread before wait`
- 死锁实例
  ```java
    final public class DeadLock {

        private static final String TAG = "DeadLock";
    
        private final Object mLockA, mLockB;
        private int mNumber = 100;
    
        private DeadLock() {
            mLockA = new Object();
            mLockB = new Object();
        }
    
        public static void run() {
            final DeadLock lock = new DeadLock();
            new Thread("ThreadA") {
                @Override
                public void run() {
                    lock.runThreadA(getName());
                }
            }.start();
            new Thread("ThreadB") {
                @Override
                public void run() {
                    lock.runThreadB(getName());
                }
            }.start();
        }
    
        private void runThreadA(String name) {
            Log.d(TAG, name + " is running and try to acquire mLockA");
            while (mNumber >= 0) {
                synchronized (mLockA) {
                    Log.d(TAG, name + " is holding mLockA.");
                    mNumber++;
                    Log.d(TAG, name + " mNumber++: " + mNumber);
                    SystemClock.sleep(100L);
                    Log.d(TAG, name + " try to acquire mLockB");
                    synchronized (mLockB) {
                        Log.d(TAG, name + " is holding mLockB.");
                        mNumber--;
                        Log.d(TAG, name + " mNumber:--" + mNumber);
                        Log.d(TAG, name + " release mLockB.");
                    }
                    Log.d(TAG, name + " release mLockA.");
                }
            }
        }
    
        private void runThreadB(String name) {
            Log.d(TAG, name + " is running and try to acquire mLockB");
            while (mNumber >= 0) {
                synchronized (mLockB) {
                    Log.d(TAG, name + " is holding mLockB.");
                    mNumber--;
                    Log.d(TAG, name + " mNumber--: " + mNumber);
                    SystemClock.sleep(100L);
                    Log.d(TAG, name + " try to acquire mLockA");
                    synchronized (mLockA) {
                        Log.d(TAG, name + " is holding mLockA.");
                        mNumber++;
                        Log.d(TAG, name + " mNumber++: " + mNumber);
                        SystemClock.sleep(100L);
                        Log.d(TAG, name + " release mLockA.");
                    }
                    Log.d(TAG, name + " release mLockB.");
                }
            }
        }
    }
    ```

### 反射

### 类加载
- ClassLoader
- BootClassLoader
- ExtClassLoader
- 双亲委派机制

## 加密/解密
- 对称加密
- 非对称加密

## 开源框架

### GreenDao 数据库使用（ORM 数据库）

### ObjectBox 数据库使用（no sql 数据库）

### OkHttp（适用于 Java 和 Android 的高效网络请求框架）
- Dispatcher

### Retrofit（http 网络请求适配器）
- 动态代理

### Glide（高效的图片加载框架）

### Lottie（使用 json 优雅地实现动画）

### LeakCanary（内存泄露检测工具）
- 强引用

