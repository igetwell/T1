package tech.getwell.t1;

import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import tech.getwell.t1.beans.Callback;
import tech.getwell.t1.beans.RawSmo2Data;
import tech.getwell.t1.beans.Response;
import tech.getwell.t1.bles.ReadTask;
import tech.getwell.t1.listeners.OnJDT1Listener;
import tech.getwell.t1.listeners.OnReadListener;
import tech.getwell.t1.utils.Errors;
import tech.getwell.t1.utils.LogUtils;

/**
 * @author Wave
 * @date 2019/9/6
 */
public class JDT1 implements OnReadListener {

    boolean isRunning;

    long timeout = 3000L;

    BluetoothSocket bluetoothSocket;

    OnJDT1Listener listener;

    ReadTask readTask;

    public void setListener(OnJDT1Listener listener) {
        this.listener = listener;
    }

    public void setTimeout(long interval) {
        this.timeout = interval;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) throws IOException{
        this.bluetoothSocket = bluetoothSocket;
        readTask = new ReadTask(this.bluetoothSocket.getInputStream()) ;
        readTask.setListener(this);
        readTask.start();
    }

    /**
     * 开始运动
     */
    public void start(){
        start(10);
    }

    /**
     * 开始运动
     * @param mode 运动类型 0,跑步,1 抗阻
     */
    public void start(int mode){
        if(!isConnected()){
            callback(new Callback(Errors.BLE_BAD));
            return;
        }
        // 启动读取数据
        readTask.setMode(mode);
        isRunning = send(getCommand(mode));
    }

    /**
     * 停止运动
     */
    public void stop(){
        isRunning = false;
        // 停止接受数据
        send(getCommand(-1));
    }

    public void onDestroy(){

    }

    /**
     * 获取设备指令
     * @param command
     * @return
     */
    byte[] getCommand(int command){
        byte[] bytes = null;
        switch (command){
            // 停止接收数据
            case -1:
                bytes = new byte[]{0x73,(byte) 0xA0,0x01,(byte) 0xF0};
                break;
            // 初始化数据 跑步类型
            case 1:
                bytes = new byte[]{0x73,(byte) 0xA0,0x01,0x01};
                break;
            // 初始化数据 非跑步类型
            case 2:
                bytes = new byte[]{0x73,(byte) 0xA0,0x01,0x02};
                break;
            // 初始化数据 跑步类型 (调试模式,带原始数据)
            case 10:
                bytes = new byte[]{0x73,(byte) 0xA0,0x01,0x10};
                break;
            // 初始化数据 非跑步类型 (调试模式,带原始数据)
            case 11:
                bytes = new byte[]{0x73,(byte) 0xA0,0x01,0x11};
                break;
            // 获取设备版本
            case 999:
                bytes = new byte[]{0x73,0x6a,0x00,0x02};
                break;
        }
        return bytes;
    }

    /**
     * 发送数据
     * @param bs
     */
    boolean send(byte[] bs){
        if(!isConnected()){
            callback(new Callback(Errors.BLE_BAD));
            return false;
        }
        if(bs == null){
            LogUtils.e(" 未知的指令");
            return false;
        }

        LogUtils.e(" sendDataToBlueTooth = "+bs.toString());
        try {
            bluetoothSocket.getOutputStream().write(bs);
            bluetoothSocket.getOutputStream().flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            //异常情况, 无法读写数据
            callback(new Callback(Errors.BLE_BAD));
            return false;
        }
    }

    /**
     * 运动状态
     * @return
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * 蓝牙连接是否可用
     * @return
     */
    boolean isConnected(){
        return bluetoothSocket != null && bluetoothSocket.isConnected();
    }

    /**
     * 响应数据
     * @param callback
     */
    void callback(Callback callback){
        if(listener != null)listener.onCallback(callback);
    }

    /**
     * 设备响应所有数据
     * @param response
     */
    @Override
    public void onCallback(Response response) {

    }

    @Override
    public void onSmo2Callback(RawSmo2Data rawSmo2Data) {
        callback(new Callback(Errors.NONE,rawSmo2Data.smoothSmo2,rawSmo2Data.smo2));
    }

    @Override
    public void onFirmwareVersionCallback(boolean isValid, int version) {
        callback(new Callback(Errors.VER));
    }
}
