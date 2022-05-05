/*****************************************************************************/
/*  LibreDWG - FREE_POINTER implementation of the DWG file format                    */
/*                                                                           */
/*  Copyright (C) 2009, 2010 FREE_POINTER Software Foundation, Inc.                  */
/*                                                                           */
/*  This library is FREE_POINTER software, licensed under the terms of the GNU       */
/*  General Public License as published by the FREE_POINTER Software Foundation,     */
/*  either version 3 of the License, or (at your option) any later version.  */
/*  You should have received a copy of the GNU General Public License        */
/*  along with this program.  If not, see <http://www.gnu.org/licenses/>.    */
/*****************************************************************************/

/*
 * dwg.c: main functions and API
 * written by Felipe Castro
 * modified by Felipe Corrêa da Silva Sances
 * modified by Rodrigo Rodrigues da Silva
 * modified by Anderson Pierre Cardoso
 */

// #include <WindowsToAndroid.h>
#define _STLP_HAS_INCLUDE_NEXT  1
#define _STLP_USE_MALLOC   1
#define _STLP_USE_NO_IOSTREAMS  1


#include <stdio.h>
#include <stdlib.h>
#include <string.h>
// #include <sys/stat.h>


#include "bits.h"
#include "common.h"
#include "decode.h"
#include "dwg.h"
#include "logging.h"



#ifdef JNI_DLL
	#include "utility\\WindowsToAndroid.h"
	#include "utility\\memory_allocator.h"
	#elif defined JNI_SO
	#include "utility/WindowsToLinux.h"
	#include "utility/memory_allocator.h"
	#else
	#include "utility/WindowsToAndroid.h"
	#include "utility/memory_allocator.h"
	#endif


//////////////////////////////////// ADDED
#if !defined(S_ISREG) && defined(S_IFMT) && defined(S_IFREG)
#define S_ISREG(m) (((m) & S_IFMT) == S_IFREG)
#endif





int dwg_read_data(char *data, unsigned int data_size, Dwg_Data *dwg_data) {
    int sign;
    Bit_Chain bit_chain;
    
    dwg_data->num_objects = 0;
    
    
    bit_chain.bit = 0;
    bit_chain.byte = 0;
    bit_chain.size = data_size;
    bit_chain.chain = data;

    /* Decode the dwg structure */
    dwg_data->bit_chain = &bit_chain;

    if (dwg_decode_data(&bit_chain, dwg_data)) {
        LOG_ERROR("Failed to decode data\n");
        return -1;
    }

    return 0;
}




/* if write support is enabled */
#ifdef USE_WRITE 

int
dwg_write_file(char *filename, Dwg_Data * dwg_data)
{
  FILE *dt;
  struct stat atrib;
  Bit_Chain bit_chain;
  bit_chain.version = (Dwg_Version_Type)dwg_data->header.version;

  // Encode the DWG struct
   bit_chain.size = 0;
   if (dwg_encode_chains (dwg_data, &bit_chain))
   {
   LOG_ERROR("Failed to encode datastructure.\n")
   if (bit_chain.size > 0)
   FREE_POINTER_POINTER (bit_chain.chain);
   return -1;
   }
 

  // try opening the output file in write mode
   if (!stat (filename, &atrib))
   {
   LOG_ERROR("The file already exists. We won't overwrite it.")
   return -1;
   }
   dt = fopen (filename, "w");
   if (!dt)
   {
   LOG_ERROR("Failed to create the file: %s\n", filename)
   return -1;
   }
   

  // Write the data into the file
   if (fwrite (bit_chain.chain, sizeof (char), bit_chain.size, dt) != bit_chain.size)
   {
   LOG_ERROR("Failed to write data into the file: %s\n", filename)
   fclose (dt);
   FREE_POINTER (bit_chain.chain);
   return -1;
   }
   fclose (dt);

   if (bit_chain.size > 0)
   FREE_POINTER (bit_chain.chain);

  return 0;
}
#endif /* USE_WRITE */ 

