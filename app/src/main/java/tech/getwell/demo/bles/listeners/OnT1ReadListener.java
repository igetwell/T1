package tech.getwell.demo.bles.listeners;

import tech.getwell.t1.beans.MotionMessage;
import tech.getwell.t1.beans.Response;

/**
 * @author Wave
 * @date 2019/7/30
 */
public interface OnT1ReadListener {
    /**
     * 设备响应的数据
     * @param response
     */
    void onCallback(Response response);

    /**
     * 设备响应 肌氧数据回调
     * @param motionMessage
     */
    void onSmo2Callback(MotionMessage motionMessage);

    /**
     * 设备响应 当前固件版本号
     * @param isValid 是否支持此固件版本
     * @param version 当前固件版本号
     */
    void onFirmwareVersionCallback(boolean isValid,int version);
}
