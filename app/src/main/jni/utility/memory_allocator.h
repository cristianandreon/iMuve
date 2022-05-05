
#define FREE_POINTER(__ptr) \
	if (__ptr) my_free(__ptr);\
	__ptr = NULL;


extern unsigned int malloc_size(void *ptr);
extern void *my_realloc (void *ptr, unsigned int size );
extern void *my_calloc(unsigned int size, unsigned int n);
extern void my_free (void *ptr);

extern int NSLog ( char *txt );
extern int check_general_structure_allocated ( int PtrCurWrk, void **StructureData,
							UINT StructureSize, UINT NumItem, UINT *NumItemAllocated,
							 UINT NumGapItem, MDB_CHAR *FeatureName, HWND ptr_hwnd );
extern void CpyStr ( MDB_CHAR **Target, MDB_CHAR *Source, UINT *TargetSize );
extern void AddStr ( MDB_CHAR **Target, MDB_CHAR *Source, UINT *TargetSize );
