package tech.getwell.t1.beans;

import tech.getwell.t1.utils.LogUtils;

/**
 * @author Wave
 * @date 2019/8/12
 */
public class RawSmo2Data extends Smo2Data{

    public int[] datas;

    public RawSmo2Data(RawByteData byteData) {
        super(byteData);
        if(byteData.others == null){
            LogUtils.e("byteData others == null ");
            return;
        }
        int len = byteData.others.length;
        crate(byteData.others,0,0,len / 4);
    }

    void crate(byte[] others,int index,int start, int maxIndex){
        if(datas == null){
            datas = new int[maxIndex];
        }
        if(index >= maxIndex){
            return;
        }
        String temp = "";
        int maxLen = start + 4;
        for (int i = start; i < maxLen; i++) {
            String hex = Integer.toHexString(others[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            temp = temp + hex;
        }
        //System.out.println(index + " temp = "+temp);
        datas[index] = Integer.parseInt(temp,16);
        index++;
        crate(others,index,maxLen,maxIndex);
    }

    public String toDataString(){
        String temp = "";
        if(datas == null)return temp;
        for (int i = 0; i < datas.length; i++) {
            temp += datas[i] +" ";
        }
        return temp;
    }

    public RawDataLog toRawDataLog(){
        return new RawDataLog(this);
    }
}
