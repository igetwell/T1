package tech.getwell.demo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.view.View;
import androidx.lifecycle.ViewModelProviders;
import java.io.IOException;
import java.util.UUID;
import tech.getwell.demo.beans.BleDevice;
import tech.getwell.t1.JDT1;
import tech.getwell.t1.beans.Callback;
import tech.getwell.demo.databinding.ActivitySdk2Binding;
import tech.getwell.t1.listeners.OnJDT1Listener;
import tech.getwell.t1.utils.LogUtils;
import tech.getwell.demo.viewmodels.SDKViewModel;

/**
 * @author Wave
 * @date 2019/7/30
 */
public class SDKActivity extends DataBindingActivity<ActivitySdk2Binding> implements OnJDT1Listener {

    public static final String EXTRA_T1 = "extra_t1_info";

    SDKViewModel model;

    JDT1 jdt1;

    @Override
    protected int layoutId() {
        return R.layout.activity_sdk_2;
    }

    @Override
    public void onAfter() {
        model = ViewModelProviders.of(this).get(SDKViewModel.class);
        // 开始观察 name
        model.getCallbackMutableLiveData().observe(this,(callback)-> onT1DataChanged(callback));
        BleDevice device = getIntent().getParcelableExtra(EXTRA_T1);
        onInitBle(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.address));
    }

    void onT1DataChanged(Callback callback){
        getViewDataBinding().setData(callback);
    }

    @Override
    public void onCallback(Callback callback) {
         if(callback == null){
             return;
         }
         if(callback.code != 0){
            return;
         }
        model.getCallbackMutableLiveData().postValue(callback);
    }

    void onInitBle(BluetoothDevice device){
        try{
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            // 建立蓝牙
            BluetoothSocket bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            LogUtils.d("连接成功...");
            // 读取数据
            jdt1 = new JDT1();
            jdt1.setBluetoothSocket(bluetoothSocket);
            jdt1.setListener(this);

        }catch (IOException e){
            e.printStackTrace();
        }

        //jdt1.start(); // jdt1.stop();

    }

    public void onStartRunningClick(View view){
        if(jdt1 != null)jdt1.start();
    }

    public void onStopClick(View view){
        if(jdt1 != null)jdt1.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
