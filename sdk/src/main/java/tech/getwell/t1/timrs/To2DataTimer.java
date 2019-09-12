package tech.getwell.t1.timrs;

import java.util.Timer;

/**
 * @author Wave.Zhang
 * @time 2019/3/22
 * @email 284388431@qq.com
 */
public class To2DataTimer extends Timer {

    Builder builder;

    To2TimeOutTask timeOutTask;

    To2DataTimer(Builder builder){
        this.builder = builder;
    }

    public void setDataTime(Long dataTime) {
        timeOutTask.setDataTime(dataTime);
    }

    public void start(){
        if(timeOutTask != null){
            timeOutTask.cancel();
        }
        timeOutTask = new To2TimeOutTask();
        timeOutTask.setTimeOut(builder.time);
        timeOutTask.setListener(builder.listener);
        this.schedule(timeOutTask,0,1000);
    }

    public void close(){
        if(timeOutTask != null)timeOutTask.cancel();
        this.cancel();
    }

    public static final class Builder{

        Long time;

        To2TimeOutTask.OnTimeOutTaskListener listener;

        public Builder setTimeOutListener(To2TimeOutTask.OnTimeOutTaskListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setTimeOut(Long time){
            this.time = time;
            return this;
        }

        public To2DataTimer build(){
            return new To2DataTimer(this);
        }
    }

}
