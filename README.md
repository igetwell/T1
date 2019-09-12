#### T1 SDK 工作时序图

![image](https://igetwell-media.oss-cn-shenzhen.aliyuncs.com/ofit_pro_test_media/image/WechatIMG69.png)

Gradle
------
```
allprojects {
    repositories {
        ...
        maven {url 'https://jitpack.io'}
    }
}

dependencies {
    ...
    implementation 'com.github.igetwell:T1:Tag'
}
```
Usage
-----
``` Java
    // 建立蓝牙
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
    bluetoothSocket.connect();
    LogUtils.d("连接成功...");
    // 建立JDT1
    JDT1 jdt1 = new JDT1();
    // 设置蓝牙通道
    jdt1.setBluetoothSocket(bluetoothSocket); 
    // 监听数据响应
    jdt1.setListener(new OnJDT1Listener() {
        @Override
        public void onCallback(Callback callback) {
            // 设备响应数据处理
        }
    }); 
    // 启动/停止
    //jdt1.start(); // jdt1.stop();
```

JDT1.class 
-----------
作为与T1设备交互的核心工具类,依赖硬件蓝牙通道(BluetoothSocket). 注意此类并不维护硬件之间蓝牙连接的生命周期,用户需要自身去维护硬件之间蓝牙连接生命周期

方法 | 归属类  | 用途
---|---|---
setBluetoothSocket | JDT1 | 已连接T1设备蓝牙BluetoothSock
setTimeout | JDT1 | 数据响应超时时间,默认时间为3秒
setListener | JDT1 | 数据响应回调
start | JDT1 | 开始运动(0跑步,1抗阻) 默认0
stop | JDT1 | 停止运动 (设备将停止发送运动数据)
close | JDT1 | 结束运动 (停止与设备之间的交互并释放相关的引用)

OnJDT1Listener.class 
-----------

接口 | 归属类  | 用途
---|---|---
onCallback |  OnJDT1Listener | 将设备数据(Callback)返回 

Callback.class 
-----------

变量 | 描述
---|---
code | 响应码
smo2 | 原始肌氧值 
smoothSmo2 | 平滑肌氧值

ErrorCode
-----------

响应码 | 描述
---|---
0 | 正常
1001 | BluetoothSocket 为空
1002 | BluetoothSocket 连接异常
2001 | 内部解析数据异常
3001 | 固件版本过低
4001 | 响应数据超时
9001 | 其它异常


