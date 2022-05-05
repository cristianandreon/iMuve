
#define _STLP_HAS_INCLUDE_NEXT  1
#define _STLP_USE_MALLOC   1
#define _STLP_USE_NO_IOSTREAMS  1


#include <stdlib.h>
#include <malloc.h>
#include <string.h>


#include "WindowsToAndroid.h"


#ifdef WINDOWS
    #define CAST_VOID_PTR (int)
    #else
    #define CAST_VOID_PTR
    #endif


#define DEBUG_LEVEL1
// #define DEBUG_LEVEL2
// #define DEBUG_LEVEL3



#define MY_MALLOC_SIGN  123456789

#define MARK_ALLOCATED_SIZE

unsigned int GLAllocatedBytes = 0;



char *_GLErrStr = NULL;




unsigned int malloc_size(void *ptr) {
    unsigned int malloc_usable_size = 0, malloc_sign = 0;

    #ifdef MARK_ALLOCATED_SIZE

		#ifdef DEBUG_LEVEL3
			my_printf("[malloc_size:ptr:%ld, sizeof(uint):%d]", ptr, sizeof(unsigned int));
		#endif

		memcpy(&malloc_sign, (void*)((long)ptr-sizeof(int)-sizeof(unsigned int)), sizeof(unsigned int));

		#ifdef DEBUG_LEVEL3
			my_printf("[malloc_size:malloc_sign:]", malloc_sign);
		#endif

        if (malloc_sign == MY_MALLOC_SIGN) {
			#ifdef DEBUG_LEVEL3
    			my_printf("[malloc_size:getting size]");
			#endif
            memcpy(&malloc_usable_size, (void*)((long)ptr-sizeof(unsigned int)), sizeof(unsigned int));

        	} else {
			// my_printf("[malloc_size]:Failed by sign:%d", malloc_sign);
            }

		#ifdef DEBUG_LEVEL3
		my_printf("[malloc_size:ptr:%ld]", ptr);
		#endif

        #endif

    return malloc_usable_size;
}



void *my_realloc (void *ptr, unsigned int size ) {
    void *old_ptr = (void *)ptr;

	#ifdef DEBUG_LEVEL3
		my_printf("[my_realloc]:ptr:%ld", (long)ptr);
	#endif

	unsigned int old_size = ptr?malloc_size(ptr):0;

	#ifdef DEBUG_LEVEL3
		my_printf("[my_realloc]:old_size:", old_size);
	#endif

    ptr = (void*)calloc(size+4+4, 1);

    if (!ptr) {
        #ifdef DEBUG_LEVEL1
            my_printf("[my_realloc]:Failed to allocated %d bytes",size+4+4);
        #endif
        if (old_ptr) {
            free(old_ptr);
            }
        return NULL;
        }

    GLAllocatedBytes += size;

    if (ptr != old_ptr) {

        #ifdef MARK_ALLOCATED_SIZE

    		#ifdef DEBUG_LEVEL3
    			my_printf("[my_realloc]:mark sign");
			#endif

    		if (ptr) {
                unsigned int malloc_sign = MY_MALLOC_SIGN;
                memcpy(ptr, &malloc_sign, sizeof(unsigned int));
                CAST_VOID_PTR ptr += sizeof(unsigned int);
                memcpy(ptr, &size, sizeof(unsigned int));
                CAST_VOID_PTR ptr += sizeof(unsigned int);
                }

			#ifdef DEBUG_LEVEL3
				my_printf("[my_realloc]:mark sign done");
			#endif

			#endif


        if (ptr && old_ptr && old_size) {
            // my_printf("[my_realloc]:old_size:%d",old_size);
            if (old_size > size) {
                old_size = size;
                }
			#ifdef DEBUG_LEVEL3
				my_printf("[my_realloc]:copy old ptr");
			#endif

			memcpy(ptr, old_ptr, old_size);

            #ifdef DEBUG_LEVEL3
				my_printf("[my_realloc]:copy old ptr done");
			#endif
            }

        if (old_ptr) {
            if (old_size) {
				#ifdef DEBUG_LEVEL3
					my_printf("[my_realloc]:getting original pointer");
				#endif
            	CAST_VOID_PTR old_ptr -= (sizeof(unsigned int)+sizeof(unsigned int));
            	}
			#ifdef DEBUG_LEVEL3
				my_printf("[my_realloc]:free the ptr");
			#endif
            free(old_ptr);
            GLAllocatedBytes -= old_size;
			#ifdef DEBUG_LEVEL3
				my_printf("[my_realloc]:free the ptr done");
			#endif
            }
        }
    
	#ifdef DEBUG_LEVEL2
		my_printf("[my_realloc]:done:%d",ptr);
	#endif

    return ptr;
}



void *my_calloc(unsigned int size, unsigned int n) {
    GLAllocatedBytes += size*n;
    #ifdef DEBUG_LEVEL2
        my_printf("[%db]",GLAllocatedBytes);
        #endif
    return calloc (size, n);
}


void my_free (void *ptr) {
if (ptr) {

	#ifdef DEBUG_LEVEL3
    	my_printf("[my_free:ptr:%ld]", (long)ptr);
		#endif

    unsigned int old_size = malloc_size(ptr);

    #ifdef DEBUG_LEVEL3
    	my_printf("[my_free:old_size:%d]",old_size);
		#endif

    if (old_size) {
        GLAllocatedBytes -= old_size;
        #ifdef DEBUG_LEVEL2
            my_printf("[%db]",GLAllocatedBytes);
        #endif

        free((void*)((long)ptr-sizeof(unsigned int)-sizeof(unsigned int)));

    	} else {
        free(ptr);
        }
    }
}


