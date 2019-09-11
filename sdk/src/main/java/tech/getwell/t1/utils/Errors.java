package tech.getwell.t1.utils;

/**
 * @author Wave
 * @date 2019/9/6
 */
public class Errors {

    public static final int NONE = 0;

    /**
     * socket 为 null
     */
    public static final int BLE_NULL = 1001;
    /**
     * 蓝牙连接异常
     */
    public static final int BLE_BAD = 1002;
    /**
     * 读取数据发生错误
     */
    public static final int READ_BAD = 2001;
    /**
     * 固件版本过低
     */
    public static final int VER = 3001;
    /**
     * 其它异常
     */
    public static final int OTHER = 9001;
}
