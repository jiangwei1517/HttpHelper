package com.jiangwei.httplib.api;

import com.jiangwei.httplib.model.BaseModel;
import com.jiangwei.httplib.model.ZhihuStory;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface APIService {

    // 当POST遇到Query时,uid会作为拼接放在后面
    @FormUrlEncoded
    @POST("/api/4/news/{id}")
    Observable<ZhihuStory> getZhihuStory1Post(@Path("id") String id, @Query("uid") int uid, @Field("qid") int qid);

    @GET("/api/4/news/{id}")
    Observable<ZhihuStory> getZhihuStoryGet(@Path("id") String id);
}
