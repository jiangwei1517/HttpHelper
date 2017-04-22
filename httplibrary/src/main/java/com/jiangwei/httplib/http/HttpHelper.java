package com.jiangwei.httplib.http;

import java.io.File;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.json.JSONObject;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.ParameterizedType;
import com.jiangwei.httplib.api.APIService;

import android.support.annotation.NonNull;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * author: jiangwei18 on 17/4/13 10:45 email: jiangwei18@baidu.com Hi: jwill金牛
 */

public class HttpHelper {

    private static HttpHelper sInstance;

    // 连续三个SSL异常切换到HTTP
    private static int sHttpsErrorCounter = 0;

    private Retrofit mRetrofit;
    private APIService mService;
    private boolean mIsReleased;
    private static String mBaseUrlHttps;
    private static String mBaseUrlHttp;

    private static final Func1<Throwable, Throwable> sErrorReturn = new Func1<Throwable, Throwable>() {
        @Override
        public Throwable call(Throwable throwable) {
            return throwable;
        }
    };

    private HttpHelper(boolean isReleased) {
        mIsReleased = isReleased;
        OkHttpClient client = HttpBuilder.createOkClient(isReleased, new HttpBuilder.BuildAction() {
            @Override
            public void call(OkHttpClient.Builder builder) {
                builder.addInterceptor(new ParamsInterceptor(new ParamsBuilder()));
            }
        });
        // 默认https通信
        mRetrofit = HttpBuilder.createRetrofit(mBaseUrlHttps, client);
        mService = mRetrofit.create(APIService.class);
    }

    public APIService start() {
        return mService;
    }

    private static class SwitchToHttpException extends Exception {

        public SwitchToHttpException(String detailMessage, Throwable causeThrowable) {
            super(detailMessage, causeThrowable);
        }
    }

    // 切换到http通信
    public void switchToHttp(Throwable throwable) {
        if (sHttpsErrorCounter < 3) {
            return;
        }
        OkHttpClient client = HttpBuilder.createOkClient(mIsReleased, new HttpBuilder.BuildAction() {
            @Override
            public void call(OkHttpClient.Builder builder) {
                builder.addInterceptor(new ParamsInterceptor(new ParamsBuilder()));
            }
        });
        mRetrofit = HttpBuilder.createRetrofit(mBaseUrlHttp, client);
        mService = mRetrofit.create(APIService.class);
        Timber.tag("HttpHelper").wtf(new SwitchToHttpException("Switch to HTTP", throwable));
    }

    public static void init(boolean isReleased, String baseUrlHttps, String baseUrlHttp) {
        mBaseUrlHttps = baseUrlHttps;
        mBaseUrlHttp = baseUrlHttp;
        sInstance = new HttpHelper(isReleased);
    }

    public static boolean isSSLException(Throwable throwable) {
        boolean value = throwable instanceof SSLHandshakeException || throwable instanceof SSLException;
        if (value) {
            sHttpsErrorCounter++;
        } else {
            sHttpsErrorCounter = 0;
        }
        return value;
    }

    public static HttpHelper getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Call HttpHelper.init(boolean) first");
        }
        return sInstance;
    }

    public static APIService api() {
        if (sInstance == null) {
            throw new IllegalStateException("Call HttpHelper.init(boolean) first");
        }
        return sInstance.mService;
    }

    public static @NonNull <T> Subscription request(@NonNull final CompositeSubscription subscriptions,
            @NonNull Observable<T> observable, @NonNull final Action1<T> success,
            @NonNull final Action1<ApiException> error) {
        RequestSubscriber subscriber = new RequestSubscriber() {

            @Override
            public void onCompleted() {
                if (subscription != null) {
                    subscriptions.remove(subscription);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (subscription != null) {
                    subscriptions.remove(subscription);
                }
                Timber.tag("HttpHelper.request").w(throwable, "HttpHelper.request.onError()");
                if (HttpHelper.isSSLException(throwable)) {
                    HttpHelper.getInstance().switchToHttp(throwable);
                    Timber.tag("HttpHelper.request").e(throwable, "SSLException");
                }

                if (throwable instanceof ApiException) {
                    // ApiException 赋值?
                    ApiException exception = (ApiException) throwable;
                    if (exception.getLocalErrorCode() == ErrorCode.ERROR_WITH_MESSAGE) {
                        error.call((ApiException) throwable);
                    } else {
                        error.call((ApiException) throwable);
                    }
                } else {
                    error.call(new ApiException(ErrorCode.NETWORK_ERROR));
                }
            }

            @Override
            public void onNext(Object o) {
                if (o instanceof Throwable) {
                    this.onError((Throwable) o);
                } else {
                    sHttpsErrorCounter = 0;
                    success.call((T) o);
                }
            }
        };

        // 必要时候用map(new ResponseTransform())进行类型转换
        final Subscription subscription = observable.doOnNext(new Action1() {
            @Override
            public void call(Object o) {
                T model = (T) o;
                try {
                    String json = new JSONObject(LoganSquare.serialize(model, new ParameterizedType<T>() {
                    })).toString(4);
                    Timber.tag("Response").i(json);
                } catch (Exception e) {
                    Timber.tag("ResponseTransform").w(e, "Error when serialize %s", o.toString());
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
        subscriptions.add(subscription);
        subscriber.subscription = subscription;
        return subscription;
    }

    private static abstract class RequestSubscriber extends Subscriber {
        Subscription subscription;
    }

    /*
     * Content-Type的类型扩充了multipart/form-data用以支持向服务器发送二进制数据 ①application/x-www-form-urlencoded(默认值) ②multipart/form-data
     */

    public MultipartBody getMultipartBody(@NonNull File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("file", file.getName(), requestBody);
        // 服务器协商表单
        // builder.addFormDataPart("pic_type", "desc_pics");
        // 必须设置contentType为Content-Type: multipart/form-data 否则服务端拿不到数据
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    public MultipartBody getAudioMultipartBody(int duration, @NonNull File audioFile) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), audioFile);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.addFormDataPart("file", audioFile.getName(), requestBody);
        // 服务器协商表单
        // builder.addFormDataPart("duration", "" + duration);
        // 必须设置contentType为Content-Type: multipart/form-data 否则服务端拿不到数据
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

}
