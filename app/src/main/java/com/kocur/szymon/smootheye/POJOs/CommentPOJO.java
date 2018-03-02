package com.kocur.szymon.smootheye.POJOs;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Szymon Kocur on 2017-11-03.
 */

public class CommentPOJO {
    @SerializedName("commentOwner")
    public String commentOwner;
    @SerializedName("commentText")
    public String commentText;
    @SerializedName("commentDate")
    public String commentDate;
}
