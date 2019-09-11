package tech.getwell.demo.adapters;


import androidx.databinding.ViewDataBinding;
import java.util.ArrayList;

/**
 * @author Wave
 * @date 2019/7/30
 */
public abstract class BaseArrayAdapter<A,T extends ViewDataBinding> extends BaseAdapter<T> {

    ArrayList<A> array = new ArrayList<>();

    public void setArray(ArrayList<A> mArray) {
        this.array = mArray;
    }

    public ArrayList<A> getArray() {
        return array;
    }

    public void clearArray(){
        this.array.clear();
    }

    public void addArray(A e){
        this.array.add(e);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }
}
