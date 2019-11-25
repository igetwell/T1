package tech.getwell.t1.throwables;

public class BluetoothSocketException extends RuntimeException{

    public BluetoothSocketException() {
    }

    public BluetoothSocketException(String message) {
        super(message);
    }

    public BluetoothSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public BluetoothSocketException(Throwable cause) {
        super(cause);
    }
}
