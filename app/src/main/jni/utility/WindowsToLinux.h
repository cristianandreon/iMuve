
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




#define FREE_POINTER(__ptr)  \
    if (__ptr) my_free(__ptr); \
    __ptr = NULL;


#define strcpy_s(__dst, __dst_size, __src) strncpy(__dst, __src, min(__dst_size, __src?strlen(__src)+1:1))

#define strcat_s(__src, __src_size, __add)   strcat(__src, __add)



#define TRY
#define CATCH
#define ETRY