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
public class Smo2Data extends Smo2HexData{

    /**
     * 表示上报序号（两个字节表示，高字节在前，低字节在后）
     */
    public Integer seq;

    /**
     * 表示肌氧 （0x00 ~ 0x64）(平滑之后的值)
     */
    public Integer smoothSmo2;
    /**
     * 表示肌氧 （0x00 ~ 0x64）
     */
    public Integer smo2;
    /**
     * 表示电量（0x00 ~ 0x64）
     */
    public Integer ele;
    /**
     * 表示T1版本号 版本号字段说明如下：b7-b6：主版本号（00~03）0x00--0x03  b5-b0:  次版本号(00~63)  0x00 --0x3F
     */
    public Integer version;
    /**
     * 表示告警状态（00无告警）
     */
    public Integer status;

    public Smo2Data(){
        super();
    }

//    /**
//     * println("序号为:"+value.substring(6,10) +" 转INT:"+Integer.parseInt(value.substring(6,10),16));
//     * println("肌氧为:"+value.substring(10,12) +" 转INT:"+Integer.parseInt(value.substring(10,12),16));
//     * println("电量为:"+value.substring(12,14) +" 转INT:"+Integer.parseInt(value.substring(12,14),16));
//     * println("版本为:"+value.substring(14,16) +" 转INT:"+Integer.parseInt(value.substring(14,16),16));
//     * println("状态为:"+value.substring(16,18) +" 转INT:"+Integer.parseInt(value.substring(16,18),16));
//     * @param value
//     */
//    public Smo2Data(String value){
//        super(value);
//        parseIntAll();
//    }

    public Smo2Data(Smo2ByteData byteData) {
        super(byteData);
        parseIntAll();
    }

    void parseIntAll(){
        this.seq = parseInt(this.nn);
        this.smo2 = parseInt(this.xx);
        this.ele = parseInt(this.yy);
        this.version = parseInt(this.zz);
        this.status = parseInt(this.ss);
    }

    /**
     * 将原始数据转为10进制数据,无法转换则为-1
     * @param oldValue
     * @return
     */
    Integer parseInt(String oldValue){
        return oldValue == null ? -1 : Integer.parseInt(oldValue,16);
    }

    public Smo2DataLog toSmo2DataLog(){
        return new Smo2DataLog(this);
    }
}
