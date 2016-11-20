package com.zzzmode.appopsx.ui.main;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.zzzmode.appopsx.R;
import com.zzzmode.appopsx.ui.model.AppInfo;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private MainListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(getApplicationContext(),R.drawable.list_divider_h),true));

        adapter=new MainListAdapter();
        recyclerView.setAdapter(adapter);

        Observable.create(new ObservableOnSubscribe<AppInfo>() {
            @Override
            public void subscribe(ObservableEmitter<AppInfo> e) throws Exception {
                PackageManager packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
                for (PackageInfo installedPackage : installedPackages) {
                    if( (installedPackage.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        AppInfo info = new AppInfo();
                        info.packageName = installedPackage.packageName;
                        info.appName = String.valueOf(installedPackage.applicationInfo.loadLabel(packageManager));
                        info.icon = installedPackage.applicationInfo.loadIcon(packageManager);
                        e.onNext(info);
                    }
                }
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new ResourceObserver<AppInfo>() {
            @Override
            public void onNext(AppInfo value) {
                adapter.addItem(value);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}
