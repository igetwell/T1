package tech.getwell.t1.bles;

import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tech.getwell.t1.beans.FirmwareVersionMessage;
import tech.getwell.t1.beans.MotionMessage;
import tech.getwell.t1.beans.Response;
import tech.getwell.t1.utils.Motion;
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

    public void setMotion(Motion motion){
        smoothSom2.setMax(getMotionMaxCount(motion));
    }

    int getMotionMaxCount(Motion motion){
        int count = 3;
        switch (motion){
            case RUNNING:
            case RUNNING_DEBUG:
                count = 5;
                break;
        }
        return count;
    }

    int getMax(int mode) {
        return mode == 1 || mode == 10 ? 5 : 3;
    }

    @Override
    public void run() {
        super.run();
        // 上一次的数据..
        Response mLastResponse = null;
        //开始读取数据
        while (isRunning) {
            if (Thread.interrupted() || this.stream == null) {
                isRunning = false;
                //mLastResponse = null;
                Log.e("T1ReadTask", " 连接已断开,结束读取数据 ");
                return;
            }
            try {
                Response response = new Response(this.stream);
                // 需要拼接
                if(mLastResponse != null && mLastResponse.isRawSMO2() && !mLastResponse.isValidRawSMO2Length()){
                    //response.data = mLastResponse.data.append(response.data);
                    response = merge(mLastResponse,response);
                    LogUtils.e("原始数据不够长 进行拼接 :"+response.data);
                }
                mLastResponse = response;
                if(mLastResponse.isRawSMO2() && !mLastResponse.isValidRawSMO2Length()){
                    LogUtils.d("原始数据未结束,待拼接完整");
                    if(mLastResponse.data != null && mLastResponse.data.length() > mLastResponse.getSMO2ValidLength()){
                        mLastResponse = null;
                        LogUtils.d("原始数据长度超标,丢掉数据异常不进行解析了");
                    }
                    continue;
                }
                // 进行解析转换
                // LogUtils.d(" 返回数据 : "+mLastResponse.data.toString());
                if(mLastResponse != null)callback(mLastResponse);
                // 不能全部占用CPU，要稍微释放
                Thread.sleep(10);
            } catch (Exception e) {
                //e.printStackTrace();
                if(listener != null && isRunning)listener.onError(e);
                return;
            }
        }
    }

    Response merge(Response mLastResponse,Response response){
        // 合并数据
        byte[] newBytes = new byte[mLastResponse.getBytes().length + response.getBytes().length];
        System.arraycopy(mLastResponse.getBytes(), 0, newBytes, 0, mLastResponse.getBytes().length);
        System.arraycopy(response.getBytes(), 0, newBytes, mLastResponse.getBytes().length, response.getBytes().length);
        // 合并长度
        int newNum = mLastResponse.getNum() + response.getNum();
        // 合并
        mLastResponse.getData().append(response.data);
        mLastResponse.setNum(newNum);
        mLastResponse.setBytes(newBytes);
        mLastResponse.resetType();
        return mLastResponse;
    }

    public void clear() {
        LogUtils.d("停止读取设备响应数据");
        isRunning = false;
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
            case Response.UPDATE_FIRMWARE:
                //listener.onUpdateFirmwareCallback(response.getBuffer(),response.getNum());
                break;
            case Response.FIRMWARE_VERSION:
                listener.onFirmwareVersionCallback(new FirmwareVersionMessage(response.isFirmwareValid(), response.getFirmwareVersion()));
                break;
            case Response.SMO2:
                MotionMessage motionMessage = new MotionMessage(response.toRawByteData());
                motionMessage.smoothSmo2 = getSmoothValue(motionMessage.smo2);
                listener.onSmo2Callback(motionMessage);
                break;
        }
    }
}