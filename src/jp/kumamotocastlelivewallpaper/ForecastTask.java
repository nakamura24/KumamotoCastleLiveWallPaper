/*
 * Copyright (C) 2012 M.Nakamura
 *
 * This software is licensed under a Creative Commons
 * Attribution-NonCommercial-ShareAlike 2.1 Japan License.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 		http://creativecommons.org/licenses/by-nc-sa/2.1/jp/legalcode
 */

package jp.kumamotocastlelivewallpaper;

import java.io.InputStream;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;

public class ForecastTask extends AsyncTask<Integer, Integer, Long> {
	private static final String TAG = "ForecastTask";
	private ArrayList<String> titles = new ArrayList<String>();
	private ArrayList<String> weatherIcons = new ArrayList<String>();
	private Date pubDate = null;
	private int id = 63;
	private Context context;
	
	public ForecastTask(Context context){
		this.context = context;
	}

	@Override
	protected Long doInBackground(Integer... params) {
		if(params.length > 0) id = params[0];
		String uri = "http://rss.rssad.jp/rss/tenki/forecast/city_" +
				String.valueOf(id) + ".xml";
		Log.i(TAG, "doInBackground - " + uri);
		boolean item = false;
		HttpGet httpGetObj   = new HttpGet(uri);
		DefaultHttpClient httpClientObj = new DefaultHttpClient();
		HttpParams httpParamsObj = httpClientObj.getParams();
		HttpEntity httpEntityObj = null;
		InputStream inpurStreamObj = null;
	
		//接続のタイムアウト（単位：ms）
		HttpConnectionParams.setConnectionTimeout(httpParamsObj, 60000);
		//データ取得のタイムアウト（単位：ms）サーバ側のプログラム(phpとか)でsleepなどを使えばテストできる
		HttpConnectionParams.setSoTimeout(httpParamsObj, 180000);
	
		try {
			//httpリクエスト（時間切れなどサーバへのリクエスト時に問題があると例外が発生する）
			HttpResponse httpResponseObj = httpClientObj.execute(httpGetObj);
			//httpレスポンスの400番台以降はエラーだから
			if (httpResponseObj.getStatusLine().getStatusCode() < 400){
				//
				httpEntityObj = httpResponseObj.getEntity();
				//レスポンス本体を取得
				inpurStreamObj = httpEntityObj.getContent();
	
				//-----[パーサーの設定]
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(inpurStreamObj, "UTF-8");
	
				int eventType = parser.getEventType();
	
				titles.clear();
				weatherIcons.clear();
				String tag;
				while(eventType != XmlPullParser.END_DOCUMENT)
				{
					switch(eventType)
					{
						case XmlPullParser.START_TAG:
							tag = parser.getName();
					   		//Log.i(TAG, "tag=" + tag);
							if("item".equals(tag))
							{
								item = true;
							}
							if(item && "title".equals(tag))
							{
								String txt = parser.nextText();
								titles.add(txt);
								item = false;
								Log.v(TAG, "title=" + txt);
							}
							if("url".equals(tag))
							{
								String txt = parser.nextText();
								weatherIcons.add(txt);
								Log.v(TAG, "url=" + txt);
							}
							if("pubDate".equals(tag) && pubDate == null)
							{
								String txt = parser.nextText();
								pubDate = new Date(txt);
								Log.v(TAG, "pubDate=" + pubDate.toString());
							}
							break;
						case XmlPullParser.END_TAG:
							tag = parser.getName();
					   		//Log.i(TAG, "tag=" + tag);
							if("item".equals(tag))
							{
								item = false;
							}
							break;
					}
					eventType = parser.next();
				}
			}
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
			editor.putString(KumamotoCastleLiveWall.KEY_TODAY, titles.get(0));
			editor.putString(KumamotoCastleLiveWall.KEY_TOMORROW, titles.get(1));
			editor.putString(KumamotoCastleLiveWall.KEY_DAY_AFTER_TOMORROW, titles.get(2));

			// Commit the edits!
			editor.commit();		
		} catch (Exception e) {
			//ExceptionLog.Log(TAG, e);
		}
	}
}
