package tech.getwell.t1.throwables;

public class BluetoothDeviceException extends RuntimeException{

    public BluetoothDeviceException() {
    }

    public BluetoothDeviceException(String message) {
        super(message);
    }

    public BluetoothDeviceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BluetoothDeviceException(Throwable cause) {
        super(cause);
    }
}
