package tech.getwell.demo.bles.listeners;

/**
 * @author Wave
 * @date 2019/7/30
 */
public interface OnT1ConnectListener extends OnT1ReadListener{

    void onConnected();

    void onConnectionFailed(Throwable throwable);
}
