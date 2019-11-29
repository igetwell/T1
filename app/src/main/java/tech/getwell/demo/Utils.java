package tech.getwell.demo;

import java.nio.Buffer;

public class Utils {

    public static String toRawDatas(int[] datas){
        if(datas == null ){
            return "原始数据为null";
        }
        if(datas.length <= 0){
            return "原始数据 length <= 0";
        }
        if(datas.length != 9){
            return "原始数据 length != 9";
        }

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < datas.length; i++) {
            buffer.append(datas[i]);
            buffer.append(",");
        }
        return buffer.toString();
    }
}
