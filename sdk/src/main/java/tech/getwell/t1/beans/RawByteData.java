package tech.getwell.t1.beans;

import tech.getwell.t1.utils.LogUtils;

/**
 * 采样原始数据，供调试检查时使用，共49字节
 *
 * @author Wave
 * @date 2019/8/7
 */
public class RawByteData extends Smo2ByteData {
    /**
     * 9-48位
     * 其它数据 (近端LED数据、远端LED数据)
     */
    public byte[] others;

    public RawByteData(byte[] value) {
        super(value);
        others = new byte[48 - 9];
        if(value.length != 49){
            LogUtils.e("RawByteData : byte[] 长度不符合 49 ");
            return;
        }
        for (int i = 9; i < 48; i++) {
            others[i-9] = value[i];
        }
    }

    @Override
    public StringBuilder toHexStringBuilder() {
        return super.toHexStringBuilder();
    }
}
