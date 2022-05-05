

#ifdef JNI_DLL
	#include <windows.h>
	#include <jni-gl.h>
	#include "lib-dwg-master\dwg.h"
	typedef long my_long;
	#elif defined JNI_SO
	#include "utility/WindowsToLinux.h"
	#include "lib-dwg-master/dwg.h"
	#else
    #define _STLP_HAS_INCLUDE_NEXT  1
    #define _STLP_USE_MALLOC   1
    #define _STLP_USE_NO_IOSTREAMS  1
	#include <android/log.h>
	#include "utility/WindowsToAndroid.h"
	#include "lib-dwg-master/dwg.h"
	#endif


#include <stdio.h>
#include <stdlib.h>


#define RGB_GETRED(rgb)    ((rgb) & 0xff)
#define RGB_GETGREEN(rgb)    (((rgb) >> 8) & 0xff)
#define RGB_GETBLUE(rgb)    (((rgb) >> 16) & 0xff)
#define RGB_GETA(rgb)    (((rgb) >> 24) & 0xff)
#define RGB_GETBLU RGB_GETBLUE




/******************
 * Handle functions
 */
typedef struct _handle_map
{
  unsigned int hd;
  unsigned int ix;
} Handle_Map;

static unsigned int hmap_size = 0;
static Handle_Map *hmap = (Handle_Map *)NULL;
static Dwg_Data *hmap_dwg = (Dwg_Data *)NULL;

void
dwg_handle_close () {
  if (hmap) free (hmap);
  hmap = NULL;
  hmap_size = 0;
  hmap_dwg = NULL;
}

/** 
 * Initializes the handle database ordering of dwg data, for searching it.
 */
void dwg_handle_init (Dwg_Data * dwg) {
  unsigned int i;
  int j, y;
  double factor;
  char space[101];
  space[100] = '\0';

  if (dwg == NULL)
    {
      hmap_dwg = NULL;
      return;
    }

  if (dwg->num_objects == 0)
    {
      hmap_dwg = NULL;
      return;
    }

  hmap_dwg = dwg;

  if (hmap_size == 0)
    {
      hmap_size = dwg->num_objects;
      hmap = (Handle_Map *) malloc (hmap_size * sizeof (Handle_Map));
    }
  else if (hmap_size < dwg->num_objects)
    {
      hmap_size = dwg->num_objects;
      hmap = (Handle_Map *) realloc (hmap, hmap_size * sizeof (Handle_Map));
    }



  factor = (double) dwg->header_vars.HANDSEED->absolute_ref / 101.0;

 

  for (i = 0; i < dwg->num_objects; i++) {
      char tmp[1024];
      /// printf ("|");
      y = (double) dwg->object[i].handle.value / factor;
      if (y > 100)
	y = 100;
      memset (space, ' ', 100);
      space[(int) y] = '\0';
      /// printf (space);
      /// printf ("*");
      memset (space, ' ', 100);
      space[100 - (int) y] = '\0';
      /// printf (space);
      sprintf (tmp, "| %08X\n", dwg->object[i].handle.value);
      /// printf (tmp);
    }


  /* Insertion sorting
   */
  hmap[0].hd = dwg->object[0].handle.value;
  hmap[0].ix = 0;
  for (i = 1; i < dwg->num_objects; i++)
    {
      unsigned int a, b, mid;
      unsigned int hdtmp, ixtmp;

      hdtmp = dwg->object[i].handle.value;
      ixtmp = i;
      a = 0;
      b = i - 1;
      while (b > a + 1)
	{
	  mid = (b - a) / 2 + a;
	  if (hdtmp < hmap[mid].hd)
	    b = mid;
	  else
	    a = mid;
	}
      if (hdtmp >= hmap[b].hd)
	b++;

      memmove (&hmap[b + 1], &hmap[b], (i - b) * sizeof (Handle_Map));
      hmap[b].hd = hdtmp;
      hmap[b].ix = ixtmp;
    }

  for (i = 0; i < dwg->num_objects; i++) {
      unsigned int idx;
      my_long delta;
      char tmp[1024];

      idx = hmap[i].ix;

      /// printf ("|");
      y = (double) dwg->object[idx].handle.value / factor;
      if (y > 100)
	y = 100;
      memset (space, ' ', 100);
      space[(int) y] = '\0';
      /// printf (space);
      /// printf ("*");
      memset (space, ' ', 100);
      space[100 - (int) y] = '\0';
      /// printf (space);
      sprintf (tmp, "| %08X", dwg->object[idx].handle.value);
      /// printf (tmp);
      delta = 0;
      if (i > 0)
	delta = dwg->object[idx].handle.value - dwg->object[hmap[i - 1].ix].handle.value;
      sprintf (tmp, " (+=)%li\n", delta);
      /// printf (tmp);
      if (delta == 0 && i > 0)
	{
          sprintf (tmp, "Objects with same handle: obj[%lu].handle == obj[%lu].handle\n",
	     idx, hmap[i - 1].ix);
	  /// printf (tmp);
	}
      else if (delta < 0)
	{
	  /// printf ("Sorting of objects handles failed.\n");
	}
    }
  /// printf ("-------------------------------------------------------------------------------------------------------\n");
}







