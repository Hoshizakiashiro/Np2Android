#include	"compiler.h"
#include	"softkey.h"
#include	"keystat.h"
#include	"mousemng.h"
#include	"np2.h"
#include	"diskdrv.h"

//#include <android/log.h>

JNIEXPORT void JNICALL Java_jp_sawada_np2android_SDLActivity_onNativeSoftKeyDown (JNIEnv *env, jclass jcls, jint nkeycode) {

	BYTE	data;

	if (nkeycode == 117) {
		data = 0;
	}
	else {
		data = nkeycode;
	}

	if (data <118 ) {
		keystat_senddata(data);
	}
	else if (data == 118) {
		mousemng.btn &= ~(uPD8255A_LEFTBIT);	//左クリック
	}
	else if (data == 119) {
		mousemng.btn &= ~(uPD8255A_RIGHTBIT);	//右クリック
	}
	else {}
}

JNIEXPORT void JNICALL Java_jp_sawada_np2android_SDLActivity_onNativeSoftKeyUp (JNIEnv *env, jclass jcls, jint nkeycode) {

	BYTE	data;
	int	id;

	if (nkeycode == 117) {
		data = 0;
	}
	else {
		data = nkeycode;
	}

	if (data < 118) {
		keystat_senddata((BYTE)(data | 0x80));
	}
	else if (data == 118) {
		mousemng.btn |= uPD8255A_LEFTBIT;;	//左クリック
	}
	else if (data == 119) {
		mousemng.btn |= uPD8255A_RIGHTBIT;	//右クリック
	}
	else if (data == 121) {
		flagsave("s00");
	}
	else if (data == 122) {
		id = flagload("s00", "Status Load", FALSE);
	}

}

JNIEXPORT void JNICALL Java_jp_sawada_np2android_SDLActivity_onNativeNP2Mouse (JNIEnv *env, jclass jcls, jint action, jfloat jx, jfloat jy) {

	int x = jx;
	int y = jy;

	if (action == 2) {	//move
		mousemng.x = x;	//x
		mousemng.y = y;	//y

	} else if (action == 0) {	//ボタンダウンだよ
		mousemng.btn &= ~(uPD8255A_LEFTBIT);	//左クリック
	} else if (action == 1) {	//ボタンアップだよ
		mousemng.btn |= uPD8255A_LEFTBIT;	//左クリック解除
	}
}

JNIEXPORT void JNICALL Java_jp_sawada_np2android_SDLActivity_onNativeFileDir (JNIEnv *env, jclass jcls, jint device, jstring name) {

	const char *path = (*env)->GetStringUTFChars(env, name, 0);

//	__android_log_print(ANDROID_LOG_INFO, "SDL", "DISK-filepath=(%s)",path );
	
	if (device == 0) {
		/* Set current directory */
		chdir(path);
	}
	else if (device == 1) {
		/* Set SASI1 filepath */
		diskdrv_sethdd(0, path);

	}
	else if (device == 2) {
		/* Set SASI2 filepath */
		diskdrv_sethdd(1, path);
	}
	else if (device == 3) {
		/* Set FDD1 filepath */
		diskdrv_setfdd(0, path, 0);
	}
	else if (device == 4) {
		/* Set FDD2 filepath */
		diskdrv_setfdd(1, path, 0);
	}
	(*env)->ReleaseStringUTFChars(env, name, path);
}
