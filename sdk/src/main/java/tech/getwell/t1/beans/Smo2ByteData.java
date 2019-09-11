package tech.getwell.t1.beans;

/**
 * 正常的数据上报，共9个字节。
 *
 * @author Wave
 * @date 2019/8/7
 */
public class Smo2ByteData {
    /**
     * 数据包起始字符 (X073)
     */
    public byte byte0;
    /**
     * 协议类型 (X0A1)
     */
    public byte byte1;
    /**
     * 长度字节
     */
    public byte byte2;
    /**
     * 序号高字节
     */
    public byte seq0;
    /**
     * 序号低字节
     */
    public byte seq1;
    /**
     * （固定）肌氧字段
     */
    public byte smo2;
    /**
     * （固定）电量字段
     */
    public byte ele;
    /**
     * （固定）版本号字段
     */
    public byte version;
    /**
     * （固定）告警字段
     */
    public byte status;

    public Smo2ByteData(byte[] value){
        this.byte0 = value[0];
        this.byte1 = value[1];
        this.byte2 = value[2];
        this.seq0 = value[3];
        this.seq1 = value[4];
        this.smo2 = value[5];
        this.ele = value[6];
        this.version = value[7];
        this.status = value[8];
    }

    public StringBuilder toHexStringBuilder() {
        return new StringBuilder()
                .append(byteToHexString(byte0))
                .append(byteToHexString(byte1))
                .append(byteToHexString(byte2))
                .append(byteToHexString(seq0))
                .append(byteToHexString(seq1))
                .append(byteToHexString(smo2))
                .append(byteToHexString(ele))
                .append(byteToHexString(version))
                .append(byteToHexString(status));
    }

    public String byteToHexString(byte b){
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }
}