/** 
 * Finds the index of an object given it's id (handle)
 */
unsigned int dwg_handle_get_index (Dwg_Data *dwg,unsigned int hdl) {
  unsigned int a, b, mid;
  char tmp[1024];

  if (hdl == 0)
    return -1;

  if (!hmap_dwg) 
	  dwg_handle_init(dwg);

  if (hmap_dwg == NULL) {
      // sprintf (tmp, "dwg_handle_get_index(0x%X): " "please initialize the handle mapper (dwg_handle_init)\n" "\tbefore using any other handle function.\n", hdl);
      // printf(tmp);
      return (-1);
    }

  /* Binary searching
   */
  a = 0;
  b = hmap_dwg->num_objects - 1;
  while (b > a + 1)
    {
      mid = (b - a) / 2 + a;
      if (hdl < hmap[mid].hd)
	b = mid;
      else if (hdl > hmap[mid].hd)
	a = mid;
      else
	return (hmap[mid].ix);
    }
  if (hdl == hmap[a].hd)
    return (hmap[a].ix);
  if (hdl == hmap[b].hd)
    return (hmap[b].ix);

  sprintf (tmp, "dwg_handle_get_index(0x%X): handle not found.\n", hdl);
  // printf(tmp);
  return (-1);
}

/** 
 * Finds the absolute id of a handle from a referenced one.
 */
unsigned int 
dwg_handle_absolute (Dwg_Handle *hd, unsigned int refhd)
{
  if (hd->code < 6)
    return (hd->value);
  else if (hd->code == 6 && hd->size != 0)
    return (hd->value);
  else if (hd->code == 6)
    return (refhd + 1);
  else if (hd->code == 8)
    return (refhd - 1);
  else if (hd->code == 10)
    return (refhd + hd->value);
  else if (hd->code == 12)
    return (refhd - hd->value);

  return 0;
}





void *get_layer (Dwg_Data *pdwg, Dwg_Handle plhanle, my_long plabshanle) {
    unsigned short idx;

    idx = dwg_handle_get_index (pdwg,dwg_handle_absolute(&plhanle,plabshanle));
    if (idx < pdwg->num_objects) {
        if(pdwg->object[idx].type == DWG_TYPE_LAYER) {
            return pdwg->object[idx].tio.object->tio.LAYER;
		}
	}
return NULL;
}


void get_layer_info (Dwg_Data *pdwg, Dwg_Handle plhanle, my_long plabshanle, Dwg_Color *pcolor, Dwg_Object_Ref *pltype, char *layer_on, float *layer_weight) {
    unsigned short idx;
    
    idx = dwg_handle_get_index (pdwg,dwg_handle_absolute(&plhanle,plabshanle));
    if (idx < pdwg->num_objects) {
        if(pdwg->object[idx].type == DWG_TYPE_LAYER) {
		Dwg_Object_LAYER *layer = pdwg->object[idx].tio.object->tio.LAYER;
		if (pcolor) memcpy(pcolor, &layer->color, sizeof(Dwg_Color));
		if (pltype) memcpy(pltype, layer->linetype, sizeof(Dwg_Object_Ref));
		// contains frozen (1 bit), on (2 bit), frozen by default in new viewports (4 bit), locked (8 bit), plotting flag (16 bit), and lineweight (mask with 0x03E0)
		if (layer_on) *layer_on = (char)(layer->values&2)?0:1;
		if (layer_weight) {
			if (layer->values) {
				short f = layer->values;
				short frozen = f & 0x0001; //layer frozen
				short frozen_on_new = ( f>> 1) & 0x0002;//frozen in new
				short locked =  ( f>> 1) & 0x0004;//locked
				short plotF = ( f>> 4) & 0x0001;
				short weight_flag3 = (f >> 5) & 0x03E0;
				short weight_flag2 = (f & 0x03E0) >> 4;
				short weight_flag = (f & 0x03E0) >> 5;
				*layer_weight = (float)(weight_flag+19) / 100.0f;
				}
			}
		}
	}
}


