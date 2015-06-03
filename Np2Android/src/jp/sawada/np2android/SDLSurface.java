package jp.sawada.np2android;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.Log;
//import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
    SDLSurface. This is what we draw on, so we need to know when it's created
    in order to do anything useful.

    Because of this, that's where we set up the SDL thread
*/
class SDLSurface extends SurfaceView implements SurfaceHolder.Callback,
    View.OnTouchListener/*, View.OnKeyListener, SensorEventListener*/  {

    // This is what SDL runs in. It invokes SDL_main(), eventually
    private Thread mSDLThread;
//    private SurfaceHolder holder;

    // EGL private objects
//    private EGLContext  mEGLContext;
    private EGLSurface  mEGLSurface;
    private EGLDisplay  mEGLDisplay;

    // displaySize
    private int displayWidth;
    private int displayHeight;

    // Sensors
//    private static SensorManager mSensorManager;

    // Startup
    public SDLSurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
//        setOnKeyListener(this);
        setOnTouchListener(this);
        setKeepScreenOn(true);

//        mSensorManager = (SensorManager)context.getSystemService("sensor");
    }

    // Called when we have a valid drawing surface
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("SDL", "surfaceCreated()");
//        enableSensor(Sensor.TYPE_ACCELEROMETER, true);
    }

    // Called when we lose the surface
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("SDL", "surfaceDestroyed()");

        // Send a quit message to the application
        SDLActivity.nativeQuit();

        // Now wait for the SDL thread to quit
        if (mSDLThread != null) {
            try {
                mSDLThread.join();
            } catch(Exception e) {
                Log.v("SDL", "Problem stopping thread: " + e);
            }
            mSDLThread = null;

            //Log.v("SDL", "Finished waiting for SDL thread");
        }

//        enableSensor(Sensor.TYPE_ACCELEROMETER, false);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	
    	this.displayWidth = w;
        this.displayHeight = h;
        Log.v("SDL", "onSizeChanged()" + w + "," + h + "," + oldw + "," + oldh );
    }
    // Called when the surface is resized
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
        Log.v("SDL", "surfaceChanged()" + width + "," + height);
        int sdlFormat = 0x85151002; // SDL_PIXELFORMAT_RGB565 by default
        switch (format) {
        case PixelFormat.A_8:
            Log.v("SDL", "pixel format A_8");
            break;
        case PixelFormat.LA_88:
            Log.v("SDL", "pixel format LA_88");
            break;
        case PixelFormat.L_8:
            Log.v("SDL", "pixel format L_8");
            break;
        case PixelFormat.RGBA_4444:
            Log.v("SDL", "pixel format RGBA_4444");
            sdlFormat = 0x85421002; // SDL_PIXELFORMAT_RGBA4444
            break;
        case PixelFormat.RGBA_5551:
            Log.v("SDL", "pixel format RGBA_5551");
            sdlFormat = 0x85441002; // SDL_PIXELFORMAT_RGBA5551
            break;
        case PixelFormat.RGBA_8888:
            Log.v("SDL", "pixel format RGBA_8888");
            sdlFormat = 0x86462004; // SDL_PIXELFORMAT_RGBA8888
            break;
        case PixelFormat.RGBX_8888:
            Log.v("SDL", "pixel format RGBX_8888");
            sdlFormat = 0x86262004; // SDL_PIXELFORMAT_RGBX8888
            break;
        case PixelFormat.RGB_332:
            Log.v("SDL", "pixel format RGB_332");
            sdlFormat = 0x84110801; // SDL_PIXELFORMAT_RGB332
            break;
        case PixelFormat.RGB_565:
            Log.v("SDL", "pixel format RGB_565");
            sdlFormat = 0x85151002; // SDL_PIXELFORMAT_RGB565
            break;
        case PixelFormat.RGB_888:
            Log.v("SDL", "pixel format RGB_888");
            // Not sure this is right, maybe SDL_PIXELFORMAT_RGB24 instead?
            sdlFormat = 0x86161804; // SDL_PIXELFORMAT_RGB888
            break;
        default:
            Log.v("SDL", "pixel format unknown " + format);
            break;
        }
        
        SDLActivity.onNativeResize(width, height, sdlFormat);
        // Now start up the C app thread
        if (mSDLThread == null) {
            mSDLThread = new Thread(new SDLMain(), "SDLThread");
            mSDLThread.start();
        }
    }

    // unused
    @Override
	public void onDraw(Canvas canvas) {}


    // EGL functions
    public boolean initEGL() {
        Log.v("SDL", "Starting up");

        try {

            EGL10 egl = (EGL10)EGLContext.getEGL();

            EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

            int[] version = new int[2];
            egl.eglInitialize(dpy, version);

            int[] configSpec = {
                    //EGL10.EGL_DEPTH_SIZE,   16,
                    EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] num_config = new int[1];
            egl.eglChooseConfig(dpy, configSpec, configs, 1, num_config);
            EGLConfig config = configs[0];

            EGLContext ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, null);

            EGLSurface surface = egl.eglCreateWindowSurface(dpy, config, this, null);

            egl.eglMakeCurrent(dpy, surface, surface, ctx);

//            mEGLContext = ctx;
            mEGLDisplay = dpy;
            mEGLSurface = surface;

        } catch(Exception e) {
            Log.v("SDL", e + "");
            for (StackTraceElement s : e.getStackTrace()) {
                Log.v("SDL", s.toString());
            }
        }

        return true;
    }

    // EGL buffer flip
    public void flipEGL() {
        try {
            EGL10 egl = (EGL10)EGLContext.getEGL();

            egl.eglWaitNative(EGL10.EGL_NATIVE_RENDERABLE, null);

            // drawing here

            egl.eglWaitGL();

            egl.eglSwapBuffers(mEGLDisplay, mEGLSurface);


        } catch(Exception e) {
            Log.v("SDL", "flipEGL(): " + e);
            for (StackTraceElement s : e.getStackTrace()) {
                Log.v("SDL", s.toString());
            }
        }
    }

    // Touch events
    public boolean onTouch(View v, MotionEvent event) {
    	

        int action = event.getAction();
        float x = (event.getX() * 640 / displayWidth);
        float y = (event.getY() *480 / displayHeight);
        float p = event.getPressure();

//        Log.v("SDL", "SDL X: " + x + "SDL Y: " + y + "SDL Action: " + action);
        SDLActivity.onNativeTouch(action, x, y, p);
        return true;
    }
}

