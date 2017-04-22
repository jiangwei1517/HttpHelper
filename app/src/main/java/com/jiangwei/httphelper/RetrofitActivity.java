package com.jiangwei.httphelper;

import com.facebook.stetho.Stetho;
import com.jiangwei.httplib.http.ApiException;
import com.jiangwei.httplib.http.HttpHelper;
import com.jiangwei.httplib.model.ZhihuStory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class RetrofitActivity extends AppCompatActivity {
    private static final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HttpHelper.init(false, "http://news-at.zhihu.com", "http://news-at.zhihu.com");
        Button btn = (Button) findViewById(R.id.btn);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Stetho.initializeWithDefaults(this);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Subscription subscription = request(HttpHelper.api().getZhihuStoryGet(String.valueOf(9355443)), new Action1<ZhihuStory>() {
                    @Override
                    public void call(ZhihuStory zhihuStory) {
                        Toast.makeText(RetrofitActivity.this, zhihuStory.title, Toast.LENGTH_SHORT)
                                .show();
                    }
                }, new Action1<ApiException>() {
                    @Override
                    public void call(ApiException e) {
                        Toast.makeText(RetrofitActivity.this, "网络链接异常", Toast.LENGTH_SHORT).show();
                    }
                });
                mCompositeSubscription.add(subscription);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    public @NonNull <T> Subscription request(@NonNull Observable<T> observable, @NonNull Action1<T> success,
            @NonNull final Action1<ApiException> error) {
        return HttpHelper.request(mCompositeSubscription, observable, success, error);
    }
}