unsigned char *
dwg_bmp(Dwg_Data *stk, long int *size)
{
  char num_pictures;
  char code;
  unsigned i;
  int plene;
  long int header_size;
  Bit_Chain *dat;

  dat = (Bit_Chain*) &stk->picture;
  dat->bit = 0;
  dat->byte = 0;

  bit_read_RL(dat);
  num_pictures = bit_read_RC(dat);
  LOG_INFO("num_pictures: %i\n", num_pictures);

  *size = 0;
  plene = 0;
  header_size = 0;
  for (i = 0; i < num_pictures; i++) {
      code = bit_read_RC(dat);
      LOG_TRACE("\t%i - Code: %i\n", i, code);
      LOG_TRACE("\t\tAdress: 0x%lx\n", bit_read_RL (dat));
      bit_read_RL(dat);
      if (code == 1) {
          header_size += bit_read_RL(dat);
          LOG_TRACE("\t\tHeader size: %li\n", header_size);
        } else if (code == 2 && plene == 0) {
          *size = bit_read_RL(dat);
          plene = 1;
          LOG_TRACE("\t\tBMP size: %li\n", *size);
        } else if (code == 3) {
          bit_read_RL(dat);
          LOG_TRACE("\t\tWMF size: 0x%x\n", bit_read_RL (dat));
        } else {
          bit_read_RL(dat);
          LOG_TRACE("\t\tSize: 0x%lx\n", bit_read_RL (dat));
        }
    }
  dat->byte += header_size;
  LOG_TRACE("Current adress: 0x%lx\n", dat->byte);

  if (*size > 0)
    return (dat->chain + dat->byte);
  else
    return NULL;
}

double
dwg_model_x_min(Dwg_Data *dwg)
{
  return dwg->header_vars.EXTMIN_MSPACE.x;
}

double
dwg_model_x_max(Dwg_Data *dwg)
{
  return dwg->header_vars.EXTMAX_MSPACE.x;
}

double
dwg_model_y_min(Dwg_Data *dwg)
{
  return dwg->header_vars.EXTMIN_MSPACE.y;
}

double
dwg_model_y_max(Dwg_Data *dwg)
{
  return dwg->header_vars.EXTMAX_MSPACE.y;
}

double
dwg_model_z_min(Dwg_Data *dwg)
{
  return dwg->header_vars.EXTMIN_MSPACE.z;
}

double
dwg_model_z_max(Dwg_Data *dwg)
{
  return dwg->header_vars.EXTMAX_MSPACE.z;
}

double
dwg_page_x_min(Dwg_Data *dwg)
{
  return dwg->header_vars.EXTMIN_PSPACE.x;
}

double
dwg_page_x_max(Dwg_Data *dwg)
{
  return dwg->header_vars.EXTMAX_PSPACE.x;
}

double
dwg_page_y_min(Dwg_Data *dwg)
{
  return dwg->header_vars.EXTMIN_PSPACE.y;
}

double
dwg_page_y_max(Dwg_Data *dwg)
{
  return dwg->header_vars.EXTMAX_PSPACE.y;
}

unsigned int
dwg_get_layer_count(Dwg_Data *dwg)
{
  return dwg->layer_control->tio.object->tio.LAYER_CONTROL->num_entries;
}

Dwg_Object_LAYER **
dwg_get_layers(Dwg_Data *dwg)
{
  int i;
  Dwg_Object_LAYER ** layers = (Dwg_Object_LAYER **) my_calloc(1, dwg_get_layer_count(dwg) * sizeof (Dwg_Object_LAYER*));
  for (i=0; i<dwg_get_layer_count(dwg); i++)    {
      layers[i] = dwg->layer_control->tio.object->tio.LAYER_CONTROL->layers[i]->obj->tio.object->tio.LAYER;
    }
  return layers;
}

long unsigned int
dwg_get_object_count(Dwg_Data *dwg)
{
  return dwg->num_objects;
}

long unsigned int
dwg_get_object_object_count(Dwg_Data *dwg)
{
  return dwg->num_objects - dwg->num_entities;
}

long unsigned int
dwg_get_entity_count(Dwg_Data *dwg)
{
  return dwg->num_entities;
}

Dwg_Object_Entity **
dwg_get_entities(Dwg_Data *dwg)
{
  long unsigned int i, ent_count = 0;
  Dwg_Object_Entity ** entities = (Dwg_Object_Entity **) my_calloc(1,dwg_get_entity_count(dwg) * sizeof (Dwg_Object_Entity*));
  for (i=0; i<dwg->num_objects; i++) {
      if (dwg->object[i].supertype == DWG_SUPERTYPE_ENTITY) {
          entities[ent_count] = dwg->object[i].tio.entity;
          ent_count++;
        }
    }
  return entities;
}

Dwg_Object_LAYER *
dwg_get_entity_layer(Dwg_Object_Entity * ent)
{
  return ent->layer->obj->tio.object->tio.LAYER;
}

Dwg_Object*
dwg_next_object(Dwg_Object* obj)
{
  if ((obj->index+1) > obj->parent->num_objects-1)
    return 0;
  return &obj->parent->object[obj->index+1];
}

