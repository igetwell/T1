package tech.getwell.t1.beans;

import java.io.IOException;
import java.io.InputStream;

import tech.getwell.t1.utils.LogUtils;

/**
 *
 * 处理T1设备响应数据类
 * @author Wave
 * @date 2019/7/30
 */
public class Response {
    /**
     * 默认数据类型(指令数据/其它数据....) 暂不需要处理类
     */
    public static final int DEFAULT = 0;
    /**
     * 固件版本类型
     */
    public static final int FIRMWARE_VERSION = 1;
    /**
     * 肌氧数据类型
     */
    public static final int SMO2 = 2;
    /**
     * 更新固件类型
     */
    public static final int UPDATE_FIRMWARE = 3;

    /**
     * 开始肌氧数据类型
     */
    public static final int SOM2_START = 2;



    /**
     * 结束肌氧数据类型
     */
    public static final int SOM2_STOP = 4;
    /**
     * 最低版本号
     */
    public static final int MIN_VERSION = 228;

    /**
     * 消息类型
     */
    public int type = DEFAULT;
    /**
     * 原始数据转换成十六进制的字符串
     */
    public StringBuilder data;
    /**
     * 原始数据
     */
    private byte[] bytes;

    private byte[] buffer;

    private int num;

    private final int SMO2ValidLength = 98;

    public int getSMO2ValidLength() {
        return SMO2ValidLength;
    }

    public Response(InputStream stream) throws IOException{
        read(stream);
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setData(StringBuilder data) {
        this.data = data;
    }

    public StringBuilder getData() {
        return data;
    }

    /**
     * 读取响应数据
     * @param stream
     * @throws IOException
     */
    void read(InputStream stream) throws IOException {
        num = 0;
        buffer = new byte[1024];
        num = stream.read(buffer);

        if(num <= 0){
            return;
        }

        if (data == null) data = new StringBuilder();
        data.setLength(0);
        // 原始数据转换成十六进制的字符串
        bytes = new byte[num];
        for (int i = 0; i < num; i++) {
            bytes[i] = buffer[i];
            //System.out.println(i+" = "+buffer_new[i]);
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            data.append(hex);
        }
        LogUtils.d(" 响应数据为: "+data.toString());
        resetType();
    }

    public void resetType(){
        this.type = isFirmwareVersion() ? FIRMWARE_VERSION : isSMO2() ? SMO2 : isUpdateFirmware() ? UPDATE_FIRMWARE : DEFAULT;
    }

    //73a12e01724a569c00074e3d3c013c5c140443e8310244a9f61848e5cb15313c96032e205609331dfc0733a19a3f0f2404
    public boolean isSMO2(){
        return this.data != null && (data.toString().startsWith("73a106") || data.toString().startsWith("73a12e")) && data.toString().length() >= 18;
    }

    /**
     * 是否为 带原始数据的SMO2 类型
     * @return
     */
    public boolean isRawSMO2(){
        return this.data != null && data.toString().startsWith("73a12e");
    }

    /**
     * 是否为有效/完整的 带原始smo2的数据
     * @return
     */
    public boolean isValidRawSMO2(){
        return isRawSMO2() && isValidRawSMO2Length();
    }

    /**
     * 原始smo2 长度是否合法
     * @return
     */
    public boolean isValidRawSMO2Length(){
        return data.length() == getSMO2ValidLength();
    }

    public boolean isUpdateFirmware(){
        return this.data != null && this.data.toString().startsWith("739508");
    }

    public boolean isFirmwareVersion(){
        return this.data != null && data.toString().startsWith("736a0a02");
    }

    /**
     * 是否支持当前的固件版本 (30323238)
     * @return
     */
    public boolean isFirmwareValid(){
        return getFirmwareVersion() >= MIN_VERSION;
    }

    /**
     * 获取固件的版本号
     * @return
     */
    public int getFirmwareVersion(){
        String n1 = String.valueOf(Integer.parseInt(data.toString().substring(8,10)) - 30);
        String n2 = String.valueOf(Integer.parseInt(data.toString().substring(10,12)) - 30);
        String n3 = String.valueOf(Integer.parseInt(data.toString().substring(12,14)) - 30);
        String n4 = String.valueOf(Integer.parseInt(data.toString().substring(14,16)) - 30);
        return Integer.parseInt(n1+n2+n3+n4);
    }

    public RawByteData toRawByteData(){
        return new RawByteData(this.bytes);
    }
}
