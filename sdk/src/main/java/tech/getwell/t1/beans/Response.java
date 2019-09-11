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
     * 开始肌氧数据类型
     */
    public static final int SOM2_START = 2;

    /**
     * 肌氧数据类型
     */
    public static final int SMO2 = 2;

    /**
     * 结束肌氧数据类型
     */
    public static final int SOM2_STOP = 4;

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

    public Response(InputStream stream) throws IOException{
        read(stream);
    }

    /**
     * 读取响应数据
     * @param stream
     * @throws IOException
     */
    void read(InputStream stream) throws IOException {
        int num = 0;
        byte[] buffer = new byte[1024];
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
        this.type = isFirmwareVersion() ? FIRMWARE_VERSION : isSMO2() ? SMO2 : DEFAULT;
    }

    /**
     * 单独功能提取：从原始数据转换成十六进制的字符串
     */
    void withdrawResult(byte[] b, int num) {
        if (data == null) {
            data = new StringBuilder();
        }
        data.setLength(0);
        for (int i = 0; i < num; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            data.append(hex);
        }
        this.type = isFirmwareVersion() ? FIRMWARE_VERSION : isSMO2() ? SMO2 : DEFAULT;
    }
    // //73a12e01724a569c00074e3d3c013c5c140443e8310244a9f61848e5cb15313c96032e205609331dfc0733a19a3f0f2404
    boolean isSMO2(){
        return this.data != null && (data.toString().startsWith("73a106") || data.toString().startsWith("73a12e")) && data.toString().length() >= 18;
    }


    boolean isFirmwareVersion(){
        return this.data != null && data.toString().startsWith("736a0a02");
    }

    /**
     * 是否支持当前的固件版本
     * @return
     */
    public boolean isFirmwareValid(){
        return getFirmwareVersion() >= 30323238;
    }

    /**
     * 获取固件的版本号
     * @return
     */
    public int getFirmwareVersion(){
        return Integer.parseInt(data.toString().substring(8,16));
    }

    public RawByteData toRawByteData(){
        return new RawByteData(this.bytes);
    }
}
