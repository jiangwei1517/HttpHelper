package com.jiangwei.httplib.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by 蔡小木 on 2016/3/7 0007.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS)
public class ZhihuStory {
    @JsonField(name = "body")
    public String body;
    @JsonField(name = "title")
    public String title;
    @JsonField(name = "image")
    public String image;
    @JsonField(name = "share_url")
    public String mShareUrl;
    @JsonField(name = "css")
    public String[] css;
}
