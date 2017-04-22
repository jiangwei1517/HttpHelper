# HttpHelper

![MacDown logo](https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1492851526865&di=d12c717f1be015eb38fa2fd368386fdb&imgtype=0&src=http%3A%2F%2Ft.388g.com%2Fuploads%2Fallimg%2F160720%2F5-160H00ZG3.jpg)

## 解决问题
* 网络请求（http、https）
* 异常处理
* Refrofit与Rxjava兼容
* 大型项目管理
* 得到json数据后，快速解析

## 基本思想
* Retrofit+Rxjava+RxAndroid
* 自定义APIException，根据Server端返回的code码解析json数据
* HttpHelper支持文件上传
* 请求过滤器，根据项目具体要求，添加自定义token、channel、sign、deviceId、requestI的、time等字段，便于server端校验
* Logansquare解析json数据

## 使用方法

在Application当中：

* 第一个参数httpsUrl
* 第二个参数httpUrl.HttpHelper首先https链接，当https不可用时切换到http链接。

 	     HttpHelper.init(false, "http://news-at.zhihu.com", "http://news-at.zhihu.com");
 	
### 请求方法  

* 第一个参数是Retrofit的请求Obsevable
* 第二个参数是请求成功之后的回调参数
* 第三个参数是请求失败的回调参数。
  
		public @NonNull <T> Subscription request(@NonNull Observable<T> observable, @NonNull Action1<T> success,
            @NonNull final Action1<ApiException> error) {
       		return HttpHelper.request(mCompositeSubscription, observable, success, error);
    	}
#### 自定义ParamsInterceptor
* buildCommonParams：添加token、channel等字段到url
* appendFormParams：将post请求的参数添加到url最后
* 以上具体实现仅为参考，具体依赖Server端要求

 		@Override
	    public Response intercept(Chain chain) throws IOException {
	        Request request = chain.request();
	        String requestId = ParamsBuilder.getRequestId();
	        HttpUrl.Builder requestBuilder = request.url().newBuilder();
	        /*
	         添加token, channel, deviceID, requestId,
	         time:防止缓存
	         requestId:防止dos攻击
	          */
	        buildCommonParams(requestBuilder, request, requestId);
	        String params = requestBuilder.build().query();
	        // 将body参数拼接在后面
	        params = appendFormParams(request.body(), params);
	        // 打印完整的请求信息
	        Timber.tag(TAG).i("request params=%s", params);
	        String sign = mParamsBuilder.getSign(requestId, params);
	        // 签名
	        requestBuilder.addQueryParameter("_s_", sign);
	        Timber.tag(TAG).i("request sign=%s", sign);
	
	        Request.Builder builder = request.newBuilder();
	        builder.addHeader("Cookie", mParamsBuilder.getCookie());
	        Request newRequest = builder.method(request.method(), request.body()).url(requestBuilder.build()).build();
	        Response response = chain.proceed(newRequest);
	
	        // Do not check and report error anymore
	        checkResponse(response, request);
	
	        return response;
	    }
	    
#### 检查返回的response合法性
判断是否返回json格式的数据

	private void checkResponse(Response response, Request request) {
	        // Do not check audio binary bytes
	        if (response.request().url().toString().contains("playaudio")) {
	            return;
	        }
	        /*
	        以application开头的媒体格式类型：
	
	        application/xhtml+xml ：XHTML格式
	        application/xml     ： XML数据格式
	        application/atom+xml  ：Atom XML聚合格式
	        application/json    ： JSON数据格式
	        application/pdf       ：pdf格式
	        application/msword  ： Word文档格式
	        application/octet-stream ： 二进制流数据（如常见的文件下载）
	        application/x-www-form-urlencoded: 默认值，键值对的方式
	         */
	        MediaType contentType = response.body().contentType();
	        if (contentType == null || !"application".equals(contentType.type()) || !"json".equals(contentType.subtype())) {
	            StringBuilder builder = new StringBuilder("Invalid response found\nRequest:").append(request.url())
	                    .append("\nResponse.Request:").append(response.request().url().toString())
	                    .append("\nResponse.ContentType:").append(contentType.toString()).append("\nResponse.Body:\n");
	            try {
	                builder.append(response.body().string());
	            } catch (IOException e) {
	                Timber.tag("InvalidResponseException").w(e, "Failed to get the body");
	            }
	            Timber.wtf(new InvalidResponseException(builder.toString()));
	        }
	    }
	    
#### 自定义APIException
* 根据Server端定义的ErrorCode，ErrorMessage来判断异常情况。
* 可以在subscriber的onNext(),如果请求到错误码，立即抛出ApiException交给onError()处理。

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
        
### Dependencies

	dependencies {
	    apt 'com.bluelinelabs:logansquare-compiler:1.3.6'
	    compile 'com.jakewharton.timber:timber:4.4.0'
	    compile 'io.reactivex:rxjava:1.2.3'
	    compile 'com.squareup.retrofit2:adapter-rxjava:2.1.0'
	    compile 'com.squareup.okhttp3:logging-interceptor:3.0.1'
	    compile 'io.reactivex:rxandroid:1.2.1'
	    compile 'com.bluelinelabs:logansquare:1.3.6'
	    compile "com.github.aurae.retrofit2:converter-logansquare:1.4.1"
	    compile 'com.android.support:appcompat-v7:25.3.1'
	    compile 'com.facebook.stetho:stetho:1.4.1'
	    compile 'com.facebook.stetho:stetho-okhttp3:1.4.1'
	    compile 'com.squareup.retrofit2:retrofit:2.1.0'
	}
	
	dependencies {
        classpath 'com.android.tools.build:gradle:2.2.0'
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }














