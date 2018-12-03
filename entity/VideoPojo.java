package com.fourarc.videostatus.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 29-Sep-18.
 */

public class VideoPojo {

    @SerializedName("status_id")
    @Expose
    private String statusId;
    @SerializedName("status_title")
    @Expose
    private String statusTitle;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_profile_picture")
    @Expose
    private String userProfilePicture;
    @SerializedName("status_view")
    @Expose
    private String statusView;
    @SerializedName("status_review")
    @Expose
    private String statusReview;
    @SerializedName("status_image")
    @Expose
    private String statusImage;
    @SerializedName("status_data")
    @Expose
    private String statusData;
    @SerializedName("status_isfavourite")
    @Expose
    private String statusIsfavourite;
    @SerializedName("status_comments")
    @Expose
    private String statusComments;
    @SerializedName("status_love")
    @Expose
    private String statusLove;
    @SerializedName("status_angry")
    @Expose
    private String statusAngry;
    @SerializedName("status_sad")
    @Expose
    private String statusSad;
    @SerializedName("status_haha")
    @Expose
    private String statusHaha;
    @SerializedName("status_wow")
    @Expose
    private String statusWow;
    @SerializedName("status_like")
    @Expose
    private String statusLike;

    private boolean downloading = false;
    private int progress;
    private String path;
    private int viewType = 2;

    private String local;

    public VideoPojo(String statusId, String statusTitle, String userId, String userName, String userProfilePicture, String statusView, String statusReview, String statusImage, String statusData, String statusIsfavourite, String statusComments, String statusLove, String statusAngry, String statusSad, String statusHaha, String statusWow, String statusLike) {
        this.statusId = statusId;
        this.statusTitle = statusTitle;
        this.userId = userId;
        this.userName = userName;
        this.userProfilePicture = userProfilePicture;
        this.statusView = statusView;
        this.statusReview = statusReview;
        this.statusImage = statusImage;
        this.statusData = statusData;
        this.statusIsfavourite = statusIsfavourite;
        this.statusComments = statusComments;
        this.statusLove = statusLove;
        this.statusAngry = statusAngry;
        this.statusSad = statusSad;
        this.statusHaha = statusHaha;
        this.statusWow = statusWow;
        this.statusLike = statusLike;
    }

    public VideoPojo() {

    }


    public boolean isDownloading() {
        return downloading;
    }

    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public String getStatusView() {
        return statusView;
    }

    public void setStatusView(String statusView) {
        this.statusView = statusView;
    }

    public String getStatusReview() {
        return statusReview;
    }

    public void setStatusReview(String statusReview) {
        this.statusReview = statusReview;
    }

    public String getStatusImage() {
        return statusImage;
    }

    public void setStatusImage(String statusImage) {
        this.statusImage = statusImage;
    }

    public String getStatusData() {
        return statusData;
    }

    public void setStatusData(String statusData) {
        this.statusData = statusData;
    }

    public String getStatusIsfavourite() {
        return statusIsfavourite;
    }

    public void setStatusIsfavourite(String statusIsfavourite) {
        this.statusIsfavourite = statusIsfavourite;
    }

    public String getStatusComments() {
        return statusComments;
    }

    public void setStatusComments(String statusComments) {
        this.statusComments = statusComments;
    }

    public String getStatusLove() {
        return statusLove;
    }

    public void setStatusLove(String statusLove) {
        this.statusLove = statusLove;
    }

    public String getStatusAngry() {
        return statusAngry;
    }

    public void setStatusAngry(String statusAngry) {
        this.statusAngry = statusAngry;
    }

    public String getStatusSad() {
        return statusSad;
    }

    public void setStatusSad(String statusSad) {
        this.statusSad = statusSad;
    }

    public String getStatusHaha() {
        return statusHaha;
    }

    public void setStatusHaha(String statusHaha) {
        this.statusHaha = statusHaha;
    }

    public String getStatusWow() {
        return statusWow;
    }

    public void setStatusWow(String statusWow) {
        this.statusWow = statusWow;
    }

    public String getStatusLike() {
        return statusLike;
    }

    public void setStatusLike(String statusLike) {
        this.statusLike = statusLike;
    }
    public int getViewType() {
        return viewType;
    }

    public VideoPojo setViewType(int viewType) {
        this.viewType = viewType;
        return this;
    }
}
