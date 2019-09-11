package tech.getwell.t1.bles;

import android.util.Log;

import java.io.InputStream;

import tech.getwell.t1.beans.RawSmo2Data;
import tech.getwell.t1.beans.Response;
import tech.getwell.t1.utils.SmoothSom2;
import tech.getwell.t1.listeners.OnReadListener;
import tech.getwell.t1.utils.LogUtils;

/**
 * @author Wave
 * @date 2019/9/9
 */
public class ReadTask extends Thread {

    boolean isRunning;

    InputStream stream;

    OnReadListener listener;

    SmoothSom2 smoothSom2;

    public ReadTask(InputStream stream) {
        this.stream = stream;
        isRunning = true;
        smoothSom2 = new SmoothSom2();
    }

    public void setListener(OnReadListener listener) {
        this.listener = listener;
    }

    public void setMode(int mode) {
        smoothSom2.setMax(getMax(mode));
    }

    int getMax(int mode) {
        return mode == 1 || mode == 10 ? 5 : 3;
    }

    @Override
    public void run() {
        super.run();
        //开始读取数据
        while (isRunning) {
            if (Thread.interrupted() || this.stream == null) {
                isRunning = false;
                Log.e("T1ReadTask", " 连接已断开,结束读取数据 ");
                return;
            }
            try {
                callback(new Response(this.stream));
                // 不能全部占用CPU，要稍微释放
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void clear() {
        isRunning = false;
        LogUtils.d("停止读取设备响应数据");
    }

    int getSmoothValue(int smo2) {
        smoothSom2.put(smo2);
        return smoothSom2.smoothValue();
    }

    /**
     * 处理数据完成的回调函数
     *
     * @param response
     */
    void callback(Response response) {
        if (listener == null || response == null) return;
        listener.onCallback(response);

        switch (response.type) {
            case Response.FIRMWARE_VERSION:
                //String version = response.
                listener.onFirmwareVersionCallback(response.isFirmwareValid(), response.getFirmwareVersion());
                break;
            case Response.SMO2:
                RawSmo2Data rawSmo2Data = new RawSmo2Data(response.toRawByteData());
                rawSmo2Data.smoothSmo2 = getSmoothValue(rawSmo2Data.smo2);
                listener.onSmo2Callback(rawSmo2Data);
                break;
        }
    }
}