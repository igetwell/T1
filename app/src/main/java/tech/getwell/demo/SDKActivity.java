package tech.getwell.demo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.View;
import androidx.lifecycle.ViewModelProviders;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import tech.getwell.demo.beans.BleDevice;
import tech.getwell.t1.JDT1;
import tech.getwell.t1.JDT1ParserClient;
import tech.getwell.t1.beans.Callback;
import tech.getwell.demo.databinding.ActivitySdk2Binding;
import tech.getwell.t1.beans.FirmwareVersionMessage;
import tech.getwell.t1.beans.MotionMessage;
import tech.getwell.t1.beans.UpdateFirmwareMessage;
import tech.getwell.t1.listeners.OnJDT1Callback;
import tech.getwell.t1.listeners.OnJDT1Listener;
import tech.getwell.t1.listeners.OnJDT1RawDataListener;
import tech.getwell.t1.logs.JDLog;
import tech.getwell.t1.throwables.NotStartException;
import tech.getwell.t1.utils.LogUtils;
import tech.getwell.demo.viewmodels.SDKViewModel;
import tech.getwell.t1.utils.Motion;

/**
 * @author Wave
 * @date 2019/7/30
 */
public class SDKActivity extends DataBindingActivity<ActivitySdk2Binding> implements OnJDT1Listener, OnJDT1RawDataListener {

    public static final String EXTRA_T1 = "extra_t1_info";

    SDKViewModel model;

    JDT1 jdt1;

    JDLog jdLog;

    @Override
    protected int layoutId() {
        return R.layout.activity_sdk_2;
    }

