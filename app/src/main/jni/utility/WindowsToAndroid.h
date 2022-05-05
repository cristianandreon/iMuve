#define _STLP_HAS_INCLUDE_NEXT  1
#define _STLP_USE_MALLOC   1
#define _STLP_USE_NO_IOSTREAMS  1


#ifndef false
	#define false	0
	#endif

#ifndef true
	#define true	1
	#endif

#ifndef NULL
    #define NULL	0
    #endif

#ifndef FALSE
    #define FALSE	false
    #endif

#ifndef TRUE
    #define TRUE	true
    #endif

typedef unsigned int        uint;
typedef unsigned int        UINT;

typedef int        			my_long;

/*
#ifndef uint
    #define uint	unsigned int
    #endif

#ifndef UINT
    #define UINT	unsigned int
    #endif
*/

#define BOOL	int

#define MDB_CHAR unsigned char

#define HWND void *


#ifndef MIN
    #define MIN(a,b) (((a)<(b))?(a):(b))
    #endif

#ifndef MAX
    #define MAX(a,b) (((a)>(b))?(a):(b))
    #endif

#define FREE_POINTER(__ptr)  \
    if (__ptr) my_free(__ptr); \
    __ptr = NULL;


#define strcpy_s(__dst, __dst_size, __src) strncpy(__dst, __src, MIN(__dst_size, __src?strlen(__src)+1:1))

#define strcat_s(__dst, __dst_size, __add)   strcat(__dst, __add)


#define TRY
#define CATCH
#define ETRY

/*

#define try
#define finally
#define catch

#define __try
#define __except(__code)
#define __finally
*/