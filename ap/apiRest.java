package com.fourarc.videostatus.api;

import com.fourarc.videostatus.config.Config;
import com.fourarc.videostatus.config.VideoPojo;
import com.fourarc.videostatus.entity.ApiResponse;
import com.fourarc.videostatus.entity.Category;
import com.fourarc.videostatus.entity.Comment;
import com.fourarc.videostatus.entity.Language;
import com.fourarc.videostatus.entity.User;
import com.fourarc.videostatus.entity.Video;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by hsn on 28/09/2017.
 */

public interface apiRest {

    @GET("version/check/{code}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> check(@Path("code") Integer code);

    @FormUrlEncoded
    @POST("support/add/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> addSupport(@Field("email") String email, @Field("name") String name, @Field("message") String message);


    @GET("category/video/all/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<List<Category>> categoriesImageAll();



    @GET("language/all/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<List<Language>> languageAll();



   /* @GET("get_video?"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Video>> imageAll(@Path("page") Integer page, @Path("order") String order, @Path("language") String language);*/

    @GET("get_video.php")
    Call<List<VideoPojo>> getvideo(@Query("c_key") String c_key, @Query("c_secret") String c_secret);

    @GET("video/by/category/{page}/{order}/{language}/{category}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<List<Video>> imageByCategory(@Path("page") Integer page, @Path("order") String order, @Path("language") String language, @Path("category") Integer category);

    @GET("video/by/random/{language}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<List<Video>> ImageByRandom(@Path("language") String language);


    @FormUrlEncoded
    @POST("video/add/copied/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageAddDownload(@Field("id") Integer id);


    @FormUrlEncoded
    @POST("video/add/like/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageAddLike(@Field("id") Integer id);


    @FormUrlEncoded
    @POST("video/add/love/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageAddLove(@Field("id") Integer id);


    @FormUrlEncoded
    @POST("video/add/angry/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageAddAngry(@Field("id") Integer id);


    @FormUrlEncoded
    @POST("video/add/haha/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageAddHaha(@Field("id") Integer id);


    @FormUrlEncoded
    @POST("video/add/sad/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageAddSad(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("video/add/woow/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageAddWoow(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("video/delete/like/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageDeleteLike(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("video/delete/love/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageDeleteLove(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("video/delete/angry/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageDeleteAngry(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("video/delete/haha/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageDeleteHaha(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("video/delete/sad/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageDeleteSad(@Field("id") Integer id);

    @FormUrlEncoded
    @POST("video/delete/woow/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<Integer> imageDeleteWoow(@Field("id") Integer id);


    @Multipart
    @POST("video/upload/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> uploadVideo(@Part MultipartBody.Part file, @Part MultipartBody.Part file_thum, @Part("id") String id, @Part("key") String key, @Part("title") String title, @Part("language") String language, @Part("categories") String categories);


    @FormUrlEncoded
    @POST("user/token/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> editToken(@Field("user") Integer user, @Field("key") String key, @Field("token_f") String token_f);

    @FormUrlEncoded
    @POST("user/register/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> register(@Field("name") String name, @Field("username") String username, @Field("password") String password, @Field("type") String type, @Field("image") String image);

    @GET("device/{tkn}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> addDevice(@Path("tkn") String tkn);

    @GET("video/by/query/{order}/{language}/{page}/{query}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<List<Video>> searchImage(@Path("order") String order, @Path("language") String language, @Path("page") Integer page, @Path("query") String query);

    @GET("video/by/user/{page}/{order}/{language}/{user}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<List<Video>> userImage(@Path("order") String order, @Path("language") String language, @Path("user") Integer user, @Path("page") Integer page);

    @GET("video/by/follow/{page}/{language}/{user}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<List<Video>> followImage(@Path("page") Integer page, @Path("language") String language, @Path("user") Integer user);

    @GET("video/by/me/{page}/{user}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<List<Video>> meImage(@Path("page") Integer page, @Path("user") Integer user);

    @GET("comment/list/{id}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<List<Comment>> getComments(@Path("id") Integer id);

    @FormUrlEncoded
    @POST("addcomment.php?")
    Call<ApiResponse> addCommentImage(@Query("c_key")String c_key,@Query("c_secret")String c_secret,@Query("user_id")String user_id,@Query("status_id")String status_id,@Query("comment_text")String comment_text);

    @GET("user/follow/{user}/{follower}/{key}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> follow(@Path("user") Integer user, @Path("follower") Integer follower, @Path("key") String key);


    @GET("user/followers/{user}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<List<User>> getFollowers(@Path("user") Integer user);

    @GET("user/followings/{user}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<List<User>> getFollowing(@Path("user") Integer user);


    @GET("user/get/{user}/{me}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> getUser(@Path("user") Integer user, @Path("me") Integer me);

    @FormUrlEncoded
    @POST("user/edit/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> editUser(@Field("user") Integer user, @Field("key") String key, @Field("name") String name, @Field("email") String email, @Field("facebook") String facebook, @Field("twitter") String twitter, @Field("instagram") String instagram);


    @GET("install/add/{id}/" + Config.TOKEN_APP + "/" + Config.ITEM_PURCHASE_CODE + "/")
    Call<ApiResponse> addInstall(@Path("id") String id);


}