void get_block_info (Dwg_Data *pdwg, Dwg_Object *pobject, unsigned short **out_index, unsigned short *num_out_index) {
    unsigned short idx, idx2, idx3, i;
    
    Dwg_Entity_INSERT *insert = pobject->tio.entity->tio.INSERT;
    
    idx = dwg_handle_get_index (pdwg,dwg_handle_absolute(&insert->block_header->handleref, insert->block_header->absolute_ref));
    if (idx < pdwg->num_objects) {
        if(pdwg->object[idx].type == DWG_TYPE_BLOCK_HEADER) {
            Dwg_Object_BLOCK_HEADER *blockh = pdwg->object[idx].tio.object->tio.BLOCK_HEADER;
            idx2 = dwg_handle_get_index (pdwg,dwg_handle_absolute(&blockh->block_control_handle->handleref, blockh->block_control_handle->absolute_ref));
            if (idx2 < pdwg->num_objects) {
                Dwg_Object_BLOCK_CONTROL *blockctrl = pdwg->object[idx2].tio.object->tio.BLOCK_CONTROL;
                
                if (out_index) {
                    if (out_index[0]) free(out_index[0]);
                    out_index[0] = calloc(1, sizeof(unsigned int) * blockctrl->num_entries+1);
				}
                
                
                for (i=0;i<blockctrl->num_entries;i++) {
                    Dwg_Object_Ref *ref = blockctrl->block_headers[i];
                    
                    if (ref) {
                        if (ref->obj) {
                            Dwg_Object_BLOCK_HEADER *hdr = ref->obj->tio.object->tio.BLOCK_HEADER;
                            Dwg_Object *obj = get_first_owned_object(ref->obj, hdr);
                            while(obj) {
                                /// output_object(obj);
                                obj = get_next_owned_object(ref->obj, obj, hdr);
                                /*
                                 idx3 = dwg_handle_get_index (pdwg,dwg_handle_absolute(&obj->tio.object->handleref, 0));
                                 if (idx3 < pdwg->num_objects) {
                                 if (out_index) { out_index[0][i] = idx3+1; };
                                 }
                                 */
							}
						}
					}
				}
                if (num_out_index) { *num_out_index = blockctrl->num_entries; };
                // idx=idx;
			}
		}
	}
}




// N.B.: In caso di salvataggio, i colori vengono istanziati alterando la caratteristica dinamica di AutoCAD

void set_gl_color (Dwg_Data *pdwg, Dwg_Color *pObjcolor, Dwg_Color *pLayerColor, Dwg_Color *pBlockColor,
                   unsigned char *pRColor, unsigned char *pGColor, unsigned char *pBColor) {
    
    unsigned char RColor = 0, GColor = 0, BColor = 0;
    Dwg_Color *pcolor;


    if (pObjcolor->index != 256 && pObjcolor->index != 0) {
    // Istanziato
        pcolor = pObjcolor;
    } else {
        pcolor = pLayerColor;
        /*
	if (pObjcolor) {
		pObjcolor->index = pLayerColor->index;
		pObjcolor->rgb = pLayerColor->rgb;
		pObjcolor->byte = pLayerColor->byte;
		}
		*/
	}
    
    if (pBlockColor) {
        if (pObjcolor->index == 256 || pObjcolor->index == 0) {
            pcolor = pBlockColor;
        }
    }


    if (pcolor->index != 256 && pcolor->index != 0) {
        if (get_rgb_color (pcolor->index, &RColor, &GColor, &BColor )) {
            if (pRColor && pGColor && pBColor) {
                *pRColor = RColor;
                *pGColor = GColor;
                *pBColor = BColor;
            } else {
#ifdef JNI_DLL
                gluColor3ub(RColor, GColor, BColor);
	#elif defined JNI_SO
                gluColor3ub(RColor, GColor, BColor);
	#else
                gluColor3ub(RColor, GColor, BColor);
#endif
            }
        } else {
            goto handle_rgb_case;
        }
    } else {
        if (get_rgb_color (pcolor->index, &RColor, &GColor, &BColor )) {
#ifdef JNI_DLL
            gluColor3ub(RColor, GColor, BColor);
	#elif defined JNI_SO
            gluColor3ub(RColor, GColor, BColor);
	#else
            gluColor3ub(RColor, GColor, BColor);
#endif
        } else {
            unsigned char b, g, r, a;
        handle_rgb_case:
            if (pcolor->rgb==0) pcolor->rgb = (unsigned int)-1;
            b = RGB_GETRED(pcolor->rgb);
            g = RGB_GETGREEN(pcolor->rgb);
            r = RGB_GETBLUE(pcolor->rgb);
            a = RGB_GETA(pcolor->rgb);
            if (a & 1) {
                if (get_rgb_color (b, &RColor, &GColor, &BColor )) {
                    if (pRColor && pGColor && pBColor) {
                        *pRColor = RColor;
                        *pGColor = GColor;
                        *pBColor = BColor;
                    } else {
#ifdef JNI_DLL
                        gluColor3ub(RColor, GColor, BColor);
	#elif defined JNI_SO
                        gluColor3ub(RColor, GColor, BColor);
	#else
                        gluColor3ub(RColor, GColor, BColor);
#endif
                    }
                } else {
                    goto handle_rgb_case;
                }
            } else {
                if (pRColor && pGColor && pBColor) {
                    *pRColor = RColor;
                    *pGColor = GColor;
                    *pBColor = BColor;
                } else {
#ifdef JNI_DLL
                    gluColor3ub(r,g,b);
	#elif defined JNI_SO
                    gluColor3ub(r,g,b);
	#else
                    gluColor3ub(r,g,b);
#endif
                }
            }
        }
    }
}




