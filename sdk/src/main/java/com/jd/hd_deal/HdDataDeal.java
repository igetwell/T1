package com.jd.hd_deal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HdDataDeal {

  static {
    System.loadLibrary("jdintegermodel");
  }

  private static Map<String, OnDataReceiveListener> callBackMaps;
  private static Map<String, Integer> deviceFlag;
  private static volatile int alreadyAddCount = 0;
  private static double[] to2Stat;
  private static ArrayList<String> sportRecords = new ArrayList<>();

  public static int SINGLE_CMD_STOP = 1;

  public static int UPDATE_HD_STATUS_RECEIVING = 1;       // 正在交互
  public static int UPDATE_HD_STATUS_FINISH = 2;          // 发送结束
  public static int UPDATE_HD_STATUS_RESTART = 3;         // 重启响应
  public static int UPDATE_HD_STATUS_FAIL = 4;            // 更新失败
    
  // 速度类型
  public static int SPEED_MODE_ACTION  = 0; // 动作训练时，无速度
  public static int SPEED_MODE_RUNNING = 1; // 跑步运动
  public static int SPEED_MODE_CYCLING = 2; // 骑行运动

  /**
   * *** 主动调用 传入蓝牙接收的指令 ***
   * @param bs
   * @param len
   * @param macAddress
   * @return
   */
  public static boolean sendData(byte[] bs, int len, String macAddress) {
    if (!judgeDevLegal(macAddress)) {
      return false;
    }
    sendDataToNative(bs, len, deviceFlag.get(macAddress));
    return true;
  }

  /**
   * *** 主动调用 通知固件更新 ***
   */
  public static boolean startUpdateHardWare(String filePath, String macAddress) {
    if (!judgeDevLegal(macAddress)) {
      return false;
    }
    int flag = deviceFlag.get(macAddress);
    startUpdateNewHardWareVersion(filePath, flag);
    return true;
  }

  /**
   * *** 主动调用 开启或关闭so库debug日志 ***
   */
  public static boolean setLibDebugLog(boolean flag, String filePath){
    setLibDebugFlag(flag, filePath);
    return true;
  }

  /**
   * *** 主动调用 获取vo2功能 ***
   */
  public static double getVo2(double speed, double hr_factor, double smo2, int speedMode, int age, double vo2_max, double hr_rest){
    return calcVo2(speed, hr_factor, smo2, speedMode, age, vo2_max, hr_rest);
  }


  public static void removeCallBack(String macAddress){
    if(callBackMaps != null && macAddress != null)
      callBackMaps.remove(macAddress);
  }

  public static boolean addCallBack(OnDataReceiveListener callback, String macAddress) {
    if (callBackMaps == null) {
      callBackMaps = Collections.synchronizedMap(new HashMap<String, OnDataReceiveListener>());
    }
    callBackMaps.put(macAddress, callback);
    return true;
  }

  public static boolean judgeDevLegal(String macAddress) {
    if (deviceFlag == null) {
      deviceFlag = Collections.synchronizedMap(new HashMap<String, Integer>());
    }
    if (deviceFlag.size() >= 7) {
      System.out.println("limit seven devices");
      return false;
    }
    if (!deviceFlag.containsKey(macAddress)) {
      deviceFlag.put(macAddress, alreadyAddCount++);
    }
    return true;
  }


  // --------------------------- java call back ---------------------------
  /**
   *  需要将这个方法收到的byte[] 发送给蓝牙
   * @param bs
   * @param byte_arr_size
   * @param macFlag
   */
  public static void receiveCmdFromGroundFloor(byte[] bs,int byte_arr_size,int macFlag) {
    // 从C/C++层传出来的数据传给蓝牙
    String result = withdrawResult(bs, byte_arr_size);
//    String mac = findMacAddressByFlag(macFlag);
    OnDataReceiveListener callBack = findCallBackByFlag(macFlag);
    //LogUtils.e("receiveCmdFromGroundFloor==>" + result);
    if(callBack != null){
      callBack.sendCmdToBlueTooth(bs);
    }
//    BluetoothUtils.getInstance().sendCmdToBlueTooth(bs);
  }

  public static void receiveUpdateVersion(int status,double percent,int macFlag){
    OnDataReceiveListener callBack = findCallBackByFlag(macFlag);
    if (callBack != null) {
      callBack.getDevUpResult(status,percent, findMacAddressByFlag(macFlag));
    }
  }

  // --------------------------- java call back
  private static int[] changeByteArrToIntArr(byte[] bs,int byte_arr_size,boolean filterTimeByteArr){
    int[] useCmd = new int[byte_arr_size];
    int count = 0;
    for(byte b:bs){
      if(filterTimeByteArr){
        // 下标从10开始
        if(count >= 10 && count <= 14){
          useCmd[count++] = b;
          continue;
        }
      }
      useCmd[count] = (b & 0xff);
      count++;
    }
    return useCmd;
  }

  private static String findMacAddressByFlag(int macFlag) {
    String findMac = null;
    for (String currentKey : deviceFlag.keySet()) {
      if (deviceFlag.get(currentKey) == macFlag) {
        findMac = currentKey;
        break;
      }
    }
    return findMac;
  }

  private static OnDataReceiveListener findCallBackByFlag(int macFlag) {
//    if (callBackMaps == null) {
//      callBackMaps = Collections.synchronizedMap(new HashMap<String, OnDataReceiveListener>());
//    }
//    OnDataReceiveListener callBack = callBackMaps.get(findMacAddressByFlag(macFlag));
    return callBackMaps.get(findMacAddressByFlag(macFlag));
  }

  public interface OnDataReceiveListener {
    /**
     *
     * @param type 1,升级中, 2,上传完成, 3,固件升级成功, 重启设备. 4,固件升级失败
     * @param percent type 为 1 时, 此参数为百分比
     * @param macAddressByFlag 设备的地址
     */
    void getDevUpResult(int type, double percent, String macAddressByFlag);

    void sendCmdToBlueTooth(byte[] bs);
  }

  public static native boolean sendDataToNative(byte[] bs, int len, int devFlag);

  public static native boolean startUpdateNewHardWareVersion(String filePath,int devFlag);

  // 开启或关闭so库调试日志
  public static native void setLibDebugFlag(boolean flag, String filePath);

  // 获取当前VO2
  public static native double calcVo2(double speed, double hr_factor, double smo2, int speedMode, int age, double vo2_max, double hr_rest);

  /**
   * 单独功能提取：从原始数据转换成十六进制的字符串
   */
  private static StringBuilder tempSb;
  public static String withdrawResult(byte[] b, int num) {
    if (tempSb == null) {
      tempSb = new StringBuilder();
    }
    tempSb.setLength(0);
    for (int i = 0; i < num; i++) {
      String hex = Integer.toHexString(b[i] & 0xFF);
      if (hex.length() == 1) {
        hex = '0' + hex;
      }
      tempSb.append(hex);
    }
    return tempSb.toString();
  }

}
