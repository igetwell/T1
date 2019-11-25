package tech.getwell.t1.listeners;

import tech.getwell.t1.beans.FirmwareVersionMessage;
import tech.getwell.t1.beans.MotionMessage;
import tech.getwell.t1.beans.Response;

/**
 * @author Wave
 * @date 2019/7/30
 */
public interface OnReadListener {

    /**
     * 设备响应的数据(解析到数据)
     * @param response 设备响应数据
     */
    void onCallback(Response response);

    /**
     * 设备响应 肌氧数据回调
     * @param motionMessage 肌氧数据
     */
    void onSmo2Callback(MotionMessage motionMessage);

    /**
     * 设备响应 当前固件版本号
     * @param firmwareVersionMessage 是否支持此固件版本,当前固件版本号
     */
    void onFirmwareVersionCallback(FirmwareVersionMessage firmwareVersionMessage);

    /**
     * 读取数据异常
     * @param throwable 异常信息
     */
    void onError(Throwable throwable);
}
