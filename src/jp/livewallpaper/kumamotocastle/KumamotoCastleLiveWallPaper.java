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
import android.view.MotionEvent;

public class KumamotoCastleLiveWallPaper extends LiveWallPaper {
	private static final int[] images = {R.drawable.kumamon05,R.drawable.kumamon07,
		R.drawable.kumamon08,R.drawable.kumamon09,R.drawable.kumamon12,};
	private static final int[] colors ={Color.BLACK,Color.RED,Color.GREEN,Color.BLUE,
		Color.CYAN,Color.MAGENTA,Color.YELLOW,Color.WHITE};
	private Random randam = new Random();
	private boolean SingleTap = false;
	private float TapPointX = 0;
	private float TapPointY = 0;
	public static final String KEY_LASTUPDATE	= "LastUpdate";
	private int mLocateId = 0;

	@Override
	public void onCreate() {
		super.onCreate();
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
		if(SingleTap) {
			Bitmap kumamon =  BitmapFactory.decodeResource(getResources(), images[randam.nextInt(images.length)]);
			canvas.drawBitmap(kumamon, TapPointX, TapPointY, null);
			SingleTap = false;
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

	@Override
	public boolean SingleTapConfirmed(MotionEvent event) {
		SingleTap = true;
		TapPointX = event.getX();
		TapPointY = event.getY();
		return true;
	}

	private void OverLayer(Canvas canvas) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		int color = Integer.parseInt(sharedPreferences.getString("color", "0"));
		Paint paint = new Paint();
		paint.setColor(colors[color]);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		if(sharedPreferences.getBoolean("forecast", false)) {
			String today = sharedPreferences.getString(ForecastTask.KEY_TODAY, "");
			String tomorrow = sharedPreferences.getString(ForecastTask.KEY_TOMORROW, "");
			String day_after_tomorrow = sharedPreferences.getString(ForecastTask.KEY_DAY_AFTER_TOMORROW, "");
			paint.setTextSize(8 * Scaled);
			canvas.drawText(today, 3 * Scaled, 22 * Scaled, paint);
			canvas.drawText(tomorrow, 3 * Scaled, 32 * Scaled, paint);
			canvas.drawText(day_after_tomorrow, 3 * Scaled, 42 * Scaled, paint);
		}
		if(sharedPreferences.getBoolean("date", false)) {
			paint.setTextSize(6 * Scaled);
			Date date = Calendar.getInstance().getTime();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd(EEE)", Locale.JAPANESE);
			SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm", Locale.JAPANESE);
			canvas.drawText(sdf1.format(date), 115 * Scaled, 22 * Scaled, paint);
			paint.setTextSize(16 * Scaled);
			canvas.drawText(sdf2.format(date), 115 * Scaled, 37 * Scaled, paint);
			int battery = (int)((double)BatteryLevel / BatteryScale * 100.0 + 0.5);
			paint.setTextSize(6 * Scaled);
			Resources resource = getResources();
			canvas.drawText(resource.getString(R.string.battery)+ String.valueOf(battery) +"%", 117 * Scaled, 43 * Scaled, paint);
		}
	}
	
	private void getForecast() {
		try {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			if(sharedPreferences.getBoolean("forecast", false)) {
				Calendar nowDate = Calendar.getInstance();
				Calendar lastUpdate = Calendar.getInstance();
				lastUpdate.setTimeInMillis(sharedPreferences.getLong(KEY_LASTUPDATE, nowDate.getTimeInMillis()));
				int locateId = Integer.parseInt(sharedPreferences.getString("locate", "63"));
				if(mLocateId == locateId) {
					if(lastUpdate.get(Calendar.DAY_OF_YEAR) == nowDate.get(Calendar.DAY_OF_YEAR) &&
							lastUpdate.get(Calendar.HOUR_OF_DAY) >= 6 && nowDate.get(Calendar.HOUR_OF_DAY) < 12) return;
					if(lastUpdate.get(Calendar.DAY_OF_YEAR) == nowDate.get(Calendar.DAY_OF_YEAR) &&
							lastUpdate.get(Calendar.HOUR_OF_DAY) >= 12 && nowDate.get(Calendar.HOUR_OF_DAY) < 18) return;
					if(lastUpdate.get(Calendar.DAY_OF_YEAR) == nowDate.get(Calendar.DAY_OF_YEAR) &&
							lastUpdate.get(Calendar.HOUR_OF_DAY) >= 18) return;
					if(lastUpdate.get(Calendar.DAY_OF_YEAR) == nowDate.get(Calendar.DAY_OF_YEAR) &&
							lastUpdate.get(Calendar.HOUR_OF_DAY) < 6 && nowDate.get(Calendar.HOUR_OF_DAY) < 6) return;
				}
				mLocateId = locateId;
				ForecastTask task = new ForecastTask(this);
				task.execute(locateId);
			}
		} catch (Exception e) {
			//ExceptionLog.Log(TAG, e);
		}
	}
}
