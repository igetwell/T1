package tech.getwell.demo.adapters;

/**
 * @author Wave.Zhang
 * @time 2018/8/3
 * @email 284388431@qq.com
 */

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseAdapter<T extends ViewDataBinding> extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {

    public class ViewHolder extends DataBindingViewHolder<T> {

        public ViewHolder(T mViewDataBinding) {
            super(mViewDataBinding);
        }

    }

    abstract public int getLayoutId();

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getLayoutId(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseAdapter.ViewHolder holder, int position) {
        onBindViewDataBinding((T) holder.getViewDataBinding(), position);
    }

    public abstract void onBindViewDataBinding(T viewDataBinding, int position);
}