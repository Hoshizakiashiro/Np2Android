#include	"compiler.h"
// #include	<signal.h>
#include	"inputmng.h"
#include	"taskmng.h"
#include	"sdlkbd.h"
#include	"mousemng.h"
#include	"vramhdl.h"
#include	"menubase.h"
#include	"sysmenu.h"
#include	"pccore.h"
//#include	<android/log.h>


	BOOL	task_avail;
	MOUSEMNG	mousemng;
	BOOL	tap = FALSE;		//タップされたかどうかの判定用
	UINT32	count ;		//カウント用

void sighandler(int signo) {

	(void)signo;
	task_avail = FALSE;
}


void taskmng_initialize(void) {

	task_avail = TRUE;
    ZeroMemory(&mousemng, sizeof(mousemng));
    mousemng.btn = uPD8255A_LEFTBIT | uPD8255A_RIGHTBIT;
}

void taskmng_exit(void) {

	task_avail = FALSE;
}

void taskmng_rol(void) {

	SDL_Event	e;

	if (SDL_GetTicks() > count + 30 ) {			//0.03秒たった？
		count = SDL_GetTicks() + 10000000;					//カウントリセット
		tap = 0;									//タップ解除
		mousemng.btn |= uPD8255A_LEFTBIT;			//左クリック解除
//		__android_log_print(ANDROID_LOG_INFO, "SDL", "SDL mouseup");
	}


	if ((!task_avail) || (!SDL_PollEvent(&e))) {
		return;
	}
	switch(e.type) {
		case SDL_MOUSEMOTION:

			if (menuvram == NULL) {

				if(e.motion.state == 1) {	//ボタン押されてる
					tap = FALSE;			//動いたのでtapじゃないよ
					mousemng.x = e.motion.xrel;
					mousemng.y = e.motion.yrel;//移動
				}
				else { 						//最初のいらないモーション
				}
			}
			else {
				menubase_moving(e.motion.x, e.motion.y, 0);
			}
			break;

		case SDL_MOUSEBUTTONUP:
//			switch(e.button.button) {
//				case SDL_BUTTON_LEFT:
//					longtap = SDL_GetTicks();//カウントリセット

					if (menuvram == NULL) {
						if (tap) {	//tapかどうかチェック
							mousemng.btn &= ~(uPD8255A_LEFTBIT);//	左クリックを送るよ
							count = SDL_GetTicks();//カウント開始
//							__android_log_print(ANDROID_LOG_INFO, "SDL", "SDL mousedown");
						}

					}
					else {
						menubase_moving(e.button.x, e.button.y, 2);
					}
					break;

//				case SDL_BUTTON_RIGHT:
//					break;
//			}
//			break;

		case SDL_MOUSEBUTTONDOWN:
//			switch(e.button.button) {
//				case SDL_BUTTON_LEFT:

					if (menuvram == NULL) {
						tap = TRUE;	//tapかも知れないよ
//						longtap = SDL_GetTicks();//カウントリセット
					}
					else {
						menubase_moving(e.button.x, e.button.y, 1);
					}
//					break;

//				case SDL_BUTTON_RIGHT:
//					break;
//			}
			break;

		case SDL_KEYDOWN:
			if (e.key.keysym.scancode == SDL_SCANCODE_MENU) {
				if (menuvram == NULL) {
					sysmenu_menuopen(0, 0, 0);
				}
				else {
					menubase_close();
				}
			}
			else if (e.key.keysym.scancode == SDL_SCANCODE_POWER) {
				pccore_cfgupdate();
				pccore_reset();
			}
			else if (e.key.keysym.scancode == SDL_SCANCODE_SELECT) {
				mousemng.btn &= ~(uPD8255A_LEFTBIT);
			}
			else if (e.key.keysym.scancode == SDL_SCANCODE_AC_BACK) {
				mousemng.btn &= ~(uPD8255A_RIGHTBIT);
			}
			else {
				sdlkbd_keydown(e.key.keysym.scancode);
			}
			break;

		case SDL_KEYUP:
			if (e.key.keysym.scancode == SDL_SCANCODE_SELECT) {
				mousemng.btn |= uPD8255A_LEFTBIT;
			}
			else if (e.key.keysym.scancode == SDL_SCANCODE_AC_BACK) {
				mousemng.btn |= uPD8255A_RIGHTBIT;
			}
			else {
				sdlkbd_keyup(e.key.keysym.scancode);
			}
			break;

		case SDL_QUIT:
			task_avail = FALSE;
			break;
	}
}

BOOL taskmng_sleep(UINT32 tick) {

	UINT32	base;

	base = GETTICK();
	while((task_avail) && ((GETTICK() - base) < tick)) {
		taskmng_rol();
#ifndef WIN32
		usleep(960);
#else
		Sleep(1);
#endif
	}
	return(task_avail);
}

