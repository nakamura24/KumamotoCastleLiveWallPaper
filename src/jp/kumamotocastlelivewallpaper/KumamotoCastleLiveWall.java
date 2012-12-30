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

import java.text.SimpleDateFormat;
import java.util.*;

import jp.template.LiveWallPaper;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

public class KumamotoCastleLiveWall extends LiveWallPaper {
	private static final int[] images = {R.drawable.kumamon05,R.drawable.kumamon07,
		R.drawable.kumamon08,R.drawable.kumamon09,R.drawable.kumamon12,};
	private Random randam = new Random();
	private int preSingleTap = 0;
	public static final String KEY_LASTUPDATE	= "LastUpdate";
	public static final String KEY_TODAY	= "today";
	public static final String KEY_TOMORROW	= "tomorrow";
	public static final String KEY_DAY_AFTER_TOMORROW	= "day_after_tomorrow";
	private boolean changeId = false;
	private int mLocateId = 0;

	// Ý’è‚ª•ÏX‚³‚ê‚½Žž‚ÉŒÄ‚Ño‚³‚ê‚éListener
	private final SharedPreferences.OnSharedPreferenceChangeListener mListerner = 
			new SharedPreferences.OnSharedPreferenceChangeListener()
	{
	    @Override
	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	    	int locateId = Integer.parseInt(sharedPreferences.getString("locate", "63"));
	    	if(mLocateId != locateId) {
		    	changeId = true;
		    	mLocateId = locateId;
	    	}
	    }
	};

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Ý’è‚ª•ÏX‚³‚ê‚½Žž‚ÉŒÄ‚Ño‚³‚ê‚éListener‚ð“o˜^
        SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setting.registerOnSharedPreferenceChangeListener(mListerner);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new LiveEngine();
	}

	@Override
	public void DrawCanvas(Canvas canvas) {
		// draw something
		super.DrawCanvas(canvas);
		OverLayer(canvas);
		if(preSingleTap != SingleTap) {
			Bitmap kumamon =  BitmapFactory.decodeResource(getResources(), images[randam.nextInt(images.length)]);
			canvas.drawBitmap(kumamon, TapPointX, TapPointY, null);
			preSingleTap = SingleTap;
			DelayMillis = 3000;	// millisecond
		}
	}

	@Override
	public void ChangeImage() {
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if(hour >= 6 && hour <= 18) {
			switch(month) {
			case Calendar.JANUARY:
			case Calendar.FEBRUARY:
				Image = BitmapFactory.decodeResource(getResources(), R.drawable.castle4);
				break;
			case Calendar.MARCH:
				Image = BitmapFactory.decodeResource(getResources(), R.drawable.castle0);
				break;
			case Calendar.APRIL:
			case Calendar.MAY:
				Image = BitmapFactory.decodeResource(getResources(), R.drawable.castle1);
				break;
			case Calendar.JUNE:
			case Calendar.JULY:
			case Calendar.AUGUST:
				Image = BitmapFactory.decodeResource(getResources(), R.drawable.castle2);
				break;
			case Calendar.SEPTEMBER:
			case Calendar.OCTOBER:
			case Calendar.NOVEMBER:
				Image = BitmapFactory.decodeResource(getResources(), R.drawable.castle3);
				break;
			case Calendar.DECEMBER:
				Image = BitmapFactory.decodeResource(getResources(), R.drawable.castle4);
				break;
			}
		} else {
			Image = BitmapFactory.decodeResource(getResources(), R.drawable.castle5);
		}
		getForecast();
	}

	@Override
	public void DrawDelay() {
		DelayMillis = 0;
	}

	private void OverLayer(Canvas canvas) {
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd(EEE)", Locale.JAPANESE);
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.JAPANESE);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(18);
		canvas.drawText(sdf1.format(date), 20, 70, paint);
		paint.setTextSize(50);
		canvas.drawText(sdf2.format(date), 20, 115, paint);
		int battery = (int)((double)BatteryLevel / BatteryScale * 100.0 + 0.5);
		paint.setTextSize(18);
		Resources resource = getResources();
		canvas.drawText(resource.getString(R.string.battery)+ String.valueOf(battery) +"%", 25, 140, paint);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String today = sharedPreferences.getString(KEY_TODAY, "");
        String tomorrow = sharedPreferences.getString(KEY_TOMORROW, "");
        String day_after_tomorrow = sharedPreferences.getString(KEY_DAY_AFTER_TOMORROW, "");
		paint.setTextSize(20);
		canvas.drawText(today, 240, 70, paint);
		canvas.drawText(tomorrow, 240, 100, paint);
		canvas.drawText(day_after_tomorrow, 240, 130, paint);
	}
	
	private void getForecast() {
		try {
			Calendar nowDate = Calendar.getInstance();
	        Calendar lastUpdate = Calendar.getInstance();
	        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	        lastUpdate.setTimeInMillis(sharedPreferences.getLong(KEY_LASTUPDATE, nowDate.getTimeInMillis()));
			if(!changeId) {
				if(lastUpdate.get(Calendar.DAY_OF_YEAR) == nowDate.get(Calendar.DAY_OF_YEAR) &&
						lastUpdate.get(Calendar.HOUR_OF_DAY) >= 6 && nowDate.get(Calendar.HOUR_OF_DAY) < 12) return;
				if(lastUpdate.get(Calendar.DAY_OF_YEAR) == nowDate.get(Calendar.DAY_OF_YEAR) &&
						lastUpdate.get(Calendar.HOUR_OF_DAY) >= 12 && nowDate.get(Calendar.HOUR_OF_DAY) < 18) return;
				if(lastUpdate.get(Calendar.DAY_OF_YEAR) == nowDate.get(Calendar.DAY_OF_YEAR) &&
						lastUpdate.get(Calendar.HOUR_OF_DAY) >= 18) return;
				if(lastUpdate.get(Calendar.DAY_OF_YEAR) == nowDate.get(Calendar.DAY_OF_YEAR) &&
						lastUpdate.get(Calendar.HOUR_OF_DAY) < 6 && nowDate.get(Calendar.HOUR_OF_DAY) < 6) return;
			}
			changeId = false;
	    	int locateId = Integer.parseInt(sharedPreferences.getString("locate", "63"));
			ForecastTask task = new ForecastTask(this);
			task.execute(locateId);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putLong(KumamotoCastleLiveWall.KEY_LASTUPDATE, nowDate.getTimeInMillis());
			editor.commit();		
		} catch (Exception e) {
			//ExceptionLog.Log(TAG, e);
		}
	}
}
