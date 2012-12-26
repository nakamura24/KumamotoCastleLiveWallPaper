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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class KumamotoCastleLiveWall extends LiveWallPaper {
	private static final int[] images = {R.drawable.kumamon05,R.drawable.kumamon07,
		R.drawable.kumamon08,R.drawable.kumamon09,R.drawable.kumamon12,};
	private Random randam = new Random();
	private int preSingleTap = 0;

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
		if(preSingleTap != SingleTap) {
			Bitmap kumamon =  BitmapFactory.decodeResource(getResources(), images[randam.nextInt(images.length)]);
			canvas.drawBitmap(kumamon, TapPointX, TapPointY, null);
			preSingleTap = SingleTap;
			DrawDelayTime = 3000;	// millisecond
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
	}

	@Override
	public void DrawDelay() {
		DrawDelayTime = 0;
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
	}
}
