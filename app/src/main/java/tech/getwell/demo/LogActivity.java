package tech.getwell.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tech.getwell.demo.adapters.LogAdapter;
import tech.getwell.demo.databinding.ActivityLogBinding;
import tech.getwell.demo.listeners.OnItemLogListener;
import tech.getwell.t1.logs.LogFile;
import tech.getwell.demo.viewmodels.LogViewModel;

/**
 * @author Wave
 * @date 2019/8/13
 */
public class LogActivity extends DataBindingActivity<ActivityLogBinding> implements OnItemLogListener {

    LogAdapter logAdapter;

    LogViewModel viewModel;

    Disposable disposable;

    String filePath;

    @Override
    protected int layoutId() {
        return R.layout.activity_log;
    }

    @Override
    public void onAfter() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        getViewDataBinding().rv.setLayoutManager(manager);

        logAdapter = new LogAdapter();
        logAdapter.setListener(this);
        getViewDataBinding().rv.setAdapter(logAdapter);

        // 开始观察 name
        viewModel = ViewModelProviders.of(this).get(LogViewModel.class);
        viewModel.getMutableLiveData().observe(this,(files)-> onLogFilesChanged(files));

        observableFileLogs(getCacheDir().getAbsolutePath() +"/"+ LogFile.DIR_NAME);
    }

    String getRootFilePath(){
        return getCacheDir().getAbsolutePath() +"/"+ LogFile.DIR_NAME;
    }

    boolean isRootFilePath(){
        return filePath.equals(getRootFilePath());
    }

    String getBackFilePath(){
        return filePath.substring(0,filePath.lastIndexOf("/"));
    }

    void observableFileLogs(String path){
        this.filePath = path;
        getViewDataBinding().setFilePath(this.filePath);
        disposable = Observable.create((ObservableEmitter<File[]> emitter) -> onFindLogs(path,emitter))
                .onTerminateDetach()
                .observeOn(Schedulers.io())
                .subscribe((File[] files)->onCallbackFiles(files));
    }

    void onFindLogs(String path,ObservableEmitter<File[]> emitter){
        emitter.onNext(findLogFiles(path));
    }

    File[] findLogFiles(String filePath){
        //String filePath = getCacheDir().getPath() +"/"+ LogFile.DIR_NAME;
        return new File(filePath).listFiles();
    }

    void onCallbackFiles(File[] files){
        viewModel.getMutableLiveData().postValue(files);
    }

    void onLogFilesChanged(File[] files){
        logAdapter.clearArray();
        logAdapter.setArray(new ArrayList<>(Arrays.asList(files)));
        logAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, File file) {
        if(file.isDirectory()){
            // 进入目录
            observableFileLogs(file.getAbsolutePath());
            return;
        }
        onShareFile(file);
    }

    @Override
    public void onBackPressed() {
        if(filePath == null || "".equals(filePath) || isRootFilePath()){
            super.onBackPressed();
            return;
        }
        //回退
        observableFileLogs(getBackFilePath());
    }

    void onShareFile(File file){
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("*/*");//设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_STREAM, getFileUri(file));//添加分享内容标题
        //share_intent.putExtra(Intent.EXTRA_TEXT, "share with you:"+"android");//添加分享内容
        //创建分享的Dialog
        share_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//授予临时权限别忘了
        share_intent = Intent.createChooser(share_intent, "share");
        //share_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(share_intent);

    }

    Uri getFileUri(File file){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N ){
            return FileProvider.getUriForFile(getApplicationContext(),"tech.getwell.t1.fileprovider",file);//file即为所要共享的文件的file
        }
        return Uri.fromFile(file);
    }


    @Override
    protected void onDestroy() {
        if(disposable != null && !disposable.isDisposed())disposable.dispose();
        super.onDestroy();
    }
}
