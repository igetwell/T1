package tech.getwell.t1.beans;

/**
 * @author Wave
 * @date 2019/8/13
 */
public class Smo2DataLog {
    //int smo2_filter,int hr, double conduction, int temp1, int temp2,double latitude, double longitude, double speed
    public int smo2_filter;

    public int hr;

    public double conduction;

    public int temp1;

    public int temp2;

    public double latitude;

    public double longitude;

    public double speed;

    public Smo2DataLog() {
    }

    public Smo2DataLog(Smo2Data smo2Data) {
        this.smo2_filter = smo2Data.smo2;
        this.hr = -1;
        this.conduction = -1;
        this.temp1 = -1;
        this.temp2 = -1;
        this.latitude = -1;
        this.longitude = -1;
        this.speed = -1;
    }
}
