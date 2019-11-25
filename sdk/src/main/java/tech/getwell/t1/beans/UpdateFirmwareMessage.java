package tech.getwell.t1.beans;

public class UpdateFirmwareMessage {
    /**
     * 正在交互, 正在上传文件中
     */
    public static int UPDATE_HD_STATUS_RECEIVING = 1;
    /**
     * 发送结束, 文件中传完成
     */
    public static int UPDATE_HD_STATUS_FINISH = 2;
    /**
     * 重启响应, 校验文件结束,并重启设备
     */
    public static int UPDATE_HD_STATUS_RESTART = 3;
    /**
     * 更新失败, 固件更新失败
     */
    public static int UPDATE_HD_STATUS_FAIL = 4;

    /**
     * 类型 ,
     */
    public int type;
    /**
     * 进度百分比
     */
    public Double percent;
    /**
     * 设备mac地址
     */
    public String macAddress;

    public UpdateFirmwareMessage(){

    }

    public UpdateFirmwareMessage(int type, double percent, String macAddress) {
        this.type = type;
        this.percent = percent;
        this.macAddress = macAddress;
    }
}