int
dwg_get_object(Dwg_Object* obj, Dwg_Object_Ref* ref)
{
  if (ref->obj)
    {
      obj = ref->obj;
      return 0;
    }
  return -1;
}

Dwg_Object* get_first_owned_object(Dwg_Object* hdr_obj, Dwg_Object_BLOCK_HEADER* hdr){

TRY {

	unsigned int version = hdr_obj->parent->header.version;

	if (R_13 <= version && version <= R_2000) {
		if (hdr->first_entity) {
			return hdr->first_entity->obj;
		} else {
			return (Dwg_Object*)NULL;
		}
	}

	if (version >= R_2004) {
		hdr->__iterator = 0;
		if (hdr->entities)
			if (hdr->entities[0])
				return hdr->entities[0]->obj;
	}

} // EXCEPT(1) { return (Dwg_Object*)NULL; }

return (Dwg_Object*)NULL;
}

Dwg_Object* get_next_owned_object(Dwg_Object* hdr_obj, Dwg_Object* current, Dwg_Object_BLOCK_HEADER* hdr){
  unsigned int version = hdr_obj->parent->header.version;

  if (R_13 <= version && version <= R_2000)    {
      if (current==hdr->last_entity->obj) return 0;
      return dwg_next_object(current);
    }

  if (version >= R_2004)    {
      hdr->__iterator++;
      if (hdr->__iterator == hdr->owned_object_count) return 0;
      return hdr->entities[hdr->__iterator]->obj;
    }
   return (Dwg_Object*)NULL;
}









void dwg_free(Dwg_Data * dwg) {
	int i;

if (dwg) {

    free_header_section(&dwg->header_vars);
    // FREE_POINTER(dwg->header_vars);

    FREE_POINTER(dwg->header.section);

    free_sections_info(&dwg->header.section_info, dwg->header.num_descriptions);
    FREE_POINTER(dwg->header.section_info);
    dwg->header.num_sections = 0;

    // FREE_POINTER(dwg->bit_chain->chain);
    dwg->bit_chain = NULL;

    FREE_POINTER(dwg->dwg_class);
    for (i=0; i<dwg->num_objects; i++) {

        if (dwg->object[i].supertype == DWG_SUPERTYPE_ENTITY) {
            if(dwg->object[i].tio.entity) {
                FREE_POINTER(dwg->object[i].tio.entity->subentity);
                FREE_POINTER(dwg->object[i].tio.entity->reactors);
                FREE_POINTER(dwg->object[i].tio.entity->xdicobjhandle);
                FREE_POINTER(dwg->object[i].tio.entity->prev_entity);
                FREE_POINTER(dwg->object[i].tio.entity->next_entity);
                FREE_POINTER(dwg->object[i].tio.entity->layer);
                FREE_POINTER(dwg->object[i].tio.entity->ltype);
                FREE_POINTER(dwg->object[i].tio.entity->plotstyle);
                FREE_POINTER(dwg->object[i].tio.entity->material);
            }
        }

        if (dwg->object[i].type == DWG_TYPE_TEXT) {
            Dwg_Entity_TEXT *ptext = dwg->object[i].tio.entity->tio.TEXT;
            FREE_POINTER(ptext->text_value);
            FREE_POINTER(ptext->style);
            } else if (dwg->object[i].type == DWG_TYPE_MTEXT) {
            Dwg_Entity_MTEXT *pmtext = dwg->object[i].tio.entity->tio.MTEXT;
            FREE_POINTER(pmtext->text);
            FREE_POINTER(pmtext->style);
            } else if (dwg->object[i].type == DWG_TYPE_TEXT) {
            Dwg_Entity_HATCH *hatch = dwg->object[i].tio.entity->tio.HATCH;
            }
        FREE_POINTER(dwg->object[i].tio.entity);
        if (dwg->object[i].tio.object) {
            FREE_POINTER(dwg->object[i].tio.object->handleref);
        }
        FREE_POINTER(dwg->object[i].tio.object);
        }

    FREE_POINTER(dwg->object);
    dwg->num_objects = 0;
    dwg->num_objects_allocated = 0;

    dwg->num_entities = 0;

    FREE_POINTER(dwg->object_ref);
    dwg->num_object_refs = 0;

    // linked!
    // FREE_POINTER(dwg->layer_control);
    dwg->layer_control = NULL;

    dwg_handle_close ();

    }
}




