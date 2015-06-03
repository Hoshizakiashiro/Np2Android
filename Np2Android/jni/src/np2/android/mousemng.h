
#ifdef __cplusplus
extern "C" {
#endif

enum {
        uPD8255A_LEFTBIT        = 0x80,
        uPD8255A_RIGHTBIT       = 0x20
};

typedef struct {
    SINT16  x;
    SINT16  y;
    UINT8   btn;
} MOUSEMNG;

extern MOUSEMNG mousemng;

UINT8 mousemng_getstat(SINT16 *x, SINT16 *y, int clear);
void mousemng_initialize(void);

#ifdef __cplusplus
}
#endif

