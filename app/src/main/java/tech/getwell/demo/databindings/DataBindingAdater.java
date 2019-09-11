package tech.getwell.demo.databindings;

import android.view.View;

import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;

/**
 * @author Wave
 * @date 2019/8/14
 */
@BindingMethods({
        @BindingMethod(
                type = View.class,
                attribute = "android:selected",
                method = ""),
})
public class DataBindingAdater {

    @BindingAdapter("android:selected")
    public static void setViewSelected(View view,Boolean flag){
        if(view == null || flag == null){
            return;
        }
        view.setSelected(flag);
    }
}
