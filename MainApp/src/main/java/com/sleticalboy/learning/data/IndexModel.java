package com.sleticalboy.learning.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sleticalboy.learning.anims.TransitionUI;
import com.sleticalboy.learning.bean.ModuleItem;
import com.sleticalboy.learning.bt.BluetoothUI;
import com.sleticalboy.learning.camera.LiveCameraActivity;
import com.sleticalboy.learning.components.ProviderPractise;
import com.sleticalboy.learning.components.ServicePractise;
import com.sleticalboy.learning.csv.AlphaActivity;
import com.sleticalboy.learning.csv.AutoSwitchUI;
import com.sleticalboy.learning.csv.CustomViewActivity;
import com.sleticalboy.learning.csv.HeaderActivity;
import com.sleticalboy.learning.debug.DebugUI;
import com.sleticalboy.learning.devices.DeviceAdminUI;
import com.sleticalboy.learning.others.AlarmActivity;
import com.sleticalboy.learning.others.ImageConvertUI;
import com.sleticalboy.learning.others.NotificationsUI;
import com.sleticalboy.learning.rv.ClassifyActivity;
import com.sleticalboy.learning.rv.DecorationActivity;
import com.sleticalboy.learning.rv.PagerActivity;
import com.sleticalboy.learning.rv.WheelRVActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 20-3-30.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class IndexModel {

    private final MutableLiveData<Result<List<ModuleItem>>> mModuleSource;
    // private RemoteTask mRemoteTask;
    // private LocalTask mLocalTask;

    IndexModel() {
        mModuleSource = new MutableLiveData<>();
    }

    public LiveData<Result<List<ModuleItem>>> getModuleSource() {
        mModuleSource.setValue(getTasks());
        return mModuleSource;
    }

    private Result<List<ModuleItem>> getTasks() {
        final ModuleItem[] array = {
                // debug tools
                new ModuleItem("调试工具", DebugUI.class),
                // bluetooth
                new ModuleItem("蓝牙模块", BluetoothUI.class),
                // device admin
                new ModuleItem("设备管理", DeviceAdminUI.class),
                // components
                new ModuleItem("Service", ServicePractise.class),
                new ModuleItem("ContentProvider", ProviderPractise.class),
                // animations
                new ModuleItem("转场动画", TransitionUI.class),
                // custom View & // special effects of View
                new ModuleItem("自动切换View使用", AutoSwitchUI.class),
                new ModuleItem("自定义 View", CustomViewActivity.class),
                new ModuleItem("头部悬停效果", HeaderActivity.class),
                new ModuleItem("控件透明度", AlphaActivity.class),
                // camera
                new ModuleItem("TextureView 实现实时预览", LiveCameraActivity.class),
                // Bitmap
                new ModuleItem("图片和字符串相互转换", ImageConvertUI.class),
                // Tasks
                new ModuleItem("定时任务", AlarmActivity.class),
                // RecyclerView
                new ModuleItem("RecyclerView 分类别显示", ClassifyActivity.class),
                new ModuleItem("RecyclerView 分页", PagerActivity.class),
                new ModuleItem("RecyclerView 添加 item 分割线 / 拖拽排序", DecorationActivity.class),
                new ModuleItem("RecyclerView 轮播", WheelRVActivity.class),
                // notifications
                new ModuleItem("通知呼吸灯闪烁", NotificationsUI.class)
        };
        return new Result.Success<>(new ArrayList<>(Arrays.asList(array)));
    }
}
