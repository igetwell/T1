package tech.getwell.demo.bles;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * T1 蓝牙重连接机制
 * @author Wave
 * @date 2019/8/1
 */
public class T1RepeatWhen implements Function<Observable<Object>, ObservableSource<?>> {

    @Override
    public ObservableSource<?> apply(Observable<Object> objectObservable) {
        return objectObservable.flatMap((Object o)->onApply());
    }

    int count = 0;

    ObservableSource<?> onApply(){
        if(count >= 3){
            return Observable.error(new Throwable("502:蓝牙连接失败"));
        }
        count++;
        return Observable.just("再来一次");
    }
}
