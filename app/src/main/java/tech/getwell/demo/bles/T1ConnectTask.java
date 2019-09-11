package tech.getwell.demo.bles;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tech.getwell.demo.bles.listeners.OnT1ConnectListener;
import tech.getwell.t1.utils.Command;
import tech.getwell.t1.utils.LogUtils;

/**
 * @author Wave
 * @date 2019/7/29
 */
public class T1ConnectTask {

    private final static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    Disposable disposable;

    BluetoothDevice device;

    BluetoothSocket socket;

    T1ReadTask readTask;

    OnT1ConnectListener listener;

    public T1ConnectTask(BluetoothDevice device,OnT1ConnectListener listener) {
        this.device = device;
        setListener(listener);
    }

    public void setListener(OnT1ConnectListener listener) {
        this.listener = listener;
    }

    void subscribe(ObservableEmitter<BluetoothSocket> emitter, BluetoothDevice device) throws Exception {
        BluetoothSocket socket = connect(device);
        if(socket == null || !socket.isConnected()){
            // 蓝牙连接失败
            emitter.onComplete();
            return;
        }
        emitter.onNext(socket);
    }
    
    /**
     * 开始连接
     */
    public void start(){
        Observable.create((ObservableEmitter<BluetoothSocket> emitter) -> subscribe(emitter,device))
                .onTerminateDetach()
                .repeatWhen(new T1RepeatWhen())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<BluetoothSocket>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(BluetoothSocket bluetoothSocket) {
                        read();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onConnectionFailed("连接失败:"+e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 开始蓝牙连接
     */
    BluetoothSocket connect(BluetoothDevice bluetoothDevice){
        UUID uuid = UUID.fromString(SPP_UUID);
        LogUtils.d("开始连接设备 = "+bluetoothDevice.getAddress());
        try{
            socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            socket.connect();
        }catch (IOException e){
            //LogUtils.e(" 连接失败.... ");
            e.printStackTrace();
            //connect2(bluetoothDevice);
        }
        return socket;
    }

    /**
     * 使用反射方式连接设备
     */
    BluetoothSocket connect2(BluetoothDevice bluetoothDevice){
        LogUtils.d("使用反射方式连接");
        try{
            socket =(BluetoothSocket) bluetoothDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(bluetoothDevice,1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return socket;
    }

    void read(){
        if(socket == null || !socket.isConnected()){
            LogUtils.e(" 连接失败.... ");
            onConnectionFailed("连接失败....");
            return;
        }
        try{
            readTask = new T1ReadTask(socket.getInputStream());
            readTask.setListener(listener);
            readTask.start();
        }catch (IOException e){
            e.printStackTrace();
            //异常情况, 无法读写数据
            onConnectionFailed("异常情况, 无法读写数据");
        }
    }


    /**
     * 向蓝牙发送原始数据
     * @param bs
     */
    void sendDataToBlueTooth(byte[] bs) {
        if(socket == null){
            LogUtils.e(" socket is null  ");
            return;
        }
        if(!socket.isConnected()){
            LogUtils.e(" socket is connected  ");
            return;
        }
        LogUtils.e(" sendDataToBlueTooth = "+bs.toString());
        try {
            socket.getOutputStream().write(bs);
            socket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
            //异常情况, 无法读写数据
        }
    }

    public void startRunning(){
        sendDataToBlueTooth(Command.RUN_DEBUG);
    }

    public void startResistance(){
        sendDataToBlueTooth(Command.RESISTANCE_DEBUG);
    }

    /**
     * 向设备发送初始数据命令
     */
    public void beginData(){
        sendDataToBlueTooth(Command.RUN_DEBUG);
    }

    /**
     * 向设备发送结束数据命令
     */
    public void stopData(){
        sendDataToBlueTooth(Command.STOP);
    }

    /**
     * 获取设备固件版本号
     */
    public void getVersion(){
        sendDataToBlueTooth(Command.OLDER_VER);
    }

    /**
     * 断开蓝牙连接
     */
    public void disconnect(){
        LogUtils.d("断开连接");
        if(disposable != null && !disposable.isDisposed())disposable.dispose();
        if(readTask != null)readTask.clear();
        try{
            if(socket != null)socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 连接异常回调
     * @param msg
     */
    void onConnectionFailed(String msg){
        if(listener != null)listener.onConnectionFailed(new Throwable(msg));
    }
}
