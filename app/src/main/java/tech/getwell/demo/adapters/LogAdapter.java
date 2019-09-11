package tech.getwell.demo.adapters;

import java.io.File;

import tech.getwell.demo.R;
import tech.getwell.demo.databinding.ItemLogBinding;
import tech.getwell.demo.listeners.OnItemLogListener;

/**
 * @author Wave
 * @date 2019/8/13
 */
public class LogAdapter extends BaseArrayAdapter<File, ItemLogBinding>{

    OnItemLogListener listener;

    @Override
    public int getLayoutId() {
        return R.layout.item_log;
    }

    public void setListener(OnItemLogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewDataBinding(ItemLogBinding viewDataBinding, int position) {
        viewDataBinding.setListener(listener);
        viewDataBinding.setFile(getArray().get(position));
    }
}
