package tech.getwell.t1.listeners;

import tech.getwell.t1.beans.RawSmo2Data;
import tech.getwell.t1.beans.Response;

/**
 * @author Wave
 * @date 2019/7/30
 */
public interface OnReadListener {
    /**
     * 设备响应的数据
     * @param response
     */
    void onCallback(Response response);

    /**
     * 设备响应 肌氧数据回调
     * @param rawSmo2Data
     */
    void onSmo2Callback(RawSmo2Data rawSmo2Data);

    /**
     * 设备响应 当前固件版本号
     * @param isValid 是否支持此固件版本
     * @param version 当前固件版本号
     */
    void onFirmwareVersionCallback(boolean isValid, int version);
}
