package tech.getwell.demo.beans;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Wave
 * @date 2019/7/31
 */
public class BleDevice implements Parcelable {

    public String name;

    public String address;

    public BleDevice(){

    }

    public BleDevice(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public BleDevice(BluetoothDevice device){
        this.name = device.getName();
        this.address = device.getAddress();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.address);
    }

    protected BleDevice(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
    }

    public static final Parcelable.Creator<BleDevice> CREATOR = new Parcelable.Creator<BleDevice>() {
        @Override
        public BleDevice createFromParcel(Parcel source) {
            return new BleDevice(source);
        }

        @Override
        public BleDevice[] newArray(int size) {
            return new BleDevice[size];
        }
    };
}
