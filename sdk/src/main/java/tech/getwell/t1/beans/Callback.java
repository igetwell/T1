package tech.getwell.t1.beans;

/**
 * @author Wave
 * @date 2019/9/6
 */
public class Callback {
    /**
     * 响应码
     */
    public int code;
    /**
     * 固件版本号
     */
    public int version;
    /**
     * 原始肌氧气值
     */
    public Integer smo2 = -1;
    /**
     * 平滑肌氧气值
     */
    public Integer smoothSmo2 = -1;

    public Callback(){

    }

    public Callback(int code,int version){
        this.code = code;
        this.version = version;
        this.smo2 = -1;
        this.smoothSmo2 = -1;
    }

    public Callback(int code,int version, Integer smo2, Integer smoothValue) {
        this.code = code;
        this.version = version;
        this.smo2 = smo2;
        this.smoothSmo2 = smoothValue;
    }
}
