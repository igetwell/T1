package tech.getwell.demo.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;

/**
 * @author Wave
 * @date 2019/8/13
 */
public class LogViewModel extends ViewModel {

    MutableLiveData<File[]> logMutableLiveData;

    public MutableLiveData<File[]> getMutableLiveData() {
        if(logMutableLiveData == null){
            logMutableLiveData = new MutableLiveData<>();
        }
        return logMutableLiveData;
    }
}
