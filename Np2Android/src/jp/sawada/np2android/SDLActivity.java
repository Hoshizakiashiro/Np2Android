package jp.sawada.np2android;

import java.io.File;
import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.view.*;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.os.*;
import android.util.Log;
import android.media.*;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import jp.sawada.np2android.SelectFileDialog.onSelectFileDialogListener;


/**
    SDL Activity
*/
public class SDLActivity extends Activity implements OnKeyboardActionListener, onSelectFileDialogListener{

    // Main components
    private static SDLActivity mSingleton;
    private static SDLSurface mSurface;

    // Audio
    private static Thread mAudioThread;
    private static AudioTrack mAudioTrack;
    private static AudioManager aManager;
    private int vol;

    // Load the .so
    static {
        System.loadLibrary("SDL");
        System.loadLibrary("main");
    } 
    //Device
    static final int NP2PATH = 0;
    static final int HDD1 = 1;
    static final int HDD2 = 2;
    static final int FDD1 = 3;
    static final int FDD2 = 4;
    static final int KeyMap = 5;
    
    public static String NP2Dir;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ScanCode scancode;
    
    // Setup
    @Override
	protected void onCreate(Bundle savedInstanceState) {

//        Log.v("SDL", "onCreate()");
        super.onCreate(savedInstanceState);

        // So we can call stuff from static callbacks
        mSingleton = this;

        // Set up the surface
        mSurface = new SDLSurface(getApplication());

        setContentView(R.layout.main);
    	

        // Layout
        pref = getSharedPreferences("jp.sawada.np2android_preferences", Activity.MODE_PRIVATE);
		editor = pref.edit();
		
		int x = Integer.parseInt(pref.getString("width", "640"));
		int y = Integer.parseInt(pref.getString("height", "400"));
		int z = Integer.parseInt(pref.getString("position", "3"));
		int display_h = Integer.parseInt(pref.getString("mode", "400"));

		int key_y = Integer.parseInt(pref.getString("key_height", "400"));
						
        	Log.v("SDL","X:" + x + " Y:" + y );

        LinearLayout mainViewLayout = (LinearLayout)findViewById(R.id.mainView);      
        if (pref.getBoolean("fullscreen", false)){
        	mainViewLayout.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        } else {
        	mainViewLayout.setLayoutParams(new FrameLayout.LayoutParams(x, y, z ));
        }
   
        mainViewLayout.addView(mSurface, new LinearLayout.LayoutParams ( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        // Keyboard
        LinearLayout keyboardLayout = (LinearLayout)findViewById(R.id.keyboardLayer);
        if (pref.getBoolean("button", false)){
            keyboardLayout.setVisibility(View.VISIBLE);
        } else {
            keyboardLayout.setVisibility(View.GONE);        	
        }
        
        LinearLayout dummyLayout = (LinearLayout)findViewById(R.id.dummy);  
    	int position = Integer.parseInt(pref.getString("keyposition", "5"));
    	dummyLayout.setGravity(position);
    	dummyLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, key_y ));
        
        if (pref.getBoolean("underkey", true)){        
        	LinearLayout underkeyboardLayout = (LinearLayout)findViewById(R.id.underKeyboard);
        	LinearLayout underkeyboardLayout1 = (LinearLayout)findViewById(R.id.underKeyboard1);

        	Keyboard underkeyboard = new Keyboard(this ,R.xml.keyboard);
        	KeyboardView underkeyboardView = new KeyboardView(this ,null);
        	underkeyboardView.setKeyboard(underkeyboard);
        	underkeyboardView.setOnKeyboardActionListener(this);
        	underkeyboardLayout1.addView(underkeyboardView );
        	underkeyboardLayout.setVisibility(View.VISIBLE);
        }
        if (pref.getBoolean("sidekey", true)){

        	LinearLayout sidekeyboardLayout = (LinearLayout)findViewById(R.id.sideKeyboard);

        	Keyboard sidekeyboard = new Keyboard(this ,R.xml.sidekeyboard);
        	KeyboardView sidekeyboardView = new KeyboardView(this ,null);
        	sidekeyboardView.setKeyboard(sidekeyboard);
        	sidekeyboardView.setOnKeyboardActionListener(this);
        	sidekeyboardLayout.addView(sidekeyboardView);
        }

        
        SurfaceHolder holder = mSurface.getHolder();
        holder.setFixedSize(640,display_h);
//        holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
        
        //key setting
        String keymapfile = pref.getString("keymapfile", null );
    	scancode =new ScanCode(keymapfile);

    	String path = System.getenv("EXTERNAL_STORAGE2");
    	if (path == null) {
        	path = System.getenv("EXTERNAL_STORAGE");
    	}
        NP2Dir = path + "/np2";
        
        aManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        vol = aManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }
	@Override
	public void onPause() {
		
		if(_dlgSelectFile != null)
			_dlgSelectFile.onPause();
		
		super.onPause();
//        Log.v("SDL", "onPause()");
	}

