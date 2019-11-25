package tech.getwell.t1;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.jd.hd_deal.HdDataDeal;

import java.io.File;
import java.io.IOException;
import tech.getwell.t1.beans.Callback;
import tech.getwell.t1.beans.FirmwareVersionMessage;
import tech.getwell.t1.beans.MotionMessage;
import tech.getwell.t1.beans.Response;
import tech.getwell.t1.bles.ReadTask;
import tech.getwell.t1.listeners.OnJDT1Listener;
import tech.getwell.t1.listeners.OnJDT1RawDataListener;
import tech.getwell.t1.listeners.OnReadListener;
import tech.getwell.t1.timrs.To2DataTimer;
import tech.getwell.t1.timrs.To2TimeOutTask;
import tech.getwell.t1.utils.CommandManagement;
import tech.getwell.t1.utils.Errors;
import tech.getwell.t1.utils.LogUtils;

/**
 * @author Wave
 * @date 2019/9/6
 */
public class JDT1 implements OnReadListener, To2TimeOutTask.OnTimeOutTaskListener, CommandManagement.OnCommandManagementListener, HdDataDeal.OnDataReceiveListener {

    boolean isRunning;

    long timeout = 3000L;

    BluetoothDevice bluetoothDevice;

    BluetoothSocket bluetoothSocket;

    CommandManagement commandManagement;

    OnJDT1Listener listener;

    OnJDT1RawDataListener rawDataListener;

    ReadTask readTask;

    To2DataTimer to2DataTimer;

    int version = -1;

    boolean isVersionValid;

    public JDT1(){
        LogUtils.setDebug(true);
        commandManagement = new CommandManagement();
        commandManagement.setListener(this);
    }

    public void setListener(OnJDT1Listener listener) {
        this.listener = listener;
    }

    public void setRawDataListener(OnJDT1RawDataListener rawDataListener) {
        this.rawDataListener = rawDataListener;
    }

    public void setTimeout(long interval) {
        this.timeout = interval;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) throws IOException{
        this.bluetoothSocket = bluetoothSocket;
        createReadTask();
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        HdDataDeal.addCallBack(this,this.bluetoothDevice.getAddress());
    }

    void createReadTask()throws IOException{
        if(this.bluetoothSocket == null){
            new RuntimeException("bluetoothSocket is null");
            return;
        }
        if(readTask != null){
            readTask.clear();
        }
        readTask = new ReadTask(this.bluetoothSocket.getInputStream()) ;
        readTask.setListener(this);
        readTask.start();
        // 获取固件版本
        commandManagement.pull(CommandManagement.VERSION);
        // 发送停止接受数据命令
        //if(isStopData)commandManagement.pull(CommandManagement.STOP);
    }

    To2DataTimer newTo2DataTimer(){
        closeTimer();
        return new To2DataTimer.Builder().setTimeOut(timeout).setTimeOutListener(this).build();
    }

    void closeTimer(){
        if(to2DataTimer == null)return;
        to2DataTimer.close();
        to2DataTimer = null;
    }

    /**
     * 开始运动
     */
    public void start(){
        start(CommandManagement.START_RUNNING_DEBUG);
    }

    /**
     * 开始运动
     * @param mode 运动类型 0,跑步,1 抗阻
     */
    public void start(int mode){
        if(!isConnected()){
            LogUtils.d("start isConnected is error ");
            callback(new Callback(Errors.BLE_BAD,this.version));
            return;
        }
        if(readTask == null){
            callback(new Callback(Errors.BLE_BAD,this.version));
            return;
        }
        int command = getMode(mode);
        // 启动读取数据
        readTask.setMode(command);
        commandManagement.pull(command);
        to2DataTimer = newTo2DataTimer();
        to2DataTimer.start();
    }

    /**
     * 刷新固件
     * @param file
     */
    public void updateFirmware(File file){
        if(file == null || !file.isFile()){
            LogUtils.e("固件文件不存在");
            return;
        }
        HdDataDeal.startUpdateHardWare(file.getAbsolutePath(),bluetoothDevice.getAddress());
    }

    /**
     * 停止运动
     */
    public void stop(){
        isRunning = false;
        // 停止接受数据
        commandManagement.pull(CommandManagement.STOP);
    }

    public void close(){
        if(isRunning){
            stop();
        }
        commandManagement.close();
        if(readTask != null){
            readTask.clear();
        }
    }

    int getMode(int mode){
        return mode == CommandManagement.START_RUNNING ? 10 : 11;
    }

    /**
     * 返回可执行的指令
     * @param command
     */
    @Override
    public void onCommandCallback(byte[] command) {
        send(command);
    }

    /**
     * 固件更新结果
     * @param type 1,升级中, 2,上传完成, 3,固件升级成功, 重启设备. 4,固件升级失败
     * @param percent type 为 1 时, 此参数为百分比
     * @param macAddressByFlag 设备的地址
     */
    @Override
    public void getDevUpResult(int type, double percent, String macAddressByFlag) {
        LogUtils.d(" type = "+type +" percent = "+percent +" String = "+macAddressByFlag);
    }

    @Override
    public void sendCmdToBlueTooth(byte[] bs) {
        send(bs);
    }

    /**
     * 发送数据
     * @param bs
     */
    boolean send(byte[] bs){
        if(!isConnected()){
            LogUtils.d("isConnected is error ");
            callback(new Callback(Errors.BLE_BAD,this.version));
            return false;
        }
        if(bs == null){
            LogUtils.e(" 未知的指令");
            callback(new Callback(Errors.OTHER,this.version));
            return false;
        }

        LogUtils.e(" sendDataToBlueTooth = "+bs.toString());
        try {
            bluetoothSocket.getOutputStream().write(bs);
            bluetoothSocket.getOutputStream().flush();
            return true;
        } catch (IOException e) {
            //e.printStackTrace();
            //异常情况, 无法读写数据
            LogUtils.d("当前连接不可用");
            callback(new Callback(Errors.BLE_BAD,this.version));
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
     * 原始数据
     * @param motionMessage
     */
    void rawDataCallback(MotionMessage motionMessage){if(rawDataListener != null)rawDataListener.onRawDataCallback(motionMessage);}

    /**
     * 设备响应所有数据
     * @param response
     */
    @Override
    public void onCallback(Response response) {
        // 转交给so解析数据
        if(response.getBuffer() != null)HdDataDeal.sendData(response.getBuffer(),response.getNum(),bluetoothDevice.getAddress());
    }

    @Override
    public void onSmo2Callback(MotionMessage motionMessage) {
        closeTimer();
        isRunning = true;
        rawDataCallback(motionMessage);
        callback(new Callback(Errors.NONE,this.version, motionMessage.smo2, motionMessage.smoothSmo2));
    }

    @Override
    public void onFirmwareVersionCallback(FirmwareVersionMessage firmwareVersionMessage) {
        this.isVersionValid = firmwareVersionMessage.isValid;
        int code = Errors.NONE;
        this.version = version;
        if(!firmwareVersionMessage.isValid){
            closeTimer();
            code = Errors.VER;
        }
        isRunning = firmwareVersionMessage.isValid;
        callback(new Callback(code,this.version));
    }

    @Override
    public void onError(Throwable throwable) {
        LogUtils.d("蓝牙连接已断开...");
        isRunning = false;
        closeTimer();
        callback(new Callback(Errors.BLE_BAD,this.version));
    }

    @Override
    public void onTimeOutCallback() {
        LogUtils.d("响应超时了...");
        isRunning = false;
        closeTimer();
        callback(new Callback(Errors.TIME,this.version));
    }
}
