package tech.getwell.demo.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import tech.getwell.t1.beans.Callback;
import tech.getwell.t1.beans.FirmwareVersionMessage;
import tech.getwell.t1.beans.MotionMessage;
import tech.getwell.t1.beans.UpdateFirmwareMessage;

/**
 * @author Wave
 * @date 2019/9/9
 */
public class SDKViewModel extends ViewModel {

    MutableLiveData<MotionMessage> motionMessageMutableLiveData;

    MutableLiveData<FirmwareVersionMessage> firmwareVersionMutableLiveData;

    MutableLiveData<UpdateFirmwareMessage> updateFirmwareMessageMutableLiveData;

    public MutableLiveData<MotionMessage> getMotionMessageMutableLiveData() {
        if(motionMessageMutableLiveData == null){
            motionMessageMutableLiveData = new MutableLiveData<>();
        }
        return motionMessageMutableLiveData;
    }

    public MutableLiveData<FirmwareVersionMessage> getFirmwareVersionMutableLiveData() {
        if(firmwareVersionMutableLiveData == null){
            firmwareVersionMutableLiveData = new MutableLiveData<>();
        }
        return firmwareVersionMutableLiveData;
    }

    public MutableLiveData<UpdateFirmwareMessage> getUpdateFirmwareMessageMutableLiveData() {
        if(updateFirmwareMessageMutableLiveData == null){
            updateFirmwareMessageMutableLiveData = new MutableLiveData<>();
        }
        return updateFirmwareMessageMutableLiveData;
    }
}
