package jp.sawada.np2android;


class SDLMain implements Runnable {
    
    public void run() {  
//        Log.v("SDL", "SDL path: " + NP2Path);
        SDLActivity.onNativeFileDir(SDLActivity.NP2PATH, SDLActivity.NP2Dir);

        // Runs SDL_main()   
        SDLActivity.nativeInit();
        
    }

}


