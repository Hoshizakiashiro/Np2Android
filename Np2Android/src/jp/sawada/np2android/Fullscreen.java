package jp.sawada.np2android;


import java.io.File;

import jp.sawada.np2android.SelectFileDialog.onSelectFileDialogListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;


public class Fullscreen extends PreferenceActivity implements onSelectFileDialogListener{
	/** Called when the activity is first created. */
	
	String NP2Path;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.screen);

		Preference preference = findPreference("keymapfile");
		preference .setOnPreferenceClickListener(new OnPreferenceClickListener() {

				public boolean onPreferenceClick(Preference preference) {
			        SelectFile(SDLActivity.KeyMap);
					return false;
				}
			});
	}
/*
	 public static boolean fullScreen (Context con){
		 return PreferenceManager.getDefaultSharedPreferences(con).getBoolean("fullscreen", false);
	 }

	 public static String width (Context con){
		 return PreferenceManager.getDefaultSharedPreferences(con).getString("width", "640");
	 }
	 
	 public static String height (Context con){
		 return PreferenceManager.getDefaultSharedPreferences(con).getString("height", "400");
	 }
*/	 
	protected void onPause() {
        //Log.v("SDL", "onPause()");

        super.onPause();
        
        Intent intent = new Intent();
		intent.setClassName("jp.sawada.np2android", "jp.sawada.np2android.SDLActivity");
		startActivity(intent);
	}
	
	public void onConfigurationChanged(Configuration newConfig){
	    super.onConfigurationChanged(newConfig);

	}

	protected	SelectFileDialog	_dlgSelectFile;
	
	private void SelectFile(int device) {
		
    	String path = System.getenv("EXTERNAL_STORAGE2");
    	if (path == null) {
        	path = System.getenv("EXTERNAL_STORAGE");
    	}
        NP2Path = path + "/np2";
        
		_dlgSelectFile = new SelectFileDialog(this, device);
		_dlgSelectFile.Show(NP2Path);
	}

	
	public void onFileSelected_by_SelectFileDialog(File file,int device) {
		
		if(file != null) {
		    SharedPreferences pref;
		    SharedPreferences.Editor editor;
	        pref = getSharedPreferences("jp.sawada.np2android_preferences", Activity.MODE_PRIVATE);
		    editor = pref.edit();
//			Log.d("SDL","device :" + device + " selected : " + file.getName());
			editor.putString("keymapfile",NP2Path + "/" + file.getName());			
			editor.commit();

		}else
//			Log.d("SDL","not selected");

		_dlgSelectFile = null;
	}

}
