package tech.getwell.t1.listeners;

import tech.getwell.t1.beans.MotionMessage;

/**
 * @author Wave
 * @date 2019/9/19
 */
public interface OnJDT1RawDataListener {

    void onRawDataCallback(MotionMessage motionMessage);
}