    @Override
    public void onAfter() {
        model = ViewModelProviders.of(this).get(SDKViewModel.class);
        // 开始观察 name
        model.getMotionMessageMutableLiveData().observe(this,(motionMessage)-> onT1DataChanged(motionMessage));
        model.getFirmwareVersionMutableLiveData().observe(this,(firmwareVersionMessage)->onFirmwareVersionChanged(firmwareVersionMessage));
        model.getUpdateFirmwareMessageMutableLiveData().observe(this, updateFirmwareMessage -> onUpdateFirmwareChanged(updateFirmwareMessage));


        BleDevice device = getIntent().getParcelableExtra(EXTRA_T1);
        onInitBle(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.address));
        try{
            jdLog = new JDLog(this.getApplication());
            jdLog.newFile();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void onT1DataChanged(MotionMessage motionMessage){
        getViewDataBinding().setMotionMessage(motionMessage);
    }

    void onFirmwareVersionChanged(FirmwareVersionMessage firmwareVersionMessage){
        getViewDataBinding().setFirmwareVersion(firmwareVersionMessage);
    }

    void onUpdateFirmwareChanged(UpdateFirmwareMessage updateFirmwareMessage){
        getViewDataBinding().setUpdateFirmware(updateFirmwareMessage);
    }

    @Override
    public void onCallback(Callback callback) {
         if(callback == null){
             return;
         }
         if(callback.code != 0){
             Log.e("Callback codes", "error codes : "+callback.code);
             return;
         }
         LogUtils.d("固件版本号为: = "+callback.version);
        //model.getCallbackMutableLiveData().postValue(callback);
    }

    @Override
    public void onRawDataCallback(MotionMessage motionMessage) {
        //motionMessage.
        try{
            jdLog.addRawDataLog(motionMessage);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    BluetoothSocket bluetoothSocket;

    JDT1ParserClient jdt1ParserClient;

    void onInitBle(BluetoothDevice device){
        try{
            File logs = new File(this.getCacheDir().getAbsolutePath()+"/t1Logs");
            if(!logs.exists()){
                logs.mkdirs();
            }
            //HdDataDeal.setLibDebugLog(true,logs.getAbsolutePath());

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            // 建立蓝牙
            bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            //LogUtils.d("连接成功...");
            // 读取数据
            //jdt1 = new JDT1(true);
//            jdt1 = new JDT1();
//            jdt1.setBluetoothDevice(device);
//            jdt1.setBluetoothSocket(bluetoothSocket);
//            jdt1.setListener(this);
            jdt1ParserClient = new JDT1ParserClient.Builder()
                    .setBluetoothDevice(device)
                    .setBluetoothSocket(bluetoothSocket)
                    .setDebugLog(true)
                    .setCallback(new OnJDT1Callback() {
                        /**
                         * 异常
                         * @param throwable 异常信息
                         */
                        @Override
                        public void onFailure(Throwable throwable) {
                            if(throwable instanceof NotStartException){
                                //jdt1ParserClient.start();
                                LogUtils.d("未启动读取数据..."+throwable.getMessage());
                                return;
                            }else if(throwable instanceof IOException ){
                                LogUtils.d("蓝牙连接异常,读取数据失败 : "+throwable.getMessage());
                                return;
                            }

                            LogUtils.d(throwable.getMessage());
                        }

                        /**
                         * 固件版本信息回调
                         * @param firmwareVersionMessage 返回固件版本信息
                         */
                        @Override
                        public void onVersionCallback(FirmwareVersionMessage firmwareVersionMessage) {
                            model.getFirmwareVersionMutableLiveData().postValue(firmwareVersionMessage);
                        }

                        /**
                         * 执行运动时回调
                         * @param motionMessage 肌氧数据
                         */
                        @Override
                        public void onMotionCallback(MotionMessage motionMessage) {
                            LogUtils.d("原始数据为 :"+Utils.toRawDatas(motionMessage.datas));
                            model.getMotionMessageMutableLiveData().postValue(motionMessage);

                            try{
                                if(jdLog != null)jdLog.addRawDataLog(motionMessage);
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }

                        /**
                         * 执行固件更新时回调
                         * @param updateFirmwareMessage
                         */
                        @Override
                        public void onUpdateFirmwareCallback(UpdateFirmwareMessage updateFirmwareMessage) {
                            model.getUpdateFirmwareMessageMutableLiveData().postValue(updateFirmwareMessage);
                        }
                    })
                    .build();
            jdt1ParserClient.start();
            //jdt1ParserClient.startMotion(Motion.RUNNING_DEBUG);
        }catch (IOException e){
            e.printStackTrace();
        }
        //jdt1.start(); // jdt1.stop();
    }

    public void onStartRunningClick(View view){
        //if(jdt1 != null)jdt1.start();
        if(jdt1ParserClient != null) jdt1ParserClient.startMotion(Motion.RUNNING);
    }

    public void onMotion2Click(View view){
        //if(jdt1 != null)jdt1.start();
        if(jdt1ParserClient != null) jdt1ParserClient.startMotion(Motion.RUNNING_DEBUG);
    }

    public void onMotion3Click(View view){
        //if(jdt1 != null)jdt1.start();
        if(jdt1ParserClient != null) jdt1ParserClient.startMotion(Motion.RESISTANCE);
    }

    public void onMotion4Click(View view){
        //if(jdt1 != null)jdt1.start();
        if(jdt1ParserClient != null) jdt1ParserClient.startMotion(Motion.RESISTANCE_DEBUG);
    }

    public void onUpdateFirmwareClick(View view){
        File file = new File( this.getCacheDir().getAbsolutePath() +"/fw_forusb_V0228.bin");
        if(jdt1 != null)jdt1.updateFirmware(file);
        if(jdt1ParserClient != null) jdt1ParserClient.refreshFirmware(file);
    }


    public void onQueryVersionClick(View view){
//        if(jdt1 != null)jdt1.stop();
//        try{
//            jdLog.close();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
        if(jdt1ParserClient != null) jdt1ParserClient.queryFirmwareVersion();
    }


    public void onStopClick(View view){
//        if(jdt1 != null)jdt1.stop();
//        try{
//            jdLog.close();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
        if(jdt1ParserClient != null) jdt1ParserClient.stopMotion();
        model.getMotionMessageMutableLiveData().setValue(new MotionMessage());
    }

    @Override
    protected void onDestroy() {
        if(jdt1 != null)jdt1.close();
        try{
            if(bluetoothSocket != null)bluetoothSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
