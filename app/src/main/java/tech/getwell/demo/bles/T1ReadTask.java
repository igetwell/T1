package tech.getwell.demo.bles;

import android.util.Log;
import java.io.InputStream;

import tech.getwell.t1.beans.MotionMessage;
import tech.getwell.t1.beans.Response;
import tech.getwell.t1.utils.LogUtils;
import tech.getwell.t1.utils.SmoothSom2;
import tech.getwell.demo.bles.listeners.OnT1ConnectListener;

/**
 * 处理蓝牙设备响应数据类
 * @author Wave
 * @date 2019/7/30
 */
public class T1ReadTask extends Thread{

    boolean isRunning;

    InputStream stream;

    OnT1ConnectListener listener;

    SmoothSom2 smoothSom2;

    public T1ReadTask(InputStream stream) {
        this.stream = stream;
        isRunning = true;
        smoothSom2 = new SmoothSom2();
    }

    public void setListener(OnT1ConnectListener listener) {
        this.listener = listener;
    }

    public void setMode(int mode){
        smoothSom2.setMax(getMax(mode));
    }

    int getMax(int mode){
        return mode == 1 || mode == 10 ? 5 : 3;
    }

    @Override
    public void run() {
        super.run();
        // 连接成功
        onConnected();
        //开始读取数据
        while (isRunning){
            if(Thread.interrupted() || this.stream == null){
                isRunning = false;
                Log.e("T1ReadTask"," 连接已断开,结束读取数据 ");
                return;
            }
            try {
                callback(new Response(this.stream));
                // 不能全部占用CPU，要稍微释放
                Thread.sleep(10);
            }catch (Exception e){
                e.printStackTrace();
                onConnectionFailed("连接异常,无法读取数据");
                return;
            }
        }
    }

    public void clear(){
        isRunning = false;
        LogUtils.d("停止读取设备响应数据");
    }

    /**
     * 标示蓝牙连接成功回调
     */
    void onConnected(){
        if(listener != null)listener.onConnected();
    }

    /**
     * 标记连接异常的信息回调
     * @param msg
     */
    void onConnectionFailed(String msg){
        if(listener != null)listener.onConnectionFailed(new Throwable(msg));
    }

    int getSmoothValue(int smo2){
        smoothSom2.put(smo2);
        return smoothSom2.smoothValue();
    }
    /**
     * 处理数据完成的回调函数
     * @param response
     */
    void callback(Response response){
        if(listener == null || response == null)return;
        listener.onCallback(response);

        switch (response.type){
            case Response.FIRMWARE_VERSION:
                //String version = response.
                listener.onFirmwareVersionCallback(response.isFirmwareValid(),response.getFirmwareVersion());
                break;
            case Response.SMO2:
                MotionMessage motionMessage = new MotionMessage(response.toRawByteData());
                motionMessage.smoothSmo2 = getSmoothValue(motionMessage.smo2);
                listener.onSmo2Callback(motionMessage);
                break;
        }
    }
}
