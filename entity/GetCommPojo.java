package com.fourarc.videostatus.config;

/**
 * Created by admin on 09-Oct-18.
 */

public class GetCommPojo {

    String comment_id;
    String comment_text;
    String user_id;
    String user_name;
    String user_profile_picture;
    String comment_created_date;


    public GetCommPojo(String comment_id, String comment_text, String user_id, String user_name, String user_profile_picture, String comment_created_date) {
        this.comment_id = comment_id;
        this.comment_text = comment_text;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_profile_picture = user_profile_picture;
        this.comment_created_date = comment_created_date;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_profile_picture() {
        return user_profile_picture;
    }

    public void setUser_profile_picture(String user_profile_picture) {
        this.user_profile_picture = user_profile_picture;
    }

    public String getComment_created_date() {
        return comment_created_date;
    }

    public void setComment_created_date(String comment_created_date) {
        this.comment_created_date = comment_created_date;
    }
}
