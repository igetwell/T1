package tech.getwell.t1.bles;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.jd.hd_deal.HdDataDeal;
import com.jd.hd_deal.OnDataReceiveListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import tech.getwell.t1.beans.Response;
import tech.getwell.t1.beans.UpdateFirmwareMessage;
import tech.getwell.t1.listeners.OnReadListener;
import tech.getwell.t1.throwables.BluetoothDeviceException;
import tech.getwell.t1.throwables.BluetoothSocketException;
import tech.getwell.t1.throwables.NotFirmwareFileException;
import tech.getwell.t1.throwables.NotStartException;
import tech.getwell.t1.utils.LogUtils;
import tech.getwell.t1.utils.Motion;

public abstract class BluetoothCommunication implements OnReadListener, OnDataReceiveListener {

    private BluetoothDevice bluetoothDevice;

    private BluetoothSocket bluetoothSocket;

    private ReadTask readTask;

    public BluetoothCommunication(BluetoothDevice bluetoothDevice, BluetoothSocket bluetoothSocket) {
        this.bluetoothDevice = bluetoothDevice;
        this.bluetoothSocket = bluetoothSocket;
        HdDataDeal.addCallBack(this,this.bluetoothDevice.getAddress());
    }

    protected void setReadTaskMotion(Motion motion){
        if(readTask != null)readTask.setMotion(motion);
    }

    public boolean isRunning(){
        return readTask != null && readTask.isRunning;
    }

    /**
     * 启动
     */
    public void start(){
        startReadBluetooth();
    }

    /**
     * 刷新固件
     * @param firmwareFile
     */
    public void refreshFirmware(File firmwareFile){
        if(firmwareFile == null || !firmwareFile.exists()){
            onError(new FileNotFoundException("firmwareFile not exists!"));
            return;
        }
        if(!isFirmwareFile(firmwareFile)){
            onError(new NotFirmwareFileException("not firmwareFile"));
            return;
        }
        HdDataDeal.startUpdateHardWare(firmwareFile.getAbsolutePath(),bluetoothDevice.getAddress());
    }

    boolean isFirmwareFile(File file){
        return file.getName().lastIndexOf("\\.bin") <= -1;
    }

    /**
     * 读取数据
     */
    void startReadBluetooth(){
        if(this.bluetoothSocket == null){
            onError(new BluetoothSocketException("bluetoothSocket is null"));
            return;
        }
        if(this.bluetoothDevice == null){
            onError(new BluetoothDeviceException("bluetoothSocket is null"));
            return;
        }
        try{
            stopReadBluetooth();
            readTask = new ReadTask(this.bluetoothSocket.getInputStream()) ;
            readTask.setListener(this);
            readTask.start();
        }catch (IOException e){
            onError(e);
        }
    }

    boolean startWriteBluetooth(byte[] bytes){
        if(!isRunning()){
            LogUtils.d("BluetoothCommunication is not start");
            onError(new NotStartException("is not start"));
            return false;
        }
        if(bytes == null){
            return false;
        }
        LogUtils.e(" sendDataToBlueTooth = "+bytes.toString());
        try {
            bluetoothSocket.getOutputStream().write(bytes);
            bluetoothSocket.getOutputStream().flush();
            return true;
        } catch (IOException e) {
            //异常情况, 无法读写数据
            LogUtils.d("当前连接不可用");
            onError(e);
            return false;
        }
    }

    public void stopReadBluetooth(){
        if(readTask != null)readTask.clear();
    }

    @Override
    public void onCallback(Response response) {
        if(bluetoothDevice == null || response == null || response.getBuffer() == null)return;
        HdDataDeal.sendData(response.getBuffer(),response.getNum(),bluetoothDevice.getAddress());
    }

    /**
     * 刷新固件时回调函数
     * @param type 1,升级中, 2,上传完成, 3,固件升级成功, 重启设备. 4,固件升级失败
     * @param percent type 为 1 时, 此参数为百分比
     * @param macAddressByFlag 设备的地址
     */
    @Override
    public void getDevUpResult(int type, double percent, String macAddressByFlag) {
        LogUtils.d("type:"+type+" percent:"+percent);
        if(type == UpdateFirmwareMessage.UPDATE_HD_STATUS_RESTART){
            LogUtils.d("固件升级成功, 重启设备, 断开蓝牙, 终止读取设备数据");
            stopReadBluetooth();
        }
        onUpdateFirmwareCallback(new UpdateFirmwareMessage(type,percent,macAddressByFlag));
    }

    public abstract void onUpdateFirmwareCallback(UpdateFirmwareMessage updateFirmwareMessage);

    /**
     * so 回调时使用 , so 库调用此函数向 bluetooth 发送指令数据
     * @param bs
     */
    @Override
    public void sendCmdToBlueTooth(byte[] bs) {
        startWriteBluetooth(bs);
    }

}
