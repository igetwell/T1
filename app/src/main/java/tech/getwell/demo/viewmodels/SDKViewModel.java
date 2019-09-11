package tech.getwell.demo.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import tech.getwell.t1.beans.Callback;

/**
 * @author Wave
 * @date 2019/9/9
 */
public class SDKViewModel extends ViewModel {

    MutableLiveData<Callback> callbackMutableLiveData;

    public MutableLiveData<Callback> getCallbackMutableLiveData() {
        if(callbackMutableLiveData == null){
            callbackMutableLiveData = new MutableLiveData<>();
        }
        return callbackMutableLiveData;
    }
}
