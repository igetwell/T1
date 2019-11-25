package tech.getwell.t1.listeners;

import tech.getwell.t1.beans.FirmwareVersionMessage;
import tech.getwell.t1.beans.MotionMessage;
import tech.getwell.t1.beans.UpdateFirmwareMessage;

public interface OnJDT1Callback {
    /**
     * 出错,失败
     * @param throwable 异常信息
     */
    void onFailure(Throwable throwable);

    /**
     * 查询固件版本回调
     * @param firmwareVersionMessage 返回固件版本信息
     */
    void onVersionCallback(FirmwareVersionMessage firmwareVersionMessage);

    /**
     * 运动数据 回调
     * @param motionMessage 肌氧数据
     */
    void onMotionCallback(MotionMessage motionMessage);

    /**
     * 升级固件 回调
     * @param updateFirmwareMessage
     */
    void onUpdateFirmwareCallback(UpdateFirmwareMessage updateFirmwareMessage);

}
