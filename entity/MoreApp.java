package com.fourarc.videostatus.entity;

/**
 * Created by Raj Paghadar on 25-Jan-18.
 */

public class MoreApp {
    String ad_title;
    String ad_image;
    String ad_url;
    String ad_desc;
    String ad_size;


    public MoreApp(String ad_title, String ad_image, String ad_url, String ad_desc, String ad_size) {
        this.ad_title = ad_title;
        this.ad_image = ad_image;
        this.ad_url = ad_url;
        this.ad_desc = ad_desc;
        this.ad_size = ad_size;
    }

    public void setAd_title(String ad_title) {this.ad_title = ad_title;}

    public void setAd_image(String ad_image) {this.ad_image = ad_image;}

    public void setAd_url(String ad_url) {this.ad_url = ad_url;}

    public String getAd_title() {return ad_title;}

    public String getAd_image() {return ad_image;}

    public String getAd_url() {return ad_url;}

    public String getAd_desc() {return ad_desc;}

    public String getAd_size() {return ad_size;}


}
