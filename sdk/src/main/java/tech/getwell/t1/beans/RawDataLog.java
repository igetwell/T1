package tech.getwell.t1.beans;

/**
 * @author Wave
 * @date 2019/8/13
 */
public class RawDataLog {
    //int frame, double ratio, int[] datas, int temp1, int temp2
    public int frame;

    public double ratio;

    public int[] datas;

    public int temp1;

    public int temp2;

    public RawDataLog() {
    }

    public RawDataLog(RawSmo2Data rawSmo2Data) {
        this.frame = rawSmo2Data.seq;
        this.ratio = -1;
        this.datas = rawSmo2Data.datas;
        this.temp1 = -1;
        this.temp2 = -1;
    }
}
