package tech.getwell.t1;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.File;

import tech.getwell.t1.bles.BluetoothJDT1;
import tech.getwell.t1.listeners.OnJDT1Callback;
import tech.getwell.t1.utils.LogUtils;
import tech.getwell.t1.utils.Motion;

public class JDT1ParserClient {

    BluetoothJDT1 bluetoothJDT1;

    JDT1ParserClient(Builder builder){
        bluetoothJDT1 = new BluetoothJDT1(builder.bluetoothDevice,builder.bluetoothSocket);
        bluetoothJDT1.setCallback(builder.callback);
    }

    /**
     * 启动解析
     */
    public void start(){
        bluetoothJDT1.start();
    }

    /**
     * 开始运动
     * @param motion 运动类型
     */
    public void startMotion(Motion motion){
        bluetoothJDT1.startMotion(motion);
    }

    /**
     * 结束运动
     */
    public void stopMotion(){
        bluetoothJDT1.stopMotion();
    }

    /**
     * 更新固件
     */
    public void refreshFirmware(File file){
        bluetoothJDT1.refreshFirmware(file);
    }

    /**
     * 查询当前固件版本
     */
    public void queryFirmwareVersion(){
        bluetoothJDT1.queryVersion();
    }

    /**
     * 结束,释放
     */
    public void close(){
        bluetoothJDT1.close();
    }


    public static final class Builder{

        private BluetoothDevice bluetoothDevice;

        private BluetoothSocket bluetoothSocket;

        private OnJDT1Callback callback;

        public Builder setBluetoothDevice(BluetoothDevice bluetoothDevice) {
            this.bluetoothDevice = bluetoothDevice;
            return this;
        }

        public Builder setBluetoothSocket(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
            return this;
        }

        public Builder setCallback(OnJDT1Callback callback) {
            this.callback = callback;
            return this;
        }

        public Builder setDebugLog(Boolean isOpen){
            LogUtils.setDebug(isOpen);
            return this;
        }

        public JDT1ParserClient build(){
            return new JDT1ParserClient(this);
        }
    }
}
