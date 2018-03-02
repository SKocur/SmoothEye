package com.kocur.szymon.smootheye.POJOs;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Szymon Kocur on 2017-11-03.
 */

public class ThreadPOJO {
    @SerializedName("threadID")
    public String threadID;
    @SerializedName("threadName")
    public String threadName;
    @SerializedName("threadText")
    public String threadText;
    @SerializedName("threadActiveDate")
    public String threadActiveDate;
    @SerializedName("threadCreatedDate")
    public String threadCreatedDate;
    @SerializedName("threadCreatedBy")
    public String threadCreatedBy;
}
