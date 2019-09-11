package tech.getwell.demo;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import java.util.HashMap;
import tech.getwell.demo.adapters.T1Adapter;
import tech.getwell.demo.beans.BleDevice;
import tech.getwell.demo.databinding.ActivityMainBinding;
import tech.getwell.demo.listeners.OnItemT1DeviceListener;
import tech.getwell.t1.utils.LogUtils;

/**
 * @author Wave
 * @date 2019/7/30
 */
public class MainActivity extends DataBindingActivity<ActivityMainBinding> implements OnItemT1DeviceListener {

    // M0393
    // 8C:DE:52:C5:1F:7F
    // T1ConnectTask task;

    BluetoothAdapter bluetoothAdapter;

    T1Adapter t1Adapter;

    final int REQUEST_CODE_REQUEST_PERMISSIONS = 0x001;
    /**
     * 0,可用
     * 1,当前手机不支持
     * 2,未打开蓝牙
     * 3,无蓝牙/位置权限
     * 4,权限被拒绝
     * 5,权限被禁止
     */
    int bleAvailable;
    /**
     * 校验蓝牙权限
     */
    private void checkBluetoothPermission(){
        if (Build.VERSION.SDK_INT >= 23){
            //校验是否已具有模糊定位权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                setBleAvailable(3);
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE_REQUEST_PERMISSIONS);
            }
        }else{
            // TODO
            setBleAvailable(0);
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onAfter() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        getViewDataBinding().rv1.setLayoutManager(manager);

        t1Adapter = new T1Adapter();
        t1Adapter.setListener(this);
        getViewDataBinding().rv1.setAdapter(t1Adapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 当前手机不支持
        if(bluetoothAdapter == null){
            setBleAvailable(1);
            return;
        }
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);//注册广播接收信号
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceiver, intentFilter);

        checkBluetoothPermission();
    }

    // 广播接收附近可用设备信息
    BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent == null)return;
            String action = intent.getAction();
            if(action == null)return;
            LogUtils.d("action = "+action);
            switch (action){
                    // 发现可用设备信息
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    onBluetoothDeviceChanged(device);
                    break;
                    // 开始搜索
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    LogUtils.d("开始搜索");
                    getViewDataBinding().btnQuery.setSelected(true);
                    break;
                    // 搜索结束
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    LogUtils.d("搜索结束");
                    getViewDataBinding().btnQuery.setSelected(false);
                    break;
            }
        }
    };

    HashMap<String,BluetoothDevice> devLogs = new HashMap<>();

    void onBluetoothDeviceChanged(BluetoothDevice device){
        if(devLogs.containsKey(device.getAddress())){
            LogUtils.d("已存在相同的设备信息");
            return;
        }
        if(device.getName() == null || !device.getName().startsWith("GW_")){
            LogUtils.d("非T1设备信息");
            return;
        }
        devLogs.put(device.getAddress(),device);
        t1Adapter.addArray(new BleDevice(device));
        t1Adapter.notifyDataSetChanged();
    }

    public void setBleAvailable(int bleAvailable) {
        this.bleAvailable = bleAvailable;
        this.bleAvailable =  this.bleAvailable == 0 ? bluetoothAdapter.isEnabled() ? 0 : 2 : this.bleAvailable;
        LogUtils.d("this.bleAvailable = "+this.bleAvailable);
        getViewDataBinding().setBle(this.bleAvailable);
    }

    public void openFileLogs(View view){
        Intent intent = new Intent(this,LogActivity.class);
        startActivity(intent);
    }

    public void onBeginClick(View view){
        //task.beginData();
        if(bleAvailable != 0)return;
        if(bluetoothAdapter == null)return;
        if(bluetoothAdapter.isDiscovering())return;
        devLogs.clear();
        t1Adapter.clearArray();
        t1Adapter.notifyDataSetChanged();
        bluetoothAdapter.startDiscovery();
    }

    public void onStopClick(View view){
        if(bluetoothAdapter == null)return;
        if(!bluetoothAdapter.isDiscovering())return;
        bluetoothAdapter.cancelDiscovery();
    }

    @Override
    public void onItemClick(View view, BleDevice device) {
        open(device);
    }

    void open(BleDevice device){
        Intent intent = new Intent(this,SDKActivity.class);
        intent.putExtra(SDKActivity.EXTRA_T1,device);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_REQUEST_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO request success
                    setBleAvailable(0);
                    return;
                }
                // 权限被禁止了
                if(!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])){
                    LogUtils.e("权限被禁止了,无法正常使用蓝牙");
                    setBleAvailable(5);
                    return;
                }
                if(grantResults[0] == -1){
                    LogUtils.e("权限被拒绝了,无法正常使用蓝牙");
                    //拒绝
                    setBleAvailable(4);
                    return;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if(bluetoothReceiver != null)unregisterReceiver(bluetoothReceiver);
        super.onDestroy();
    }
}
