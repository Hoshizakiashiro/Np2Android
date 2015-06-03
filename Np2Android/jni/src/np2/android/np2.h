
typedef struct {
	BYTE	NOWAIT;
	BYTE	DRAW_SKIP;
	BYTE	F12KEY;
	BYTE	resume;
	BYTE	jastsnd;
} NP2OSCFG;


#if defined(SIZE_QVGA)
enum {
	FULLSCREEN_WIDTH	= 320,
	FULLSCREEN_HEIGHT	= 240
};
#else
enum {
	FULLSCREEN_WIDTH	= 640,
	FULLSCREEN_HEIGHT	= 480
};
#endif

extern	NP2OSCFG	np2oscfg;

int flagload(const char* ext, const char* title, BOOL force);
int flagsave(const char* ext);
void flagdelete(const char* ext);

