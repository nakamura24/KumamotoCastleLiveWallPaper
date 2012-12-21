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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.GestureDetector.SimpleOnGestureListener;

public class KumamotoCastleLiveWall extends WallpaperService {
	private static String Tag ="KumamonLiveWall3";
	private Context context;
	
	@Override
	public Engine onCreateEngine() {
		context = this;
		// 描画用の自作Engineクラスを返す
		return new LiveEngine();
	}
	/** 描画を行うEngineクラス **/
	public class LiveEngine extends Engine {
		// ここに描画用の処理を記述していく
		/** イメージ **/
		private Bitmap image;
		/** 表示状態フラグ **/
		private boolean visible;
		private GestureDetector Detector;
		//private int imageSelect = 2;
		//private int touchSelect = 0;
		private int battery_level = 0;
		private int battery_scale = 100;
		
		public LiveEngine() {
		}

		/** Engine生成時に呼び出される **/
		@Override
		public void onCreate(SurfaceHolder surfaceHolder){
			super.onCreate(surfaceHolder);
			Log.i(Tag, "onCreate");
			// タッチイベントを有効
			setTouchEventsEnabled(true);
			// GestureDetecotorクラスのインスタンス生成
			Detector = new GestureDetector(context,onGestureListener);
			// ACTION_TIME_TICK Receiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(mBroadcastReceiver, filter);
		}
		/** Engine破棄時に呼び出される **/
		@Override
		public void onDestroy(){
			super.onDestroy();
			Log.i(Tag, "onDestroy");
            unregisterReceiver(mBroadcastReceiver);
			if (image != null) {
				// Bitmapデータの解放
				image.recycle();
				image = null;
			}
		}
	    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
	        @Override
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        if(Intent.ACTION_TIME_TICK.equals(action)){
					if(visible){
						drawFrame();
					}
		        }
				if(action.equals(Intent.ACTION_BATTERY_CHANGED)){
					battery_level = intent.getIntExtra("level", 0);
					battery_scale = intent.getIntExtra("scale", 0);
					Log.i(Tag, "onReceive=" + String.valueOf(battery_scale));
					drawFrame();
				}
		    }
	    };
		/** 表示状態変更時に呼び出される **/
		@Override
		public void onVisibilityChanged(boolean visible){
			Log.i(Tag, "onVisibilityChanged=" + String.valueOf(visible));
			this.visible = visible;
			if(this.visible){
				drawFrame();
			}
		}
		/** サーフェイス生成時に呼び出される **/
		@Override
		public void onSurfaceCreated(SurfaceHolder surfaceHolder){
			super.onSurfaceCreated(surfaceHolder);
			Log.i(Tag, "onSurfaceCreated");
		}
		/** サーフェイス変更時に呼び出される **/
		@Override
		public void onSurfaceChanged(SurfaceHolder holder,int format, int width , int height){
			super.onSurfaceChanged(holder, format, width, height);
			Log.i(Tag, "onSurfaceChanged");
			drawFrame();
		}
		/** サーフェイス破棄時に呼び出される **/
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder){
			super.onSurfaceDestroyed(holder);
			Log.i(Tag, "onSurfaceDestroyed");
			visible = false;
			if (image != null) {
				// Bitmapデータの解放
				image.recycle();
				image = null;
			}
		}
		/** オフセット変更時に呼び出される **/
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels){
			super.onOffsetsChanged(xOffset, yOffset, xStep, yStep, xPixels, yPixels);
			Log.i(Tag, "onOffsetsChanged");
			/*int select = (int)Math.round(xOffset / xStep);
			if(imageSelect != select)
			{
				Log.d(Tag, "xOffset=" + xOffset + " xStep=" + xStep + " xPixels=" + xPixels);
				imageSelect = select;
				drawFrame();
			}*/
		}
		/** キャンバスで描画を行う **/
		private void drawFrame(){
			final SurfaceHolder holder = getSurfaceHolder();
			
			Canvas c = null;
			try{
				// キャンバスをロック
				c = holder.lockCanvas();
				if(c != null){
					c.drawColor(Color.BLACK);
					_changeImage();
					c.drawBitmap(image, 0, 0, null);
					OverLayer(c);
					Log.d(Tag, "drawBitmap");
				}
			} catch (Exception e) {
				Log.e(Tag, e.getMessage());
			}finally{
				// Canvas アンロック
				if(c != null){
					holder.unlockCanvasAndPost(c);
				}
			}
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
			int battery = (int)((double)battery_level / battery_scale * 100.0 + 0.5);
			paint.setTextSize(18);
			Resources resource = getResources();
			canvas.drawText(resource.getString(R.string.battery)+ String.valueOf(battery) +"%", 25, 140, paint);
		}
		private void _changeImage() {
			Calendar calendar = Calendar.getInstance();
			int month = calendar.get(Calendar.MONTH);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			if(hour >= 6 && hour <= 18) {
				switch(month) {
				case Calendar.JANUARY:
				case Calendar.FEBRUARY:
					image = BitmapFactory.decodeResource(getResources(), R.drawable.castle4);
					break;
				case Calendar.MARCH:
					image = BitmapFactory.decodeResource(getResources(), R.drawable.castle0);
					break;
				case Calendar.APRIL:
				case Calendar.MAY:
					image = BitmapFactory.decodeResource(getResources(), R.drawable.castle1);
					break;
				case Calendar.JUNE:
				case Calendar.JULY:
				case Calendar.AUGUST:
					image = BitmapFactory.decodeResource(getResources(), R.drawable.castle2);
					break;
				case Calendar.SEPTEMBER:
				case Calendar.OCTOBER:
				case Calendar.NOVEMBER:
					image = BitmapFactory.decodeResource(getResources(), R.drawable.castle3);
					break;
				case Calendar.DECEMBER:
					image = BitmapFactory.decodeResource(getResources(), R.drawable.castle4);
					break;
				}
			} else {
				image = BitmapFactory.decodeResource(getResources(), R.drawable.castle5);
			}
			//Log.i("tag1", "imageSelect=" + imageSelect);
		}
		//Randomクラスのインスタンス化
		private Random rnd = new Random();
		/** タッチイベント **/
		@Override
		public void onTouchEvent(MotionEvent event){
			super.onTouchEvent(event);
			// タッチイベントをGestureDetector#onTouchEventメソッドに
			Detector.onTouchEvent(event);
		}
		// 複雑なタッチイベントを取得
		private final SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {
			@Override
			public boolean onDoubleTap(MotionEvent event) {
				Log.d(Tag, "onDoubleTap - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				return super.onDoubleTap(event);
			}
			@Override
			public boolean onDoubleTapEvent(MotionEvent event) {
				Log.d(Tag, "onDoubleTapEvent - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				return super.onDoubleTapEvent(event);
			}
			@Override
			public boolean onDown(MotionEvent event) {
				Log.d(Tag, "onDown - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				return super.onDown(event);
			}
			@Override
			public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
				Log.d(Tag, "onFling - " + String.valueOf(velocityX) + " "+ String.valueOf(velocityY));
				return super.onFling(event1, event2, velocityX, velocityY);
			}
			@Override
			public void onLongPress(MotionEvent event) {
				Log.d(Tag, "onLongPress - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				super.onLongPress(event);
			}
			@Override
			public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
				Log.d(Tag, "onScroll - " + String.valueOf(distanceX) + " "+ String.valueOf(distanceY));
				return super.onScroll(event1, event2, distanceX, distanceY);
			}
			@Override
			public void onShowPress(MotionEvent event) {
				Log.d(Tag, "onShowPress - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				super.onShowPress(event);
			}
			@Override
			public boolean onSingleTapConfirmed(MotionEvent event) {
				Log.d(Tag, "onSingleTapConfirmed - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				return super.onSingleTapConfirmed(event);
			}
			@Override
			public boolean onSingleTapUp(MotionEvent event) {
				Log.d(Tag, "onSingleTapUp - " + String.valueOf(event.getX()) + " "+ String.valueOf(event.getY()));
				Handler handler = new Handler();
				Runnable drawRunnable = new Runnable(){
					public void run(){
						// 描画メソッドを呼び出す
						drawFrame();
					}
				};
				drawFrame();
				int[] Images = {R.drawable.kumamon05,R.drawable.kumamon07,
						R.drawable.kumamon08,R.drawable.kumamon09,R.drawable.kumamon12,};
				final SurfaceHolder holder = getSurfaceHolder();
				Canvas c = null;
				try{
					// キャンバスをロック
					c = holder.lockCanvas();
					if(c != null){
						Log.d(Tag, "drawBitmap");
						Bitmap kumamon =  BitmapFactory.decodeResource(getResources(), Images[rnd.nextInt(Images.length)]);
						c.drawBitmap(kumamon, event.getX(), event.getY(), null);
					}
				} catch (Exception e) {
					Log.e(Tag, e.getMessage());
				}finally{
					// Canvas アンロック
					if(c != null){
						holder.unlockCanvasAndPost(c);
					}
				}
        		handler.postDelayed(drawRunnable, 3000);
				return super.onSingleTapUp(event);
			}
		};
	}
}
