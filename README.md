#### T1 SDK_v2.0.0 
--------
```
mermaid
graph TD
    A[T1_SKD_V2]-->B(数据采集)
    A --> C[数据解码]
    A --> D[固件版本查询]
    A --> E[更新固件]
```

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
    implementation 'com.github.igetwell:T1:2.0.0'
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
    // 初始化 JDT1ParserClient 
    JDT1ParserClient jdt1ParserClient = new JDT1ParserClient.Builder()
                    .setBluetoothDevice(device)
                    .setBluetoothSocket(bluetoothSocket)
                    .setCallback(...)
                    .build();
    // 开始对T1进行通讯交互操作
    jdt1ParserClient.start();
    // 查询当前固件版本号
    //jdt1ParserClient.queryFirmwareVersion();
    // 开始跑步运动
    //jdt1ParserClient.startMotion(Motion.RUNNING_DEBUG);; 
    //jdt1ParserClient.stopMotion();
    // 更新固件
    jdt1ParserClient.refreshFirmware(...);
    // 结束通讯交互
    //jdt1ParserClient.close();
```

JDT1ParserClient.class  T1设备通讯交互解析工具
-----------
==作为与T1设备通讯交互的核心工具类,必需依赖BluetoothDevice与BluetoothSocket两个参数==  
1.==JDT1ParserClient不维护硬件之间蓝牙连接BluetoothSocket的生命周期,使用者需要自身去维护BluetoothSocket生命周期==

2.==执行refreshFirmware(file)更新固件时. 当固件更新完成时. 设备会重启此时JDT1ParserClient所依耐的BluetoothSocket会断开失效, 继续使用则需要重初始化JDT1ParserClient==

方法 | 参数 | 说明
---|---|---
start | 无 | 调用此函数,开启通讯交互
startMotion | motion | 开始获取运动数据,motion 为运动类型
stopMotion | 无 | 停止接受运动数据
refreshFirmware | File | 更新固件, file为新固件文件
queryFirmwareVersion | 无| 查询设备固件版本号
stop | 无| 停止通讯交互,并释放资源


Motion.class 运动类型
-----------
枚举 | 说明
---|---|---
RUNNING | 跑步运动 
RUNNING_DEBUG | 跑步运动(带原始数据)
RESISTANCE | 抗阻运动
RESISTANCE_DEBUG | 抗阻运动(带原始数据)


OnJDT1Callback.class T1设备数据响应接口回调
-----------
为JDT1ParserClient注册监听时,可处理各函数间的响应信息

代码示例
-----
```
new JDT1ParserClient.Builder()
        .setBluetoothDevice(device)
        .setBluetoothSocket(bluetoothSocket)
        .setCallback(new OnJDT1Callback() {
            /**
             * 异常/失败信息回调
             * @param throwable 异常信息
             */
            @Override
            public void onFailure(Throwable throwable) {
                LogUtils.d(throwable.getMessage());
            }
    
            /**
             * 固件版本信息回调
             * @param firmwareVersionMessage 返回固件版本信息
             */
            @Override
            public void onVersionCallback(FirmwareVersionMessage firmwareVersionMessage) {
                model.getFirmwareVersionMutableLiveData().postValue(firmwareVersionMessage);
            }
    
            /**
             * 执行运动时回调
             * @param motionMessage 肌氧数据
             */
            @Override
            public void onMotionCallback(MotionMessage motionMessage) {
                model.getMotionMessageMutableLiveData().postValue(motionMessage);
            }
    
            /**
             * 执行固件更新时回调
             * @param updateFirmwareMessage
             */
            @Override
            public void onUpdateFirmwareCallback(UpdateFirmwareMessage updateFirmwareMessage) {
                model.getUpdateFirmwareMessageMutableLiveData().postValue(updateFirmwareMessage);
            }
        })
        .build();
```
方法 | 参数 | 说明
---|---|---
onVersionCallback | FirmwareVersionMessage | 执行queryFirmwareVersion回调此函数
onMotionCallback | MotionMessage | 执行startMotion()回调此函数
onUpdateFirmwareCallback | UpdateFirmwareMessage| 执行refreshFirmware回调此函数
onFailure | Throwable | 当程序异常时调用此函数

FirmwareVersionMessage.class 固件版本信息类
-----------
当调用queryFirmwareVersion()查询固件版本时.设备会响应此信息
变量 | 类型 | 说明
---|---|---
isValid | boolean | 是否支持版本(true/false)
version | int | 固件版本号 例:228

UpdateFirmwareMessage.class 
-----------
当调用refreshFirmware(file)固件更新时.设备会响应此信息.
type:1,升级中, 2,上传完成, 3,固件升级成功, 重启设备. 4,固件升级失败

变量 | 类型 | 说明
---|---|---
type | int | 数据类型
percent | Double | 当type为1时. 此参数为上传进度百分比
macAddress | String | 设备地址

MotionMessage.class 运动数据类
-----------
当调用startMotion(file)开始运动.设备会响应此信息.
只有执行DEBUG命令时才会产生原始数据

变量 | 类型 | 说明
---|---|---
seq | Integer | 十进制(序号)
smoothSmo2 | Integer | 十进制(平滑肌氧值)
smo2 | Integer | 十进制(实时肌氧值)
ele | Integer | 十进制(电量)
version | Integer | 十进制(T1版本号)
status | Integer | 十进制(告警状态)
datas | int[] | 十进制(原始数据值) 

常见异常信息
-----------
异常 | 说明
---|---
BluetoothDeviceException | 初始化失败,未设置可用的BluetoothDevice
BluetoothSocketException | 初始化失败,未设置可用的BluetoothSocket
IOException | BluetoothSocket 连接异常,无法正常读写
FileNotFoundException | 更新固件失败,固件文件不存在
NotFirmwareFileException | 更新固件失败,不是有效的固件文件
NotStartException | 调用其它操作命令时,未先调用 start() 函数,无法执行其它操作



