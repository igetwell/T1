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
     * 原始肌氧气值
     */
    public Integer smo2 = -1;
    /**
     * 平滑肌氧气值
     */
    public Integer smoothSmo2 = -1;

    public Callback(){

    }

    public Callback(int code){
        this.code = code;
        this.smo2 = -1;
        this.smoothSmo2 = -1;
    }

    public Callback(int code, Integer value, Integer smoothValue) {
        this.code = code;
        this.smo2 = value;
        this.smoothSmo2 = smoothValue;
    }
}