#define ROW_SEP	1
#define MDB_CHAR char
#define UINT unsigned int

#ifdef JNI_DLL
    #else
    #define _strnicmp strncasecmp
    #endif



// Non accetta il carattere ' ' come jolly
// Non scambia le stringhe
// Ritorna 0 se le stringhe coincidono
int search_strstr ( MDB_CHAR *__str, MDB_CHAR *__search, int LOCAL_S_CASE )

{	UINT x, y, len1, len2, res = 0, max_scan_value;
	MDB_CHAR old_char;



if (!__str) {
	if (!__search) return 0;
	if (!__search[0]) return 0;
	if (__search[0] == '*' && __search[1] == 0) return 0;
	return 1;
	} else {
	if (!__str[0]) {
		if (!__search) return 0;
		if (!__search[0]) return 0;
		if (__search[0] == '*' && __search[1] == 0) return 0;
		return 1;
		} else {
		if (!__search) return 1;
		if (!__search[0]) return 1;
		if (__search[0] == '*' && __search[1] == 0) return 0;
		}
	}


/* 22-6-2002
if (LOCAL_S_CASE == 1) {
	if (strcmp (__str, __search) == 0) { return 0; }
	} else {
	if (_strcmpi (__str, __search) == 0) { return 0; }
	}
*/




len1 = strlen (__str);
len2 = strlen (__search);


if (__search[len2-1] == '*') {
	// Stringa che inizia per
	old_char = __search[len2-1];
	__search[len2-1] = 0;
	if (LOCAL_S_CASE == 1) {
		res = strncmp  (__str, __search, len2-1);
		__search[len2-1] = old_char;
		return res;
		} else {
		res = _strnicmp (__str, __search, len2-1);
		__search[len2-1] = old_char;
		return res;
		}

	} else if (__search[0] == '*') {

	// Stringa che finisce per
	if (len1 >= len2+1) {
		if (LOCAL_S_CASE == 1) {
			return strncmp ((MDB_CHAR *)&__str[len1-(len2-1)], (MDB_CHAR *)&__search[1], len2-1);
			} else {
			return _strnicmp ((MDB_CHAR *)&__str[len1-(len2-1)], (MDB_CHAR *)&__search[1], len2-1);
			}
		} else {
		return 1;
		}
	}

/*	
if (len1<len2) return 1;
	??-04-2004	8 caratteri
	4-04-2004	7 caratteri
	*/

if (len1<len2) {
	max_scan_value = len1;
	} else {
	max_scan_value = len1-len2+1;
	}

if (__search[0] == 0) res = 1;

for (x=0; x<max_scan_value; x++)  {

	if (__str[x] == ROW_SEP) {
		// Skip Numeri value
		x++;
		while (__str[x] != ROW_SEP && __str[x] != 0) x++;
		if (x >= len1-len2+1) return !res;
		}

	res = 1;

	for (y=0; y<len2; y++)  {

		if (LOCAL_S_CASE == 1) {
			if (__search[y] != __str[x+y] && __search[y] != '?') {
				res = 0;
				break;
				}

			} else {

			if (__search[y] <= 122 && __search[y] >= 97) {
				if (__search[y] != __str[x+y] && __search[y]-32 != __str[x+y] && __search[y] != '?') {
					res = 0;
					break;
					}
				} else if (__search[y] <= 90 && __search[y] >= 65) {
				if (__search[y] != __str[x+y] && __search[y]+32 != __str[x+y] && __search[y] != '?') {
					res = 0;
					break;
					}
				} else {
				if (__search[y] != __str[x+y] && __search[y] != '?') {
					res = 0;
					break;
					}
				}
			}
		}

	if (res == 1) {
		return 0;
		}
	}


return 1;
}
