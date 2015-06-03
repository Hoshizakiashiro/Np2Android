#include	"compiler.h"
#include	"mousemng.h"

UINT8 mousemng_getstat(SINT16 *x, SINT16 *y, int clear)
{
//	UINT8 btn;

    *x = mousemng.x;
    *y = mousemng.y;

    if (clear) {
        mousemng.x = 0;
        mousemng.y = 0;
    }
//    btn = mousemng.btn;
//    mousemng.btn = uPD8255A_LEFTBIT | uPD8255A_RIGHTBIT;
    return mousemng.btn;
}