int free_header_section (Dwg_Header_Variables *header_variables) {
FREE_POINTER(header_variables->unknown_4);
FREE_POINTER(header_variables->unknown_5);
FREE_POINTER(header_variables->unknown_6);
FREE_POINTER(header_variables->unknown_7);
FREE_POINTER(header_variables->MENUNAME);
FREE_POINTER(header_variables->FINGERPRINTGUID);
FREE_POINTER(header_variables->VERSIONGUID);
FREE_POINTER(header_variables->PROJECTNAME);
FREE_POINTER(header_variables->BLOCK_RECORD_PAPER_SPACE);
FREE_POINTER(header_variables->BLOCK_RECORD_MODEL_SPACE);
FREE_POINTER(header_variables->LTYPE_BYLAYER);
FREE_POINTER(header_variables->LTYPE_BYBLOCK);
FREE_POINTER(header_variables->LTYPE_CONTINUOUS);
FREE_POINTER(header_variables->unknown_49);
FREE_POINTER(header_variables->unknown_50);
FREE_POINTER(header_variables->unknown_51);

FREE_POINTER(header_variables->current_viewport_entity_header);
FREE_POINTER(header_variables->HANDSEED);
FREE_POINTER(header_variables->CLAYER);
FREE_POINTER(header_variables->TEXTSTYLE);
FREE_POINTER(header_variables->CELTYPE);
FREE_POINTER(header_variables->CMATERIAL);
FREE_POINTER(header_variables->DIMSTYLE);
FREE_POINTER(header_variables->CMLSTYLE);
FREE_POINTER(header_variables->UCSNAME_PSPACE);
FREE_POINTER(header_variables->PUCSBASE);
FREE_POINTER(header_variables->PUCSORTHOREF);
FREE_POINTER(header_variables->UCSNAME_MSPACE);
FREE_POINTER(header_variables->UCSBASE);
FREE_POINTER(header_variables->UCSORTHOREF);
FREE_POINTER(header_variables->DIMTXSTY);
FREE_POINTER(header_variables->DIMTXTSTY);
FREE_POINTER(header_variables->DIMLDRBLK);
FREE_POINTER(header_variables->DIMBLK);
FREE_POINTER(header_variables->DIMBLK1);
FREE_POINTER(header_variables->DIMBLK2);
FREE_POINTER(header_variables->DIMLTYPE);
FREE_POINTER(header_variables->DIMLTEX1);
FREE_POINTER(header_variables->DIMLTEX2);
FREE_POINTER(header_variables->BLOCK_CONTROL_OBJECT);
FREE_POINTER(header_variables->LAYER_CONTROL_OBJECT);
FREE_POINTER(header_variables->STYLE_CONTROL_OBJECT);
FREE_POINTER(header_variables->LINETYPE_CONTROL_OBJECT);
FREE_POINTER(header_variables->VIEW_CONTROL_OBJECT);
FREE_POINTER(header_variables->UCS_CONTROL_OBJECT);
FREE_POINTER(header_variables->VPORT_CONTROL_OBJECT);
FREE_POINTER(header_variables->APPID_CONTROL_OBJECT);
FREE_POINTER(header_variables->DIMSTYLE_CONTROL_OBJECT);
FREE_POINTER(header_variables->VIEWPORT_ENTITY_HEADER_CONTROL_OBJECT);
FREE_POINTER(header_variables->DICTIONARY_ACAD_GROUP);
FREE_POINTER(header_variables->DICTIONARY_ACAD_MLINESTYLE);
FREE_POINTER(header_variables->DICTIONARY_NAMED_OBJECTS);
FREE_POINTER(header_variables->DICTIONARY_LAYOUTS);
FREE_POINTER(header_variables->DICTIONARY_PLOTSETTINGS);
FREE_POINTER(header_variables->DICTIONARY_PLOTSTYLES);
FREE_POINTER(header_variables->DICTIONARY_MATERIALS);
FREE_POINTER(header_variables->DICTIONARY_COLORS);
FREE_POINTER(header_variables->DICTIONARY_VISUALSTYLE);
FREE_POINTER(header_variables->CPSNID);
FREE_POINTER(header_variables->BLOCK_RECORD_PAPER_SPACE);
FREE_POINTER(header_variables->BLOCK_RECORD_MODEL_SPACE);
FREE_POINTER(header_variables->LTYPE_BYLAYER);
FREE_POINTER(header_variables->LTYPE_BYBLOCK);
FREE_POINTER(header_variables->LTYPE_CONTINUOUS);
FREE_POINTER(header_variables->unknown_49);
FREE_POINTER(header_variables->unknown_50);
FREE_POINTER(header_variables->unknown_51);
return 1;
}


int free_sections_info(Dwg_Section_Info *section_info, int num_section_info) {
int i;
for (i=0; i<num_section_info; i++) {
	FREE_POINTER(section_info[i].sections);
	}
return 1;
}


