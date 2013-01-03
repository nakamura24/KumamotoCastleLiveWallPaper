package jp.kumamotocastlelivewallpaper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class LiveWallPaperPreference extends PreferenceActivity 
		implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		
		// 設定が変更された時に呼び出されるListenerを登録
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		onSharedPreferenceChanged(sharedPreferences, null);
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		ListPreference locate = (ListPreference)getPreferenceScreen().findPreference("locate");
		locate.setSummary(locate.getEntry());
	}
}
