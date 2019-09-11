package tech.getwell.demo.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import tech.getwell.demo.R;
import tech.getwell.demo.beans.BleDevice;
import tech.getwell.demo.databinding.ItemDeviceBinding;
import tech.getwell.demo.listeners.OnItemT1DeviceListener;

/**
 * @author Wave
 * @date 2019/7/31
 */
public class T1Adapter extends BaseArrayAdapter<BleDevice, ItemDeviceBinding>{

    OnItemT1DeviceListener listener;

    public void setListener(OnItemT1DeviceListener listener) {
        this.listener = listener;
    }

    public T1Adapter() {
        this.clearArray();
//        this.addArray(createDevice("8C:DE:52:C5:1F:7F","Wz-001"));
//        this.addArray(createDevice("8C:DE:52:C5:1F:7F","Wz-002"));
//        this.addArray(createDevice("8C:DE:52:C5:1F:7F","Wz-003"));
    }

    BleDevice createDevice(String address,String name){
        BleDevice bluetoothDevice = new BleDevice();
        bluetoothDevice.address = address;
        bluetoothDevice.name = name;
        return bluetoothDevice;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_device;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
        if(listener != null)viewHolder.getViewDataBinding().setListener(listener);
        return viewHolder;
    }

    @Override
    public void onBindViewDataBinding(ItemDeviceBinding viewDataBinding, int position) {
        viewDataBinding.setDev(getArray().get(position));
    }
}
