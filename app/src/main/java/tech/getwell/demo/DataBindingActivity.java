package tech.getwell.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * @author Wave
 * @date 2019/7/30
 */
public abstract class DataBindingActivity<T extends ViewDataBinding> extends AppCompatActivity {

    T viewDataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewDataBinding = DataBindingUtil.setContentView(this,layoutId());
        onAfter();
    }

    public T getViewDataBinding() {
        return viewDataBinding;
    }

    protected abstract int layoutId();

    public abstract void onAfter();
}
