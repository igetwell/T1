package tech.getwell.t1.utils;

/**
 * @author Wave
 * @date 2019/8/30
 */
public class Command {
    //73  a0  01  01
    public static final byte[] RUN = new byte[]{0x73,(byte) 0xA0,0x01,0x01};
    //73  a0  01  10 (带原始数据)
    public static final byte[] RUN_DEBUG = new byte[]{0x73,(byte) 0xA0,0x01,0x10};
    //73  a0  01  02
    public static final byte[] RESISTANCE = new byte[]{0x73,(byte) 0xA0,0x01,0x02};
    //73  a0  01  11 (带原始数据)
    public static final byte[] RESISTANCE_DEBUG = new byte[]{0x73,(byte) 0xA0,0x01,0x11};
    //73  a0  01  F0
    public static final byte[] STOP = new byte[]{0x73,(byte) 0xA0,0x01,(byte) 0xF0};
    // 736a0002 (获取设备固件版本)
    public static final byte[] OLDER_VER = new byte[]{0x73,0x6a,0x00,0x02};
}
