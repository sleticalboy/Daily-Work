package com.sleticalboy.dailywork.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sleticalboy.dailywork.anims.TransitionUI;
import com.sleticalboy.dailywork.bean.ModuleItem;
import com.sleticalboy.dailywork.bt.BluetoothUI;
import com.sleticalboy.dailywork.camera.LiveCameraActivity;
import com.sleticalboy.dailywork.components.ProviderPractise;
import com.sleticalboy.dailywork.components.ServicePractise;
import com.sleticalboy.dailywork.csv.AlphaActivity;
import com.sleticalboy.dailywork.csv.AutoSwitchUI;
import com.sleticalboy.dailywork.csv.CustomViewActivity;
import com.sleticalboy.dailywork.csv.HeaderActivity;
import com.sleticalboy.dailywork.debug.DebugUI;
import com.sleticalboy.dailywork.devices.DeviceAdminUI;
import com.sleticalboy.dailywork.others.AlarmActivity;
import com.sleticalboy.dailywork.others.ImageConvertUI;
import com.sleticalboy.dailywork.others.NotificationsUI;
import com.sleticalboy.dailywork.rv.ClassifyActivity;
import com.sleticalboy.dailywork.rv.DecorationActivity;
import com.sleticalboy.dailywork.rv.PagerActivity;
import com.sleticalboy.dailywork.rv.WheelRVActivity;

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
