package tech.getwell.t1.bles;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.File;

import tech.getwell.t1.beans.FirmwareVersionMessage;
import tech.getwell.t1.beans.MotionMessage;
import tech.getwell.t1.beans.UpdateFirmwareMessage;
import tech.getwell.t1.listeners.OnJDT1Callback;
import tech.getwell.t1.utils.CommandManagement;
import tech.getwell.t1.utils.LogUtils;
import tech.getwell.t1.utils.Motion;

public class BluetoothJDT1 extends BluetoothCommunication implements CommandManagement.OnCommandManagementListener{

    CommandManagement commandManagement;

    OnJDT1Callback callback;

    public BluetoothJDT1(BluetoothDevice bluetoothDevice, BluetoothSocket bluetoothSocket) {
        super(bluetoothDevice, bluetoothSocket);
        commandManagement = new CommandManagement();
        commandManagement.setListener(this);
    }

    public void setCallback(OnJDT1Callback callback) {
        this.callback = callback;
    }

    public void close(){
        this.stopReadBluetooth();
        this.callback = null;
    }

    /**
     * 查询固件版本号
     */
    public void queryVersion(){
        commandManagement.pull(CommandManagement.VERSION);
    }

    /**
     * 开始运动
     * @param motion 运动类型
     */
    public void startMotion(Motion motion){
        setReadTaskMotion(motion);
        int command = getMotionCommand(motion);
        commandManagement.pull(command);
//        to2DataTimer = newTo2DataTimer();
//        to2DataTimer.start();
//        commandManagement.pull(CommandManagement.VERSION);
    }

    /**
     * 停止运动
     */
    public void stopMotion(){
        commandManagement.pull(CommandManagement.STOP);
    }

    int getMotionCommand(Motion motion){
        switch (motion){
            case RUNNING:
                return CommandManagement.START_RUNNING;
            case RUNNING_DEBUG:
                return CommandManagement.START_RUNNING_DEBUG;
            case RESISTANCE:
                return CommandManagement.START_OTHER;
            case RESISTANCE_DEBUG:
                return CommandManagement.START_OTHER_DEBUG;
        }
        return -1;
    }

    /**
     * 执行指令
     * @param command
     */
    @Override
    public void onCommandCallback(byte[] command) {
        startWriteBluetooth(command);
    }

    @Override
    public void onUpdateFirmwareCallback(UpdateFirmwareMessage updateFirmwareMessage) {
        LogUtils.d("onUpdateFirmwareCallback --->  type : "+updateFirmwareMessage.type +" 进度: "+updateFirmwareMessage.percent);
        if(callback != null)callback.onUpdateFirmwareCallback(updateFirmwareMessage);
    }

    @Override
    public void onSmo2Callback(MotionMessage motionMessage) {
        LogUtils.d("onSmo2Callback ---> smo2 = "+ motionMessage.smo2);
        if(callback != null)callback.onMotionCallback(motionMessage);
    }

    @Override
    public void onFirmwareVersionCallback(FirmwareVersionMessage firmwareVersionMessage) {
        LogUtils.d("onFirmwareVersionCallback --->");
        if(callback != null)callback.onVersionCallback(firmwareVersionMessage);
    }

    @Override
    public void onError(Throwable throwable) {
        LogUtils.d("onError --->");
        if(callback != null)callback.onFailure(throwable);
    }
}
