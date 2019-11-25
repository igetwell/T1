package tech.getwell.t1.beans;

public class FirmwareVersionMessage {
    /**
     * 是否支持此固件版本
     */
    public boolean isValid;
    /**
     * 固件版本号
     */
    public int version;

    public FirmwareVersionMessage(){

    }

    public FirmwareVersionMessage(boolean isValid, int version) {
        this.isValid = isValid;
        this.version = version;
    }
}