	public void onConfigurationChanged(Configuration newConfig){
	    super.onConfigurationChanged(newConfig);
//	    Log.d("test","onConfigurationChanged");
	    
	}
    // C functions we call
    public static native void nativeInit();
    public static native void nativeQuit();
    public static native void onNativeResize(int x, int y, int format);
    public static native void onNativeKeyDown(int keycode);
    public static native void onNativeKeyUp(int keycode);
    public static native void onNativeTouch(int action, float x, float y, float p);
//    public static native void onNativeAccel(float x, float y, float z);
    public static native void nativeRunAudioThread();
    public static native void onNativeSoftKeyDown(int keycode);
    public static native void onNativeSoftKeyUp(int keycode);
    public static native void onNativeFileDir(int obj, String name);
    public static native void onNativeNP2Mouse(int action, float x, float y);   

    
    //メニューが生成される際に起動される。
    //この中でメニューのアイテムを追加したりする。
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	//メニューインフレーターを取得
    	MenuInflater inflater = getMenuInflater();
    	//xmlのリソースファイルを使用してメニューにアイテムを追加
    	inflater.inflate(R.menu.menu, menu);
    	//できたらtrueを返す
    	return true;
    }
    //メニューのアイテムが選択された際に起動される。
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//    	builder.setTitle("タイトル");
//    	final String message;
//    	FrameLayout keyboardLayout = (FrameLayout)findViewById(R.id.keyboardLayer);
    	LinearLayout keyboardLayout = (LinearLayout)findViewById(R.id.keyboardLayer);
		LinearLayout mainViewLayout = (LinearLayout)findViewById(R.id.mainView);
        
    	switch (item.getItemId()) {
		case R.id.np2_menu:
			SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_MENU);
			return true;

		case R.id.FDD1open:
	        SelectFile(FDD1);
			return true;

		case R.id.FDD1eject:
	        SDLActivity.onNativeFileDir(FDD1, "(null)");
			return true;
		
		case R.id.FDD2open:
	        SelectFile(FDD2);
			return true;
			
		case R.id.FDD2eject:
	        SDLActivity.onNativeFileDir(FDD2, "(null)");
			return true;

		case R.id.HDD1open:
	        SelectFile(HDD1);
			return true;

		case R.id.HDD1eject:
	        SDLActivity.onNativeFileDir(HDD1, "(null)");
			return true;
		
		case R.id.HDD2open:
	        SelectFile(HDD2);
			return true;
			
		case R.id.HDD2eject:
	        SDLActivity.onNativeFileDir(HDD2, "(null)");
			return true;

		case R.id.fullscreen:
			Intent intent = new Intent();
			intent.setClassName(getPackageName(), getClass().getPackage().getName()+".Fullscreen");
			startActivity(intent);
			mainViewLayout.removeView(mSurface);	
		
		case R.id.keyboard:
		    editor = pref.edit();
		    
			if (keyboardLayout.getVisibility() != View.VISIBLE) {
				keyboardLayout.setVisibility(View.VISIBLE);
				editor.putBoolean("button",true);			
				editor.commit();

            } else {
            	keyboardLayout.setVisibility(View.GONE);
				editor.putBoolean("button",false);			
				editor.commit();
            }
			return true;

		case R.id.reset:
			SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_POWER);
			return true;
			
		case R.id.exit:
			nativeQuit();
			return true;
			
		default:
//			message = "良く分からないメニューが指定された！";
			break;
		}
//    	builder.setMessage(message);
//    	builder.setPositiveButton("OK",new android.content.DialogInterface.OnClickListener() {
//	        public void onClick(android.content.DialogInterface dialog,int whichButton) {
//	            setResult(RESULT_OK);
//	        }
//	    });
//    	builder.create();
//    	builder.show();

    	return true;
    }


	// Events
	protected	SelectFileDialog	_dlgSelectFile;
	
	private void SelectFile(int device) {
        
		_dlgSelectFile = new SelectFileDialog(this, device);
		_dlgSelectFile.Show(NP2Dir);
	}

	
	public void onFileSelected_by_SelectFileDialog(File file,int device) {
		
		if(file != null) {
//			Log.d("SDL","device :" + device + " selected : " + file.getName());
			SDLActivity.onNativeFileDir(device, file.getAbsolutePath());
		} else
//			Log.d("SDL","not selected");

		_dlgSelectFile = null;
	}

	
    
    // Messages from the SDLMain thread
    static int COMMAND_CHANGE_TITLE = 1;

    // Handler for the messages
    Handler commandHandler = new Handler() {
        @Override
		public void handleMessage(Message msg) {
            if (msg.arg1 == COMMAND_CHANGE_TITLE) {
                setTitle((String)msg.obj);
            }
        }
    };

    // Send a message from the SDLMain thread
    void sendCommand(int command, Object data) {
        Message msg = commandHandler.obtainMessage();
        msg.arg1 = command;
        msg.obj = data;
        commandHandler.sendMessage(msg);
    }

    // Java functions called from C

    public static void createGLContext() {
        mSurface.initEGL();
    }

    public static void flipBuffers() {
        mSurface.flipEGL();
    }


    public static void setActivityTitle(String title) {
        // Called from SDLMain() thread and can't directly affect the view
        mSingleton.sendCommand(COMMAND_CHANGE_TITLE, title);
    }


    // Audio
    private static Object buf;

    public static Object audioInit(int sampleRate, boolean is16Bit, boolean isStereo, int desiredFrames) {
        int channelConfig = isStereo ? AudioFormat.CHANNEL_CONFIGURATION_STEREO : AudioFormat.CHANNEL_CONFIGURATION_MONO;
        int audioFormat = is16Bit ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
        int frameSize = (isStereo ? 2 : 1) * (is16Bit ? 2 : 1);

        Log.v("SDL", "SDL audio: wanted " + (isStereo ? "stereo" : "mono") + " " + (is16Bit ? "16-bit" : "8-bit") + " " + (sampleRate / 1000f) + "kHz, " + desiredFrames + " frames buffer");

        // Let the user pick a larger buffer if they really want -- but ye
        // gods they probably shouldn't, the minimums are horrifyingly high
        // latency already
        desiredFrames = Math.max(desiredFrames, (AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat) + frameSize - 1) / frameSize);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                channelConfig, audioFormat, desiredFrames * frameSize, AudioTrack.MODE_STREAM);

        audioStartThread();

