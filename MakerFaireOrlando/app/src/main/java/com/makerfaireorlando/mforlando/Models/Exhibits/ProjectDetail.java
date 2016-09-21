package com.makerfaireorlando.mforlando.Models.Exhibits;

import java.io.Serializable;
import java.util.List;

public class ProjectDetail implements Serializable {
    public String exhibit_category;
    public String hidden_exhibit_category;
    public String hidden_maker_category;
    public String project_name;
    public String description;
    public String web_site;
    public String promo_url;
    public String qrcode_url;
    public String project_short_summary;
    public String location;
    public String video_link;
    public List<Photo> additional_photos;
    public String organization;
    public String photo_link;
    public Maker maker;

    // Grid color properties
    public int color;
    public int darkColor;
    public int textColor;
    public boolean hasColor;
}