int NSLog ( char *txt ) {
	return 0;
}







int check_general_structure_allocated ( int PtrCurWrk, void **StructureData,
							UINT StructureSize, UINT NumItem, UINT *NumItemAllocated,
							 UINT NumGapItem, MDB_CHAR *FeatureName, HWND ptr_hwnd )
{	UINT prev_item = 0;
	MDB_CHAR *old_ptr = NULL, *new_ptr = NULL, str[512];
	int res = 0, res_add = -1;


if (!StructureData) {
	return -2;
	}
if (!NumItemAllocated) {
	return -3;
	}
if (!StructureSize) {
	return -4;
	}


start_execute_function:

if (NumItem >= *NumItemAllocated) {

	// Riallocazione
	prev_item = *NumItemAllocated;
	old_ptr = (MDB_CHAR *)(StructureData[0]);


	start_allocate_memory:

	*NumItemAllocated = NumItem+NumGapItem;


	if (StructureData[0]) {
		// Tentativo di aggiunta memoria
		// res_add = _heapadd( StructureData[0], StructureSize * NumItemAllocated[0]+1 );
		res_add = 999999;
		}

	if (res_add != 0) {
		// Aggiunta memoria fallita : riallocazione
		int local_error = -1;

		new_ptr = malloc (StructureSize * NumItemAllocated[0]+1);


		if (!(new_ptr)) {
			MDB_CHAR DummyStr[256];
			*NumItemAllocated = prev_item;


			if (res == 9999999) {
				NumGapItem = 0;
				goto start_allocate_memory;
				} else {
				FREE_POINTER(new_ptr);
				return -1;
				}
			}

		if (prev_item && old_ptr) {
			memcpy (new_ptr, old_ptr, StructureSize*prev_item);
			}

		if (prev_item) {
			memset (((MDB_CHAR*)(new_ptr + prev_item*StructureSize)), 0, StructureSize*(*NumItemAllocated-prev_item));
			} else {
			memset (new_ptr, 0, StructureSize*(*NumItemAllocated));
			}

		StructureData[0] = new_ptr;
		new_ptr = NULL;

		if (old_ptr) {
			free (old_ptr);
			}
		old_ptr = NULL;

		} else {
		// Incremento memoria riuscito : azzeramento
		if (prev_item > 0) {
			memset ((MDB_CHAR*)StructureData[0] + prev_item*StructureSize, 0, StructureSize*(*NumItemAllocated-prev_item));
			} else {
			memset (StructureData[0], 0, StructureSize*(*NumItemAllocated));
			}
		}

	} else {
	if (!(StructureData[0])) {
		*NumItemAllocated = 0;
		goto start_execute_function;
		}
	return 0;
	}



return 1;
}






#define DELTA_STRING_DIM       16000
#define SAFE_STRING_SIZE	32

void CpyStr ( MDB_CHAR **Target, MDB_CHAR *Source, UINT *TargetSize )
{

if (Target) {
	if (Source == NULL) {
		if (*Target) {
			**Target = 0;
			} else {
			if (TargetSize) {
				if (*TargetSize) {
					*TargetSize = 0;
					}
				}
			}
		return;
		}

	if (TargetSize) {
		if (strlen (Source)+SAFE_STRING_SIZE >= *TargetSize) {
			*TargetSize = strlen (Source)+DELTA_STRING_DIM;
			*Target = (MDB_CHAR *)realloc (*Target, *TargetSize);
			if (*Target == NULL) {
				*TargetSize = 0;
				return;
				}
			}
		} else {
		return;
		}


	if (*Target) {
		strcpy_s (*Target, *TargetSize, Source);
		} else {
		if (TargetSize) {
			if (*TargetSize != 0) {
				*TargetSize = 0;
				}
			}
		}
	}


return;
}


void AddStr (MDB_CHAR **Target, MDB_CHAR *Source, UINT *TargetSize )
{ 	MDB_CHAR *old_ptr = NULL;

if (Target) {

	if (Source == NULL) {
		return;
		}



	if (TargetSize) {
		UINT LocalSizeTarget = 0, len = 0;




		if (*Target) {
			len = strlen (*Target);
			} else {
			len = 0;
			}

		if (len+strlen (Source)+SAFE_STRING_SIZE >= *TargetSize) {
			*TargetSize = len+strlen (Source)+DELTA_STRING_DIM + len/5;
			old_ptr = *Target;
			*Target = (MDB_CHAR *)malloc (*TargetSize);
			if (!(*Target)) {
				*TargetSize = 0;
				return;
				}
			if (old_ptr) {
				memcpy ((void*)*Target, old_ptr, len);
				memset ((void*)(*Target + len), 0, (*TargetSize - len));
				free (old_ptr);
				} else {
				memset ((void*)*Target, 0, (*TargetSize));
				}
			}

		if (*Target) {
			strcat_s (*Target, *TargetSize, Source);
			} else {
			if (TargetSize) {
				if (*TargetSize != 0) {
					*TargetSize = 0;
					}
				}
			}
		}
	}


return;
}

