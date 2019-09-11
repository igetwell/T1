package tech.getwell.demo.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import tech.getwell.t1.beans.Smo2Data;

/**
 * @author Wave
 * @date 2019/7/30
 */
public class T1DataViewModel extends ViewModel {

    MutableLiveData<Smo2Data> t1DataMutableLiveData;

    public MutableLiveData<Smo2Data> getSmo2DataMutableLiveData() {
        if(t1DataMutableLiveData == null){
            t1DataMutableLiveData = new MutableLiveData<>();
        }
        return t1DataMutableLiveData;
    }

}
