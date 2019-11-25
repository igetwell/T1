package com.jd.hd_deal;

public interface OnDataReceiveListener {
    /**
     * 固件更时 回调函数
     * @param type 1,升级中, 2,上传完成, 3,固件升级成功, 重启设备. 4,固件升级失败
     * @param percent type 为 1 时, 此参数为百分比
     * @param macAddressByFlag 设备的地址
     */
    void getDevUpResult(int type, double percent, String macAddressByFlag);

    /**
     * 将 蓝牙数据 转发 so 解析
     * @param bs
     */
    void sendCmdToBlueTooth(byte[] bs);
}
