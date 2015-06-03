package jp.sawada.np2android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Bundle;
import android.widget.LinearLayout;

public class KeySettings extends Activity implements OnKeyboardActionListener {
	
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    

    
	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.keysetting);

	    pref = getSharedPreferences("jp.sawada.np2android_preferences", Activity.MODE_PRIVATE);

	    // Keyboard
	    Keyboard keyboard = new Keyboard(this ,R.xml.keyboard);
	    KeyboardView keyboardView = new KeyboardView(this ,null);
	    keyboardView.setKeyboard(keyboard);
	    keyboardView.setOnKeyboardActionListener(this);
	    
        // Layout
        LinearLayout keyboardLayout = (LinearLayout)findViewById(R.id.keySettingLayer);
        keyboardLayout.addView(keyboardView);
	}



	protected void onPause() {
		
        //Log.v("SDL", "onPause()");
        super.onPause();
        
        Intent intent = new Intent();
		intent.setClassName("jp.sawada.np2android", "jp.sawada.np2android.SDLActivity");
		startActivity(intent);
	}

	public void onKey(int primaryCode, int[] keyCodes) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	public void onPress(int primaryCode) {}

	public void onRelease(int primaryCode) {}

	public void onText(CharSequence text) {}

	public void swipeDown() {}

	public void swipeLeft() {}

	public void swipeRight() {}

	public void swipeUp() {}
	
/*    //KeyEvent
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

    	int scanCode = event.getScanCode();
    	int keyCode = event.getKeyCode();
    	
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			openOptionsMenu();
			return true;
		}
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            Log.v("SDL", "Scan Code: " + scanCode);

            return true;
        }
        else if (event.getAction() == KeyEvent.ACTION_UP) {

            return true;
        }
        
        return true;
        //return super.dispatchKeyEvent(event);
    }*/    

}
