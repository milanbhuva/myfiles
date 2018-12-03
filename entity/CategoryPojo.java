package com.fourarc.videostatus.config;

/**
 * Created by admin on 29-Sep-18.
 */

public class CategoryPojo {

    String tag_id;
    String tag_name;
    String tag_image;

    public CategoryPojo(String tag_id, String tag_name, String tag_image) {
        this.tag_id = tag_id;
        this.tag_name = tag_name;
        this.tag_image = tag_image;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getTag_image() {
        return tag_image;
    }

    public void setTag_image(String tag_image) {
        this.tag_image = tag_image;
    }
}
