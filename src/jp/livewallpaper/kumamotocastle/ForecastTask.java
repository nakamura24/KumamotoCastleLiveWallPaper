/*
 * Copyright (C) 2012 M.Nakamura
 *
 * This software is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 2.1 Japan License.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		http://creativecommons.org/licenses/by-nc-sa/2.1/jp/legalcode
 */

package jp.livewallpaper.kumamotocastle;

import java.util.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class ForecastTask extends AsyncTask<Integer, Integer, Long> {
	private static final String TAG = "ForecastTask";
	private ArrayList<Message> mMessages;
	private int id = 63;
	private Context context;
	public static final String KEY_LASTUPDATE	= "LastUpdate";
	public static final String KEY_TODAY	= "today";
	public static final String KEY_TOMORROW	= "tomorrow";
	public static final String KEY_DAY_AFTER_TOMORROW	= "day_after_tomorrow";
	
	public ForecastTask(Context context){
		this.context = context.getApplicationContext();
	}

	@Override
	protected Long doInBackground(Integer... params) {
		if(params.length > 0) id = params[0];
		String uri = "http://rss.rssad.jp/rss/tenki/forecast/city_" +
				String.valueOf(id) + ".xml";
		Log.i(TAG, "doInBackground - " + uri);
		try {
			AndroidSaxFeedParser paser = new AndroidSaxFeedParser(uri);
			mMessages = paser.parse();
		} catch (Exception e) {
			//ExceptionLog.Log(TAG, e);
			return 0L;
		}
		return 0L;
	}

	@Override
	protected void onPostExecute(Long result) {
		try {
			Log.i(TAG, "onPostExecute");
	        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(KEY_TODAY, mMessages.get(0).getTitle());
			editor.putString(KEY_TOMORROW, mMessages.get(1).getTitle());
			editor.putString(KEY_DAY_AFTER_TOMORROW, mMessages.get(2).getTitle());
			editor.putLong(KEY_LASTUPDATE, Calendar.getInstance().getTimeInMillis());

			// Commit the edits!
			editor.commit();		
		} catch (Exception e) {
			//ExceptionLog.Log(TAG, e);
		}
	}
}
