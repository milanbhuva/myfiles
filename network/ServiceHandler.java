//service handler revison on 18-6-2016
// By Cipher


package com.fourarc.videostatus.network;

import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


public class ServiceHandler {

	static String response = null;
	public final static int GET = 1;
	public final static int POST = 2;

	//photo upload
	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpeg");

	private static final MediaType MEDIA_TYPE_VID = MediaType.parse("video");

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	String s = null;
	public String url = null;
	public String param = null;
	public String filepath = null;
	public String requestString = null;

	OkHttpClient client = new OkHttpClient();

	String doGetRequest(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public ServiceHandler() {

	}

	public String callToServer(String url, int method, JSONObject jsonobject) throws IOException {
		String json = jsonobject.toString();
		String arraystring = null;
		Response response=null;
		if (method == POST) {
			RequestBody body = RequestBody.create(JSON, String.valueOf(jsonobject));
			FormEncodingBuilder formbody = new FormEncodingBuilder();
			JSONArray array = jsonobject.names();

			for (int i=0; i<array.length(); i++)
			{
				try {
					formbody.add(array.getString(i),jsonobject.getString(array.getString(i)));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			Request request = new Request.Builder()
					.url(url)
					.post(formbody.build())
					.build();
			 response = client.newCall(request).execute();
		}
		else if (method == GET)
		{
			Request request = new Request.Builder()
					.url(url)
					.build();

			 response = client.newCall(request).execute();
		}
	return response.body().string();

	}

	public Response run() throws Exception {
		Log.e("img", "image" + filepath);

		// Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
		RequestBody requestBody = new MultipartBuilder()
				.type(MultipartBuilder.FORM)
				.addPart(
						Headers.of("Content-Disposition", "form-data; name=\"image\";filename=\"10566design.jpg\""),
						RequestBody.create(MEDIA_TYPE_PNG, new File(filepath)))
						//Headers.of("Content-Disposition", "form-data; name=\"status_image\""),
						//RequestBody.create(MEDIA_TYPE_PNG, new File(filepath)))
				.build();

		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();

		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		Log.e("", " res : " + response.priorResponse());
		Log.e("", " res : " + response.networkResponse().toString());
		return response;
	}

    public Response uploadImage(String tempURL, String tempFilePath, String param1/*,String tempRequestString*/) throws Exception {
        url      =  tempURL;
        filepath =  tempFilePath;
        param    =  param1;
        //requestString= tempRequestString;

        Response serviceresponse = run();
        return serviceresponse;
    }

	public Response uploadImg(String tempURL, String tempFilePath, String param1/*,String tempRequestString*/) throws Exception {
		url      =  tempURL;
		filepath =  tempFilePath;
		param    =  param1;
		//requestString= tempRequestString;

		Response serviceresponse = ruun();
		return serviceresponse;
	}

	public Response ruun() throws Exception {
		Log.e("img", "image" + filepath);

		// Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
		RequestBody requestBody = new MultipartBuilder()
				.type(MultipartBuilder.FORM)
				.addPart(
						Headers.of("Content-Disposition", "form-data; name=\"image\";filename=\"10566design.jpg\""),
						RequestBody.create(MEDIA_TYPE_PNG, new File(filepath)))
				//Headers.of("Content-Disposition", "form-data; name=\"status_image\""),
				//RequestBody.create(MEDIA_TYPE_PNG, new File(filepath)))
				.build();

		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();

		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		Log.e("", " res : " + response.priorResponse());
		Log.e("", " res : " + response.networkResponse().toString());
		return response;
	}

	public Response uploadIm(String tempURL, String tempFilePath, String param1/*,String tempRequestString*/) throws Exception {
		url      =  tempURL;
		filepath =  tempFilePath;
		param    =  param1;
		//requestString= tempRequestString;

		Response serviceresponse = rn();
		return serviceresponse;
	}

	public Response rn() throws Exception {
		Log.e("img", "image" + filepath);

		// Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
		RequestBody requestBody = new MultipartBuilder()
				.type(MultipartBuilder.FORM)
				.addPart(
						Headers.of("Content-Disposition", "form-data; name=\"media\";filename=\"10566design.jpg\""),
						RequestBody.create(MEDIA_TYPE_PNG, new File(filepath)))
				//Headers.of("Content-Disposition", "form-data; name=\"status_image\""),
				//RequestBody.create(MEDIA_TYPE_PNG, new File(filepath)))
				.build();

		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();

		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		Log.e("", " res : " + response.priorResponse());
		Log.e("", " res : " + response.networkResponse().toString());
		return response;
	}


	public Response Vid_run() throws Exception {
		Log.e("vid", "Video" + filepath);

		// Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
		RequestBody requestBody = new MultipartBuilder()
				.type(MultipartBuilder.FORM)
				.addPart(
						Headers.of("Content-Disposition", "form-data; name=\"video\";filename=\"10566design.mp4\""),
						RequestBody.create(MEDIA_TYPE_VID, new File(filepath)))
				//Headers.of("Content-Disposition", "form-data; name=\"status_image\""),
				//RequestBody.create(MEDIA_TYPE_PNG, new File(filepath)))
				.build();

		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.build();

		Response response = client.newCall(request).execute();
		if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
		Log.e("", " res : " + response.priorResponse());
		Log.e("", " res : " + response.networkResponse().toString());
		return response;
	}

	public Response uploadVideo(String tempURL, String tempFilePath, String param1/*,String tempRequestString*/) throws Exception {
		url      =  tempURL;
		filepath =  tempFilePath;
		param    =  param1;
		//requestString= tempRequestString;

		Response serviceresponse = Vid_run();
		return serviceresponse;
	}
}
