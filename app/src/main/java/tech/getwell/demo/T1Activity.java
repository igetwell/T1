package tech.getwell.demo;

import android.bluetooth.BluetoothAdapter;
import android.view.View;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProviders;
import java.io.IOException;
import tech.getwell.demo.beans.BleDevice;
import tech.getwell.t1.beans.RawSmo2Data;
import tech.getwell.t1.beans.Response;
import tech.getwell.t1.beans.Smo2Data;
import tech.getwell.demo.bles.T1ConnectTask;
import tech.getwell.demo.bles.listeners.OnT1ConnectListener;
import tech.getwell.demo.databinding.ActivityT1Binding;
import tech.getwell.t1.logs.LogFile;
import tech.getwell.t1.utils.LogUtils;
import tech.getwell.demo.viewmodels.T1DataViewModel;

/**
 * @author Wave
 * @date 2019/7/30
 */
public class T1Activity extends DataBindingActivity<ActivityT1Binding> implements OnT1ConnectListener {

    public static final String EXTRA_T1 = "extra_t1_info";

    T1DataViewModel model;

    T1ConnectTask task;

    int status = 0;

    LogFile logFile;

    @Override
    protected int layoutId() {
        return R.layout.activity_t1;
    }

    @Override
    public void onAfter() {
        model = ViewModelProviders.of(this).get(T1DataViewModel.class);
        // 开始观察 name
        model.getSmo2DataMutableLiveData().observe(this,(smo2Data)-> onT1DataChanged(smo2Data));
        BleDevice device = getIntent().getParcelableExtra(EXTRA_T1);
        getViewDataBinding().setDev(device);
        task = new T1ConnectTask(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.address),this);
        task.start();
        getViewDataBinding().setIsValid(true);
        getViewDataBinding().setDevMsg("正在连接中...");


    }

    void onT1DataChanged(Smo2Data t1Data){
        getViewDataBinding().setSmo2Data(t1Data);
    }

    @Override
    public void onConnected() {
        status = 1;
        getViewDataBinding().setDevMsg("已连接");
        task.getVersion();
        try{
            logFile = new LogFile(this,"0101",2001);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(Throwable throwable) {
        status = -1;
        getViewDataBinding().setDevMsg("连接失败");
    }

    @Override
    public void onCallback(Response response) {
        LogUtils.d(" T1 response : "+response.data);
    }

    @Override
    public void onFirmwareVersionCallback(boolean isValid, int version) {
        // 非UI线程, 但 databinding 不依赖 UI线程,
        getViewDataBinding().setIsValid(isValid);
    }

    @Override
    public void onSmo2Callback(RawSmo2Data rawSmo2Data) {
        //RawSmo2Data rawSmo2Data = new RawSmo2Data(rawByteData);
        LogUtils.d("onSmo2Callback =  "+rawSmo2Data.toDataString());
        // 写入文件中..
        try{
            if(logFile != null) logFile.addRawData(rawSmo2Data);
        }catch (IOException e){
            e.printStackTrace();
        }
        // 非UI线程, 需要使用 postValue
        model.getSmo2DataMutableLiveData().postValue(rawSmo2Data);
        //model.getSmo2DataMutableLiveData().postValue(new Smo2Data(rawByteData));
    }

    public void onStartRunningClick(View view){
        if(status == 0){
            showToast("设备正在连接中...");
            return;
        }
        if(status == -1){
            showToast("设备失败...");
            return;
        }
        if(task != null)task.startRunning();
    }

    public void onStartResistanceClick(View view){
        if(status == 0){
            showToast("设备正在连接中...");
            return;
        }
        if(status == -1){
            showToast("设备失败...");
            return;
        }
        if(task != null)task.startResistance();
    }

//    public void onStartClick(View view){
//        if(status == 0){
//            showToast("设备正在连接中...");
//            return;
//        }
//        if(status == -1){
//            showToast("设备失败...");
//            return;
//        }
//        if(task != null)task.beginData();
//    }

    public void onStopClick(View view){
        if(status == 0){
            showToast("设备正在连接中...");
            return;
        }
        if(status == -1){
            showToast("设备失败...");
            return;
        }
        if(task != null)task.stopData();
    }

    void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if(task != null){
            task.stopData();
            task.disconnect();
        }
        if(logFile != null){
            try{
                logFile.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }


}
