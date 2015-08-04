package com.makerfaireorlando.makerfaireorlando.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProjectsList {

    @SerializedName("attend_link")
    public String attendLink;

    @SerializedName("sponsor_link")
    public String sponsorLink;

    @SerializedName("accepteds")
    public List<ProjectDetail> accepteds;

    @SerializedName("about_url")
    public String aboutUrl;

    @SerializedName("title")
    public String title;



}
