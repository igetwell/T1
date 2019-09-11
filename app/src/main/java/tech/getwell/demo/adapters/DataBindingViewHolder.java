package tech.getwell.demo.adapters;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Wave
 * @date 2019/7/30
 */
public class DataBindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    T viewDataBinding;

    public DataBindingViewHolder(@NonNull T viewDataBinding) {
        super(viewDataBinding.getRoot());
        this.viewDataBinding = viewDataBinding;
    }

    public T getViewDataBinding() {
        return viewDataBinding;
    }
}
