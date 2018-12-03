package com.fourarc.videostatus.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class Utility {

	public static Context appContext;
	private static String PREFERENCE;
	public static final String FAVORITES = "Favorite";

	public static void setStringSharedPreference(Context context, String name, String value) {
		appContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(name, value);
		editor.commit();
	}

	public static String getStringSharedPreferences(Context context, String name) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		return settings.getString(name, "");
	}



	public static void setIntegerSharedPreference(Context context, String name, int value) {
		appContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(name, value);
		editor.commit();
	}

	public static void setDrawableSharedPreference(Context context, String name, int value) {
		appContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(name, value);
		editor.commit();
	}

	public static int getIntegerSharedPreferences(Context context, String name) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		return settings.getInt(name, 0);
	}

	public static void setSharedPreferenceBoolean(Context context, String name, boolean value) {
		appContext = context;
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(name, value);
		editor.commit();
	}

	public static boolean getSharedPreferencesBoolean(Context context, String name) {
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
		return settings.getBoolean(name, false);
	}

	static Locale myLocale;
	public static void setLocale(String lang, Context context) {
		myLocale = new Locale(lang);
		Resources res = context.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
	}

	public static void storeFavorites(Context context, ArrayList favorites) {
// used for store arrayList in json format
		SharedPreferences settings;
		SharedPreferences.Editor editor;
		settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		editor = settings.edit();
		Gson gson = new Gson();
		String jsonFavorites = gson.toJson(favorites);
		editor.putString(FAVORITES, jsonFavorites);
		editor.commit();
	}
	public static ArrayList loadFavorites(Context context) {
// used for retrieving arraylist from json formatted string
		SharedPreferences settings;
		ArrayList<String> favorites;
		settings = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
		if (settings.contains(FAVORITES)) {
			String jsonFavorites = settings.getString(FAVORITES, null);
			Gson gson = new Gson();
			ArrayList<String> favoriteItems = gson.fromJson(jsonFavorites,new TypeToken<ArrayList<String>>(){}.getType());
			//favorites = Arrays.asList(favoriteItems);
			favorites = new ArrayList(favoriteItems);
			Collections.reverse(favorites);
		} else
			return null;
		return (ArrayList) favorites;
	}

	public static void addFavorite(Context context, String s) {
		ArrayList<String> favorites = loadFavorites(context);
		if (favorites == null)
			favorites = new ArrayList();
		favorites.add(s);
		storeFavorites(context, favorites);
	}
}// final class ends here