//        Log.v("SDL", "SDL audio: got " + ((mAudioTrack.getChannelCount() >= 2) ? "stereo" : "mono") + " " + ((mAudioTrack.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) ? "16-bit" : "8-bit") + " " + ((float)mAudioTrack.getSampleRate() / 1000f) + "kHz, " + desiredFrames + " frames buffer");

        if (is16Bit) {
            buf = new short[desiredFrames * (isStereo ? 2 : 1)];
        } else {
            buf = new byte[desiredFrames * (isStereo ? 2 : 1)];
        }
        return buf;
    }

    public static void audioStartThread() {

        mAudioThread = new Thread(new Runnable() {
            public void run() {
                mAudioTrack.play();
                nativeRunAudioThread();
            }
        });

        // I'd take REALTIME if I could get it!
        mAudioThread.setPriority(Thread.MAX_PRIORITY);
        mAudioThread.start();

    }

    public static void audioWriteShortBuffer(short[] buffer) {

        for (int i = 0; i < buffer.length; ) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    // Nom nom
                }
            } else {
                Log.w("SDL", "SDL audio: error return from write(short)");
                return;
            }
        }

    }

    public static void audioWriteByteBuffer(byte[] buffer) {

        for (int i = 0; i < buffer.length; ) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    // Nom nom
                }
            } else {
                Log.w("SDL", "SDL audio: error return from write(short)");
                return;
            }
        }

    }

    public static void audioQuit() {

        if (mAudioThread != null) {
            try {
                mAudioThread.join();
            } catch(Exception e) {
                Log.v("SDL", "Problem stopping audio thread: " + e);
            }
            mAudioThread = null;

            Log.v("SDL", "Finished waiting for audio thread");
        }

        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack = null;
        }

    }

    
    // Keyboard Action
    private int onkey;

	public void onPress(int primaryCode) {
		
		onkey = primaryCode;
		
		if (onkey > 0 ) {
			SDLActivity.onNativeSoftKeyDown(onkey);
		}
		else {}
	}

	public void onRelease(int primaryCode) {

        SDLActivity.onNativeSoftKeyUp(onkey);
        onkey = 0;
	}
	
	public void onKey(int primaryCode, int[] keyCodes) {}
	public void onText(CharSequence text) {}
	public void swipeDown() {}
	public void swipeLeft() {}
	public void swipeRight() {}
	public void swipeUp() {}

    // MotionEvent
    @Override
    public boolean onTrackballEvent (MotionEvent event) {

        int action = event.getAction();
        float x = event.getX()*50;
        float y = event.getY()*50;
        SDLActivity.onNativeNP2Mouse(action, x, y);
//        Log.d("TrackBallEvent", " X:" + x + ",Y:" + y + ",Action:" + action);

        return true;
    }
    
    //KeyEvent
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

    	int onkey = scancode.toNp2Code(event.getScanCode());
        Log.v("SDL", "Scan Code: " + event.getScanCode() + " Key Code: " + event.getKeyCode());

    	if (onkey == 120 || event.getKeyCode() == KeyEvent.KEYCODE_MENU){
			openOptionsMenu();
			return true;
		}
    	
		if (onkey == 0) {
			return true;
		}
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            Log.v("SDL", "Scan Code: " + scanCode + " np2Code: " + scancode.toNp2Code(scanCode));
        	if (onkey == 124){
        		int volume = aManager.getStreamVolume( AudioManager.STREAM_MUSIC ) + 1;
        		if ( volume <= vol )
					aManager.setStreamVolume( AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI );
        	} else if (onkey == 123){
        		int volume = aManager.getStreamVolume( AudioManager.STREAM_MUSIC ) - 1;
        		if ( volume >= 0 )
					aManager.setStreamVolume( AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI );
        	}
        	else onNativeSoftKeyDown(onkey);
        	
        	return true;
        }
        else if (event.getAction() == KeyEvent.ACTION_UP) {
        	if (onkey <= 122 ) onNativeSoftKeyUp(onkey);
			return true;
        }
        
        return true;

    }    
    
}
