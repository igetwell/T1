package tech.getwell.t1.beans;

/**
 *
 * 帧格式：
 * 			73  a1  06  nn  nn  xx  yy  zz  ss
 * 说明：
 * nn:   表示上报序号（两个字节表示，高字节在前，低字节在后）
 * xx：  表示肌氧 （0x00 ~ 0x64）
 * yy：  表示电量（0x00 ~ 0x64）
 * zz：  表示T1版本号。
 * 	      版本号字段说明如下：
 *     	b7-b6：主版本号（00~03）0x00--0x03
 * 		b5-b0:  次版本号(00~63)  0x00 --0x3F
 * 		ss：表示告警状态（00无告警）。
 *
 * @author Wave
 * @date 2019/7/29
 */
public class Smo2HexData {
    /**
     * 表示上报序号（两个字节表示，高字节在前，低字节在后）
     */
    public String nn;
    /**
     * 表示肌氧 （0x00 ~ 0x64）
     */
    public String xx;
    /**
     * 表示电量（0x00 ~ 0x64）
     */
    public String yy;
    /**
     * 表示T1版本号 版本号字段说明如下：b7-b6：主版本号（00~03）0x00--0x03  b5-b0:  次版本号(00~63)  0x00 --0x3F
     */
    public String zz;
    /**
     * 表示告警状态（00无告警）
     */
    public String ss;

    public Smo2HexData(){}

//    public Smo2HexData(String response){
//        if(!response.startsWith("73a106") || response.length() != 18){
//            //println("非法的信息");
//            return;
//        }
//        this.nn = response.substring(6,10);
//        this.xx = response.substring(10,12);
//        this.yy = response.substring(12,14);
//        this.zz = response.substring(14,16);
//        this.ss = response.substring(16,18);
//    }

    public String byteToHexString(byte b){
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    public Smo2HexData(Smo2ByteData byteData){
        this.nn = byteToHexString(byteData.seq0) + byteToHexString(byteData.seq1);
        this.xx = byteToHexString(byteData.smo2);
        this.yy = byteToHexString(byteData.ele);
        this.zz = byteToHexString(byteData.version);
        this.ss = byteToHexString(byteData.status);
    }
}
