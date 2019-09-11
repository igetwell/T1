package tech.getwell.t1.utils;

import java.util.ArrayList;

/**
 * @author Wave
 * @date 2019/8/26
 */
public class SmoothSom2 {

    int max = 5;

    ArrayList<Integer> arrayList = new ArrayList<>();

    public void setMax(int max) {
        arrayList.clear();
        this.max = max;
    }

    public int put(Integer value){
        if(arrayList.size() >= max){
            arrayList.remove(0);
        }
        arrayList.add(value);
        return smoothValue();
    }

    public int smoothValue(){
        int value = 0;
        int size = 0;
        for (Integer temp : arrayList) {
            value += temp;
            size ++;
        }
        return value / size;
    }
}
