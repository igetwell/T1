package tech.getwell.t1.timrs;

import java.util.TimerTask;
import tech.getwell.t1.utils.LogUtils;

/**
 * @author Wave.Zhang
 * @time 2019/3/22
 * @email 284388431@qq.com
 */
public class To2TimeOutTask extends TimerTask {

    Long dataTime;

    Long DEFAULT_TIME = 5 * 1000L;

    Long MAX_TIME = 10 * 1000L;

    OnTimeOutTaskListener listener;

    public To2TimeOutTask() {
        setDataTime(System.currentTimeMillis());

    }

    public void setDataTime(Long dataTime) {
        this.dataTime = dataTime;
        //LogUtils.d("setDataTime time = "+System.currentTimeMillis() +" dataTime = "+this.dataTime );
    }

    public void setTimeOut(long time){
        this.MAX_TIME = time < DEFAULT_TIME ? DEFAULT_TIME : time;
    }

    public void setListener(OnTimeOutTaskListener listener) {
        this.listener = listener;
    }

    void onConnectionTimeOut(){
        LogUtils.e("超时请求... ");
        //Smo2TimeOutThrowable throwable = new Smo2TimeOutThrowable("Smo2TimeOut time out ");
        if(listener != null)listener.onTimeOutCallback();
    }

    @Override
    public void run() {
        //LogUtils.d("run time = "+System.currentTimeMillis() +" dataTime = "+this.dataTime +" maxTime = "+MAX_TIME);
        if(System.currentTimeMillis() - this.dataTime > MAX_TIME){
            onConnectionTimeOut();
            return;
        }
    }

    public interface OnTimeOutTaskListener{

        void onTimeOutCallback();
    }

}
