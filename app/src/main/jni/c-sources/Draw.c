//
//  Draw.m
//
//  Created by cristian andreon on 31/03/11.
//  Copyright 2011 CA. All rights reserved.
//
// OTTIMIZZAZIONE DA FARE : cache dei colori BYLAYER
//


///////////////////////////////////////////
// Disabilitazioni in fase di transizione
//
// #define DISABLE_LWPLINE_ON_DRAG
// #define DISABLE_INSERT_ON_DRAG
// #define DISABLE_ARC_ON_DRAG
// #define DISABLE_CIRCLE_ON_DRAG
// #define DISABLE_LINE_ON_DRAG

int GL_DISABLE_HATCH_ON_DRAG = 0;
int GL_DISABLE_TEXT_ON_DRAG = 0;

extern float GLCanvasVersion;



#define DISABLE_HATCH_ON_DRAG
#define DISABLE_TEXT_ON_DRAG
// #define DISABLE_TEXT_ON_DRAG




///////////////////////////////////////////
// Disabilitazioni Features OpenGL
//
#define USE_LIGHT0
#define USE_BLEND
#define USE_COLOR_MATERIAL

#define NORMAL_WIDTH 1.0f



#define EXTERN

#ifdef JNI_DLL
#include <windows.h>
#include "time.h"
#include <jni-gl.h>
#include "utility/WindowsToAndroid.h"
#include "utility/OpenGLWrapper.h"
#include "c-sources/types.h"
#include "c-sources/matrix.h"
#include "spatialFilter/spatialFilter.h"
#elif defined JNI_SO
#include "time.h"
#include "utility/WindowsToLinux.h"
#include "utility/OpenGLWrapper.h"
#include "c-sources/types.h"
#include "c-sources/matrix.h"
#include "spatialFilter/spatialFilter.h"

#include <setjmp.h>

#define TRY do{ jmp_buf ex_buf__; if( !setjmp(ex_buf__) ){
#define CATCH } else {
#define ETRY } }while(0)
#define THROW longjmp(ex_buf__, 1)

#else
#include "time.h"
#include <GLES/gl.h>
#include "utility/WindowsToAndroid.h"
#include "utility/OpenGLWrapper.h"
#include "spatialFilter/spatialFilter.h"
#endif

#include "c-sources/Draw.h"
#include "c-sources/Canvas.h"

#include "lib-dwg-master/dwg.h"
#include "lib-dwg-master/logging.h"






/////////////////////////////////////////////////
// Trasformazioni matriciali sotto Linux
//
#if defined(JNI_DLL) || defined(JNI_SO)
#define MAX_MATRIX  32
Lib3dsMatrix GLMatrix = {0}, GLMatrixArray[MAX_MATRIX] = {0};
unsigned int GLCurMatrix = 0;
#endif















float GLLightM = 0.5f, GLLightA = 0.4f, GLLightD = 0.7f, GLLightS = 0.99f;
float GLLightPosX = -0.11, GLLightPosY = +0.11, GLLightPosZ = +0.91, GLLightPosA = 0.110;

float GLCameraX = 0.0f, GLCameraY = 0.0f, GLCameraWX = 100.0, GLCameraWY = 100.0, GLCameraZ = 1.0f;
float GLCt = 0.0f;


int GLDL = -1;
int GLDebug = 0;
int GLBlockOpen = 0;




// Funzioni locali
void onDrawText(unsigned char *text, unsigned char *layer,
        int horiz_alignment, int vert_alignment,
        float x, float y, float ht, float ang,
        unsigned char r, unsigned char g, unsigned char b, unsigned char a,
        unsigned char isMText, int Options, int SearchMode);

void draw_dwg_object(Dwg_Data *pdwg, Dwg_Object *pdwg_object, Dwg_Color *pBlockColor, int Mode) {


    if (pdwg_object->supertype == DWG_SUPERTYPE_UNKNOWN) {
        if (!(Mode & 1)) {
            return;
        }
    }

    if (GLBlockOpen) {
        if (pdwg_object->type == DWG_TYPE_ENDBLK) {
            GLBlockOpen = 0;
        }
        return;
    }


    /* CRASH ANYWAY!!!
    wait(5);
    return;
     */


    switch (pdwg_object->type) {

        case DWG_TYPE_LINE:
        {
            if (GLOnClick) {
            } else if (GLSearchText) {
            } else {
                Dwg_Color pLayerColor;
                Dwg_Object_Ref pltype;
                BOOL bRestoreWidth = FALSE;
                char layer_on = 0;
                float layer_weight = 0.0f;

#ifdef DISABLE_LINE_ON_DRAG
                if (GLdrawOptions & 1) return 0;
#endif

                get_layer_info(pdwg, pdwg_object->tio.entity->layer->handleref,
                        pdwg_object->tio.entity->layer->absolute_ref, &pLayerColor, &pltype,
                        &layer_on, &layer_weight);

                if (layer_on) {
                    if (pdwg_object->tio.entity->lineweight > 7 &&
                            pdwg_object->tio.entity->lineweight < 29) {
                        bRestoreWidth = TRUE;
                        gluLineWidth((float) (3.0f + (int) (pdwg_object->tio.entity->lineweight - 8) / 2));
                    } else if (pdwg_object->tio.entity->lineweight == 29) {
                        // by layer
                        gluLineWidth(layer_weight * 10.0f);
                    } else {
                        gluLineWidth(NORMAL_WIDTH);
                    }

                    gluBegin(GL_LINES, 2);

#if defined(JNI_DLL) || defined(JNI_SO)
                    {
                        float x = pdwg_object->tio.entity->tio.LINE->start.x;
                        float y = pdwg_object->tio.entity->tio.LINE->start.y;
                        float z = 0.0f;
                        if (GLCurMatrix) lib3ds_matrix_trasnform(GLMatrix, &x, &y, &z);
                        gluVertex3d(x, y, 0.0f);
                    }
#else
                    gluVertex3d(pdwg_object->tio.entity->tio.LINE->start.x,
                            pdwg_object->tio.entity->tio.LINE->start.y, 0.0f);
#endif
                    set_gl_color(pdwg, &pdwg_object->tio.entity->color, &pLayerColor, pBlockColor,
                            NULL, NULL, NULL);


#if defined(JNI_DLL) || defined(JNI_SO)
                    {
                        float x = pdwg_object->tio.entity->tio.LINE->end.x;
                        float y = pdwg_object->tio.entity->tio.LINE->end.y;
                        float z = 0.0f;
                        if (GLCurMatrix) lib3ds_matrix_trasnform(GLMatrix, &x, &y, &z);
                        gluVertex3d(x, y, 0.0f);
                    }
#else
                    gluVertex3d(pdwg_object->tio.entity->tio.LINE->end.x,
                            pdwg_object->tio.entity->tio.LINE->end.y, 0.0f);
#endif
                    set_gl_color(pdwg, &pdwg_object->tio.entity->color, &pLayerColor, pBlockColor,
                            NULL, NULL, NULL);

                    gluEnd();

                    if (bRestoreWidth) {
                        gluLineWidth(NORMAL_WIDTH);
                    }
                }
            }
            break;
        }

        case DWG_TYPE_CIRCLE:
        {
            if (GLOnClick) {
            } else if (GLSearchText) {
            } else {
                Dwg_Color pLayerColor;
                Dwg_Object_Ref pltype;
                unsigned char r, g, b, a;
                BOOL bRestoreWidth = FALSE;
                char layer_on = 0;
                float layer_weight = 0.0f;

#ifdef DISABLE_CIRCLE_ON_DRAG
                if (GLdrawOptions & 1) return 0;
#endif

                get_layer_info(pdwg, pdwg_object->tio.entity->layer->handleref,
                        pdwg_object->tio.entity->layer->absolute_ref, &pLayerColor, &pltype,
                        &layer_on, &layer_weight);

                if (layer_on) {

                    set_gl_color(pdwg, &pdwg_object->tio.entity->color, &pLayerColor, pBlockColor,
                            &r, &g, &b);

                    gluColor4ub(r, g, b, 255);

#if defined(JNI_DLL) || defined(JNI_SO)
#else
                    if (pdwg_object->tio.entity->lineweight > 7 &&
                            pdwg_object->tio.entity->lineweight < 29) {
                        bRestoreWidth = TRUE;
                        gluLineWidth((float) (3.0f + (int) (pdwg_object->tio.entity->lineweight - 8) / 2));
                    } else if (pdwg_object->tio.entity->lineweight == 29) {
                        // by layer
                        gluLineWidth(layer_weight * 10.0f);
                    } else {
                        gluLineWidth(NORMAL_WIDTH);
                    }
#endif

#if defined(JNI_DLL) || defined(JNI_SO)
                    {
                        float x = (float) pdwg_object->tio.entity->tio.CIRCLE->center.x;
                        float y = (float) pdwg_object->tio.entity->tio.CIRCLE->center.y;
                        float z = 0.0f;
                        float rad = (float) pdwg_object->tio.entity->tio.CIRCLE->radius;
                        if (GLCurMatrix) {
                            lib3ds_matrix_trasnform(GLMatrix, &x, &y, &z);
                            rad *= GLMatrix[0][0];
                        }
                        OGL_DRAW_CIRCLE(x, y, rad, r, g, b);
                    }
#else
                    OGL_DRAW_CIRCLE((float) pdwg_object->tio.entity->tio.CIRCLE->center.x,
                            (float) pdwg_object->tio.entity->tio.CIRCLE->center.y,
                            pdwg_object->tio.entity->tio.CIRCLE->radius, r, g, b);
#endif

#if defined(JNI_DLL) || defined(JNI_SO)
#else
                    if (bRestoreWidth) {
                        gluLineWidth(NORMAL_WIDTH);
                    }
#endif
                }
            }
            break;
        }

        case DWG_TYPE_ARC:
        {
            if (GLOnClick) {
            } else if (GLSearchText) {
            } else {
                Dwg_Entity_ARC *parc = pdwg_object->tio.entity->tio.ARC;
                Dwg_Color pLayerColor;
                Dwg_Object_Ref pltype;
                unsigned char r, g, b, a;
                BOOL bRestoreWidth = FALSE;
                char layer_on = 0;
                float layer_weight = 0.0f;

                if (parc->extrusion.z < 0.0f) {
#if defined(JNI_DLL) || defined(JNI_SO)
                    {
                        Lib3dsMatrix curMatrix;
                        ////////////////////////////////////
                        // Salva la matrice corrente
                        //
                        lib3ds_matrix_copy(GLMatrixArray[GLCurMatrix], GLMatrix);
                        if (GLCurMatrix < MAX_MATRIX) GLCurMatrix++;

                        // Applica la matrice
                        lib3ds_matrix_identity(curMatrix);
                        lib3ds_matrix_create(curMatrix, (float) 0.0f, (float) 0.0f, 0.0f, (float) - 1.0f, (float) 1.0f, (float) 1.0f, 0.0f, 0.0f, (float) 0.0f);
                        lib3ds_matrix_mult(GLMatrix, curMatrix);
                    }
#else
                    glPushMatrix();
                    glScalef((float) - 1.0f, (float) 1.0f, (float) 1.0f);
#endif
                }

#ifdef DISABLE_ARC_ON_DRAG
                if (GLdrawOptions & 1) return 0;
#endif

                get_layer_info(pdwg, pdwg_object->tio.entity->layer->handleref,
                        pdwg_object->tio.entity->layer->absolute_ref, &pLayerColor, &pltype,
                        &layer_on, &layer_weight);

                if (layer_on) {

                    set_gl_color(pdwg, &pdwg_object->tio.entity->color, &pLayerColor, pBlockColor, &r, &g, &b);



#if defined(JNI_DLL) || defined(JNI_SO)
                    gluColor4ub(r, g, b, 255);
#else
                    glColor4ub(r, g, b, 255);
#endif



#if defined(JNI_DLL) || defined(JNI_SO)
                    {
                        float x = (float) parc->center.x;
                        float y = (float) parc->center.y;
                        float z = 0.0f;
                        float radius = (float) parc->radius;
                        if (GLCurMatrix) {
                            lib3ds_matrix_trasnform(GLMatrix, &x, &y, &z);
                            radius *= GLMatrix[0][0];
                            radius = radius < 0.0f ? radius * -1.0 : radius;
                        }
                        OGL_DRAW_ARC((float) x, (float) y, (float) radius, (float) parc->start_angle, (float) parc->end_angle, r, g, b);
                    }
#else
                    if (pdwg_object->tio.entity->lineweight > 7 &&
                            pdwg_object->tio.entity->lineweight < 29) {
                        bRestoreWidth = TRUE;
                        gluLineWidth((float) (3.0f + (int) (pdwg_object->tio.entity->lineweight - 8) / 2));
                    } else if (pdwg_object->tio.entity->lineweight == 29) {
                        // by layer
                        gluLineWidth(layer_weight * 10.0f);
                    } else {
                        gluLineWidth(NORMAL_WIDTH);
                    }
#endif



#if defined(JNI_DLL) || defined(JNI_SO)
#else
                    OGL_DRAW_ARC((float) parc->center.x, (float) parc->center.y, (float) parc->radius, (float) parc->start_angle, (float) parc->end_angle, r, g, b);
#endif


#if defined(JNI_DLL) || defined(JNI_SO)
#else
                    if (bRestoreWidth) {
                        gluLineWidth(NORMAL_WIDTH);
                    }
#endif


                    if (parc->extrusion.z < 0.0f) {
#if defined(JNI_DLL) || defined(JNI_SO)
                        // Ripristina la matrice precedente
                        if (GLCurMatrix) {
                            GLCurMatrix--;
                            lib3ds_matrix_copy(GLMatrix, GLMatrixArray[GLCurMatrix]);
                        }
#else
                        glPopMatrix();
#endif
                    }

                }
            }
            break;
        }


        case DWG_TYPE_TEXT:
            print_dwg_text(pdwg, pdwg_object, pBlockColor, 0 + 0);
            break;

        case DWG_TYPE_MTEXT:
            print_dwg_mtext(pdwg, pdwg_object, pBlockColor, 0 + 0);
            break;

        case DWG_TYPE_INSERT:
        {
            // if (GLDebug) my_printf("INSERT");
            print_dwg_insert(pdwg, pdwg_object, pBlockColor, 0 + 0);
            break;
        }

        case DWG_TYPE_HATCH:
            // if (GLDebug) my_printf("HATCH");
            print_dwg_hatch(pdwg, pdwg_object, pBlockColor, 0 + 0);
            break;

        case DWG_TYPE_LWPLINE:
        {
            print_dwg_lwpline(pdwg, pdwg_object, pBlockColor, 0 + 0);
            break;
        }


        case DWG_TYPE_BLOCK:
            // if (GLDebug) my_printf("BLOCK");
            GLBlockOpen = 1;
            break;
        case DWG_TYPE_ENDBLK:
            // if (GLDebug) my_printf("ENDBLOCK");
            GLBlockOpen = 0;
            break;

        case DWG_TYPE_BLOCK_CONTROL:
            // if (GLDebug) my_printf("BLOCK_CONTROL");
            break;
        case DWG_TYPE_BLOCK_HEADER:
            // if (GLDebug) my_printf("BLOCK_HEADER");
            break;


        case DWG_TYPE_POINT:
            break;
        case DWG_TYPE_SOLID:
            break;


        case DWG_TYPE_ELLIPSE:
        {
            if (GLOnClick) {
            } else if (GLSearchText) {
            } else {
                Dwg_Color pLayerColor;
                Dwg_Object_Ref pltype;
                Dwg_Entity_ELLIPSE *pellipse = pdwg_object->tio.entity->tio.ELLIPSE;
                unsigned char r, g, b, a;
                BOOL bRestoreWidth = FALSE;
                char layer_on = 0;
                float s_ang = (float) pellipse->start_angle;
                float e_ang = (float) pellipse->end_angle;
                float layer_weight = 0.0f;

                BITCODE_3BD sm_axis = pellipse->sm_axis;
                BITCODE_BD rad_x = sm_axis.x;
                BITCODE_BD rad_y = rad_x * (float) pellipse->axis_ratio; // sm_axis.y; // 

                rad_x = rad_x < 0.0 ? rad_x * -1.0 : rad_x; // (double)fabs((double)rad_x);
                rad_y = rad_y < 0.0 ? rad_y * -1.0 : rad_x; // rad_y = (double)fabs((double)rad_y);


                if (pellipse->extrusion.z < 0.0f) {
#if defined(JNI_DLL) || defined(JNI_SO)
                    {
                        Lib3dsMatrix curMatrix;
                        ////////////////////////////////////
                        // Salva la matrice corrente
                        //
                        lib3ds_matrix_copy(GLMatrixArray[GLCurMatrix], GLMatrix);
                        if (GLCurMatrix < MAX_MATRIX) GLCurMatrix++;

                        // Applica la matrice
                        lib3ds_matrix_identity(curMatrix);
                        lib3ds_matrix_create(curMatrix, (float) 0.0f, (float) 0.0f, 0.0f, (float) - 1.0f, (float) 1.0f, (float) 1.0f, 0.0f, 0.0f, (float) 0.0f);
                        lib3ds_matrix_mult(GLMatrix, curMatrix);
                    }
#else
                    glPushMatrix();
                    glScalef((float) - 1.0f, (float) 1.0f, (float) 1.0f);
#endif
                }



                get_layer_info(pdwg, pdwg_object->tio.entity->layer->handleref,
                        pdwg_object->tio.entity->layer->absolute_ref, &pLayerColor, &pltype,
                        &layer_on, &layer_weight);

                if (layer_on) {
                    set_gl_color(pdwg, &pdwg_object->tio.entity->color, &pLayerColor, pBlockColor, &r, &g, &b);


#if defined(JNI_DLL) || defined(JNI_SO)
                    gluColor4ub(r, g, b, 255);
#else
                    glColor4ub(r, g, b, 255);
#endif

#if defined(JNI_DLL) || defined(JNI_SO)
#else
                    if (pdwg_object->tio.entity->lineweight > 7 &&
                            pdwg_object->tio.entity->lineweight < 29) {
                        bRestoreWidth = TRUE;
                        gluLineWidth((float) (3.0f + (int) (pdwg_object->tio.entity->lineweight - 8) / 2));
                    } else if (pdwg_object->tio.entity->lineweight == 29) {
                        // by layer
                        gluLineWidth(layer_weight * 10.0f);
                    } else {
                        gluLineWidth(NORMAL_WIDTH);
                    }
#endif

#if defined(JNI_DLL) || defined(JNI_SO)
                    {
                        float x = (float) pellipse->center.x;
                        float y = (float) pellipse->center.y;
                        float z = 0.0f;
                        if (GLCurMatrix) {
                            lib3ds_matrix_trasnform(GLMatrix, &x, &y, &z);
                            rad_x *= GLMatrix[0][0];
                            rad_y *= GLMatrix[0][0];
                        }
                        OGL_DRAW_ELLIPSE(x, y, (float) rad_x, (float) rad_y, (float) s_ang, (float) e_ang, r, g, b);
                    }
#else
                    OGL_DRAW_ELLIPSE((float) pdwg_object->tio.entity->tio.ELLIPSE->center.x, (float) pdwg_object->tio.entity->tio.ELLIPSE->center.y, (float) rad_x, (float) rad_y, (float) s_ang, (float) e_ang, r, g, b);
#endif

#if defined(JNI_DLL) || defined(JNI_SO)
#else
                    if (bRestoreWidth) {
                        gluLineWidth(NORMAL_WIDTH);
                    }
#endif

                    if (pellipse->extrusion.z < 0.0f) {
#if defined(JNI_DLL) || defined(JNI_SO)
                        // Ripristina la matrice precedente
                        if (GLCurMatrix) {
                            GLCurMatrix--;
                            lib3ds_matrix_copy(GLMatrix, GLMatrixArray[GLCurMatrix]);
                        }
#else
                        glPopMatrix();
#endif
                    }
                }
            }
            break;
        }




        case DWG_TYPE_SPLINE:
            break;
        case DWG_TYPE_POLYLINE_3D:
            break;
        case DWG_TYPE_LAYOUT:
            break;
        case DWG_TYPE_POLYLINE_2D:
            break;
        case DWG_TYPE_DIMENSION_ORDINATE:
            break;
        case DWG_TYPE_DIMENSION_LINEAR:
            break;
        case DWG_TYPE_VIEW:
            break;
        case DWG_TYPE_LEADER:
            break;
        case DWG_TYPE_LAYER:
            break;
        case DWG_TYPE_ATTRIB:
            break;
        case DWG_TYPE_ATTDEF:
            break;
        case DWG_TYPE_MINSERT:
            break;

        case DWG_TYPE_SEQEND:
            break;

        case DWG_TYPE_RAY:
            break;
        case DWG_TYPE_XLINE:
            break;
        case DWG_TYPE_VIEWPORT:
            break;
        case DWG_TYPE_VIEW_CONTROL:
            break;
        case DWG_TYPE_DIMSTYLE:
            break;
        case DWG_TYPE_DIMSTYLE_CONTROL:
            break;


        case DWG_TYPE_UNUSED:
            break;

            //case DWG_TYPE_<UNKNOWN> = 0x09,
            //case DWG_TYPE_<UNKNOWN> = 0x2b,

        case DWG_TYPE_VERTEX_2D:
        case DWG_TYPE_VERTEX_3D:
        case DWG_TYPE_VERTEX_MESH:
        case DWG_TYPE_VERTEX_PFACE:
        case DWG_TYPE_VERTEX_PFACE_FACE:
        case DWG_TYPE_XRECORD:
        case DWG_TYPE_DICTIONARY:
            break;


        case DWG_TYPE_DIMENSION_ALIGNED:
        case DWG_TYPE_DIMENSION_ANG3PT:
        case DWG_TYPE_DIMENSION_ANG2LN:
        case DWG_TYPE_DIMENSION_RADIUS:
        case DWG_TYPE_DIMENSION_DIAMETER:
        case DWG_TYPE__3DFACE:
        case DWG_TYPE_POLYLINE_PFACE:
        case DWG_TYPE_POLYLINE_MESH:
        case DWG_TYPE_TRACE:
        case DWG_TYPE_SHAPE:
        case DWG_TYPE_REGION:
        case DWG_TYPE_3DSOLID:
        case DWG_TYPE_BODY:
        case DWG_TYPE_TOLERANCE:
        case DWG_TYPE_MLINE:
        case DWG_TYPE_LAYER_CONTROL:
        case DWG_TYPE_SHAPEFILE_CONTROL:
        case DWG_TYPE_SHAPEFILE:
            //case DWG_TYPE_<UNKNOWN> = 0x36,
            //case DWG_TYPE_<UNKNOWN> = 0x37,
        case DWG_TYPE_LTYPE_CONTROL:
        case DWG_TYPE_LTYPE:
            //case DWG_TYPE_<UNKNOWN> = 0x3a,
            //case DWG_TYPE_<UNKNOWN> = 0x3b,
        case DWG_TYPE_UCS_CONTROL:
        case DWG_TYPE_UCS:
        case DWG_TYPE_VPORT_CONTROL:
        case DWG_TYPE_VPORT:
        case DWG_TYPE_APPID_CONTROL:
        case DWG_TYPE_APPID:
        case DWG_TYPE_VP_ENT_HDR_CONTROL:
        case DWG_TYPE_VP_ENT_HDR:
        case DWG_TYPE_GROUP:
        case DWG_TYPE_MLINESTYLE:
            //case DWG_TYPE_<UNKNOWN> = 0x4a
            //case DWG_TYPE_<UNKNOWN> = 0x4b
            //case DWG_TYPE_<UNKNOWN> = 0x4c
        case DWG_TYPE_PLACEHOLDER:
            break;
            //case DWG_TYPE_<UNKNOWN> = 0x51,

        default:
            // sprintf(str, "UNKNOWN:%d - ", (int)pdwg_object->type); printf(str);
            break;
    }

}

int print_dwg_insert(Dwg_Data *pdwg, Dwg_Object *pobject, Dwg_Color *pBlockColor, int Mode) {
    Dwg_Entity_INSERT *insert = pobject->tio.entity->tio.INSERT;
    Dwg_Handle *phanle = &pobject->tio.entity->layer->obj->handle;
    Dwg_Color *pColor = &pobject->tio.entity->color;

    char layer_on = 0;
    unsigned short idx, idx2;
    unsigned int i;
    float layer_weight = 0.0f;


#ifdef DISABLE_INSERT_ON_DRAG
    if (GLdrawOptions & 1) return 0;
#endif


#if defined(JNI_DLL) || defined(JNI_SO)
    {
        Lib3dsMatrix curMatrix;
        ////////////////////////////////////
        // Salva la matrice corrente
        //
        lib3ds_matrix_copy(GLMatrixArray[GLCurMatrix], GLMatrix);
        if (GLCurMatrix < MAX_MATRIX) GLCurMatrix++;

        // Applica la matrice
        lib3ds_matrix_identity(curMatrix);
        lib3ds_matrix_create(curMatrix, (float) insert->ins_pt.x, (float) insert->ins_pt.y, 0.0f, (float) insert->scale.x, (float) insert->scale.y, (float) insert->scale.z, 0.0f, 0.0f, (float) insert->rotation_ang);
        lib3ds_matrix_mult(GLMatrix, curMatrix);
    }
#else

    // get_block_info (pdwg, pobject, &out_index, &num_out_index);
    glPushMatrix();

    glTranslatef((float) insert->ins_pt.x, (float) insert->ins_pt.y, 0.0f);
    glRotatef((float) insert->rotation_ang * 57.295779f, 0.0f, 0.0f, 1.0f);
    glScalef((float) insert->scale.x, (float) insert->scale.y, (float) insert->scale.z);

#endif



    get_layer_info(pdwg, pobject->tio.entity->layer->handleref, pobject->tio.entity->layer->absolute_ref, pColor, NULL, &layer_on, &layer_weight);

    if (layer_on) {

        idx = dwg_handle_get_index(pdwg, dwg_handle_absolute(&insert->block_header->handleref, insert->block_header->absolute_ref));
        if (idx < pdwg->num_objects) {
            if (pdwg->object[idx].type == DWG_TYPE_BLOCK_HEADER) {
                Dwg_Object_BLOCK_HEADER *block_header = pdwg->object[idx].tio.object->tio.BLOCK_HEADER;

#ifdef WIN32
                {
                    char str[512];
                    sprintf(str, "Insert:%s - %0.3f.%0.3f rot:%0.3f	scale:%0.3f\n", block_header->entry_name, insert->ins_pt.x, insert->ins_pt.y, insert->rotation_ang, insert->scale.x);
                    printf(str);
                }
#endif

                if (GLDebug) {
                    if (insert->has_attribs) {
                        my_printf("Block with attr.:%s", block_header->entry_name);
                    } else {
                        my_printf("Block :%s", block_header->entry_name);
                    }
                }

                idx2 = dwg_handle_get_index(pdwg, dwg_handle_absolute(&block_header->block_control_handle->handleref, block_header->block_control_handle->absolute_ref));
                if (idx2 < pdwg->num_objects) {
                    Dwg_Object_BLOCK_CONTROL *block_control = pdwg->object[idx2].tio.object->tio.BLOCK_CONTROL;

                    for (i = 0; i < block_control->num_entries; i++) {
                        Dwg_Object_Ref *ref = block_control->block_headers[i];

#ifdef WIN32
                        {
                            char str[512];
                            sprintf(str, "\r\tHandle:%d - %d\n\r", i, block_control->block_headers[i]);
                            printf(str);
                        }
#endif
                        if (ref) {
                            if (ref->obj) {
                                // Dwg_Object_BLOCK_HEADER *hdr = ref->obj->tio.object->tio.BLOCK_HEADER;
                                // Dwg_Object_BLOCK_HEADER *hdr = ref->obj->tio.object->tio.BLOCK_HEADER;
                                Dwg_Object *obj = get_first_owned_object(ref->obj, block_header);
                                int startIndex = pobject->index;
                                while (obj) {
                                    /// output_object(obj);
                                    if (obj) {
#ifdef WIN32

                                        printf("\t\t");
#endif

                                        if (obj->index == 2192) {
                                            int db = 1;
                                        }

                                        obj->supertype = DWG_SUPERTYPE_UNKNOWN;
                                        draw_dwg_object(pdwg, obj, (pBlockColor ? pBlockColor : pColor), 0 + 1);
                                        obj = get_next_owned_object(ref->obj, obj, block_header);

                                        // dead loop ?
                                        if (obj)
                                            if (startIndex == obj->index)
                                                break;

                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }


#if defined(JNI_DLL) || defined(JNI_SO)
    // Ripristina la matrice precedente
    if (GLCurMatrix) {
        GLCurMatrix--;
        lib3ds_matrix_copy(GLMatrix, GLMatrixArray[GLCurMatrix]);
    }
#else
    glPopMatrix();
#endif



    return 1;
}



///////////////////////////////////////////////////////////////////////////////////////////
// 31-08-2016	modificata funzione colore da glColor4ub a gluColor4ub
//			DA TESTARE SUL TABLET

int print_dwg_lwpline(Dwg_Data *pdwg, Dwg_Object *pobject, Dwg_Color *pBlockColor, int Mode) {
    Dwg_Entity_LWPLINE *lwpoly = pobject->tio.entity->tio.LWPLINE;
    Dwg_Color pLayerColor = {0};
    Dwg_Object_Ref pltype;
    char layer_on = 0;

    unsigned int i;
    float layer_weight = 0.0f;

    /*
    lwpoly->num_points;
    lwpoly->points;
    lwpoly->num_bulges;
    lwpoly->bulges;
    lwpoly->num_widths;
    lwpoly->widths;
     */

#ifdef DISABLE_LWPLINE_ON_DRAG
    if (GLdrawOptions & 1) return 0;
#endif

    if (GLOutputToXML) {
        return 0;

    } else if (GLOutputToJSON) {
        return 0;

    } else if (GLOnClick) {
        return 0;

    } else if (GLSearchText) {
        return 0;

    } else {
        BOOL bRestoreWidth = FALSE;
        int nPt = 0;


        if (GLOutputToCanvas) {
        }

        get_layer_info(pdwg, pobject->tio.entity->layer->handleref, pobject->tio.entity->layer->absolute_ref, &pLayerColor, &pltype, &layer_on, &layer_weight);

        if (layer_on) {

            unsigned char r, g, b, a = 255;
            set_gl_color(pdwg, &pobject->tio.entity->color, &pLayerColor, pBlockColor, &r, &g, &b);

            if (GLDebug) {
                my_printf("print_dwg_lwpline:isLine:color:[%d,%d,%d]-O[%d,%d]-L[%d,%d]", r, g, b, pobject->tio.entity->color.index, pobject->tio.entity->color.rgb, pLayerColor.index, pLayerColor.rgb);
            }


            if (pobject->tio.entity->lineweight > 7 && pobject->tio.entity->lineweight < 29) {
                bRestoreWidth = TRUE;
                gluLineWidth((float) (3.0f + (int) (pobject->tio.entity->lineweight - 8) / 2));
            } else if (pobject->tio.entity->lineweight == 29) {
                // by layer
                gluLineWidth(layer_weight * 10.0f);
            } else {
                gluLineWidth(NORMAL_WIDTH);
            }


            for (i = 0; i < lwpoly->num_points; i++) {
                int isLine = 1;
                float bulge = 0.0f;

                if (!lwpoly->num_bulges) {
                } else if (lwpoly->bulges) {
                    if (lwpoly->bulges[i] != 0.0f) {
                        bulge = lwpoly->bulges[i];
                        isLine = 0;
                    }
                }


                if (isLine) {

                    // Aperura sequenza lineare
                    if (!nPt) {
                        nPt++;
                        gluBegin(GL_LINE_STRIP, lwpoly->num_points + (lwpoly->flags & 512 ? 1 : 0));
                    }

                    // my_printf("1)-print_dwg_lwpline:isLine:object:[%d,%d]-Layer:[%d,%d]", pobject->tio.entity->color.index, pobject->tio.entity->color.rgb, pLayerColor.index, pLayerColor.rgb);


                    gluColor4ub(r, g, b, 255);

#if defined(JNI_DLL) || defined(JNI_SO)
                    {
                        float x = (float) lwpoly->points[i].x;
                        float y = (float) lwpoly->points[i].y;
                        float z = 0.0f;
                        if (GLCurMatrix) {
                            lib3ds_matrix_trasnform(GLMatrix, &x, &y, &z);
                        }
                        glVertex3d(x, y, 0.0);
                    }
#else
                    glVertex3d(lwpoly->points[i].x, lwpoly->points[i].y, 0.0);
#endif


                } else {


                    int revDir = 0;
                    float start_ang, end_ang;
                    float center[3], radius;


                    // Chiusura sequenza lineare
                    if (nPt) {
                        // set_gl_color (pdwg, &pobject->tio.entity->color, &pLayerColor, pBlockColor, NULL, NULL, NULL);

                        // Debug del colore
                        // unsigned char r, g, b, a = 255;
                        // set_gl_color (pdwg, &pobject->tio.entity->color, &pLayerColor, pBlockColor, &r, &g, &b);
                        // my_printf("1)-print_dwg_lwpline:isLine:color:[%d,%d,%d]", r, g, b);

                        gluColor4ub(r, g, b, 255);

                        glVertex3d(lwpoly->points[i].x, lwpoly->points[i].y, 0.0);
                        gluEnd();
                        nPt = 0;
                    }


                    if (i + 1 < lwpoly->num_points) {
                        get_arc_coords_from_bulge((float) lwpoly->points[i].x, (float) lwpoly->points[i].y,
                                (float) lwpoly->points[i + 1].x, (float) lwpoly->points[i + 1].y,
                                (float) bulge,
                                (float*) &center[0], (float*) &center[1], (float*) &radius, (float*) &start_ang, (float*) &end_ang);

                        gluColor4ub(r, g, b, 255);

#ifdef JNI_DLL
                        {
                            float x = (float) center[0];
                            float y = (float) center[1];
                            float z = 0.0f;
                            if (GLCurMatrix) {
                                lib3ds_matrix_trasnform(GLMatrix, &x, &y, &z);
                            }
                            // N.B.: Da testare ed eventualmente implementare : trasformare tutti i punti dell'arco
                            OGL_DRAW_ARC(center[0], center[1], radius, start_ang, end_ang, r, g, b);
                            //
                        }
#elif defined JNI_SO
#else
                        OGL_DRAW_ARC(center[0], center[1], radius, start_ang, end_ang, r, g, b);
                        // OGL_DRAW_POINTS ( &LwPlineData[i].Center, 1, 2.0, RGB(255,0,0));
#endif

                    }
                }
            }



            if (lwpoly->flags & 512) {

                // Aperrura sequenza lineare
                if (!nPt) {
                    nPt++;
                    gluBegin(GL_LINE_STRIP, lwpoly->num_points + (lwpoly->flags & 512 ? 1 : 0));
                }

                gluColor4ub(r, g, b, 255);

#if defined(JNI_DLL) || defined(JNI_SO)
                {
                    float x = (float) lwpoly->points[0].x;
                    float y = (float) lwpoly->points[0].y;
                    float z = 0.0f;
                    if (GLCurMatrix) {
                        lib3ds_matrix_trasnform(GLMatrix, &x, &y, &z);
                    }
                    glVertex3d(x, y, 0.0);
                }
#else
                glVertex3d(lwpoly->points[0].x, lwpoly->points[0].y, 0.0);
#endif

            }

            /*
            if (lwpoly->const_width > 0 && lwpoly->const_width < 1.0) {
                gluLineWidth(3.0f);
                } else if (lwpoly->const_width > 1.0) {
                gluLineWidth(lwpoly->const_width);
                }
             */


            // Chiusura sequenza lineare
            if (nPt) {
                gluEnd();
            }


            if (bRestoreWidth) {
                gluLineWidth(NORMAL_WIDTH);
            }
        }
    }



    return 1;
}








char *GLKey[] = {"AP", "FN", "PR", "LA", "LD", "VE", "VL", "DI", "PL", "PI", "LN", "EM", "FA"
    // Richiesta del 25-07-2016
    // CORPI ILLUMINANTI MSN
    ,"BLED", "SLED", "FLED", "FOL", "FLT", "ID", "INF", "TLED", "FLOR"
    // 31-05-2018 da gialuca bonaguro
    ,"PRL"
        // 01-06-2018 da gialuca bonaguro
        , "COD", "TR", "DI", "PE", "DII", "NOCOD", "FLOR", "PA", "INLED", "IN", "DA", "LAF", "EM", "LAL", "FOL", "FB", "BLED", "LED DGA"
        , "TL", "LD", "FN", "LM", "VD", "TE", "FAPAL", "INF", "FLU", "PRB", "FNR", "VO", "VE", "APE", "FIN", "LA", "FNI", "FNE", "VF"
        , "L", "DIG", "PR", "FL", "DG", "STE", "AR", "ITE", "LED", "FO", "PL", "LT", "PIL", "PRG", "BL", "AL", "DL", "ID", "SLED"
        , "PRL", "PRE", "LG", "PRLED", "VL", "FA", "FARI AR", "LN", "AP", "DIB", "FLT", "TLED", "CN", "PI", "AD","FLED"
    };

char *GLFixedKey[] = { "FARETTI IN NUMERO VARIABILE" };

int GLNumKey = sizeof (GLKey) / sizeof (GLKey[0]);
int GLNumFixedKey = sizeof (GLFixedKey) / sizeof (GLFixedKey[0]);

int prepare_key_from_text(char *text, char *key, char *out_text, int isMText) {
    int retVal = 0;
    char str[256];


    if (key) key[0] = 0;

    
    if (text && key) {
        int i_key = 0, key_len = 0;
        for (i_key = 0; i_key < GLNumFixedKey; i_key++) {
            key_len = strlen(GLFixedKey[i_key]);
            if (strnicmp(text, GLFixedKey[i_key], key_len) == 0) {
                strcpy(key, GLFixedKey[i_key]);
                return 1;
            }
        }
    }
    
    ////////////////////////////
    // Correzione sul testo
    //
    if (wrap_text_for_search(text, str, sizeof (str), isMText) <= 0) {
        if (out_text) strcpy(out_text, text);
    } else {
        if (out_text) strcpy(out_text, str);
        text = str;
        if (text && key) {
            int i_key = 0, key_len = 0;
            for (i_key = 0; i_key < GLNumKey; i_key++) {
                key_len = strlen(GLKey[i_key]);
                if (strnicmp(text, GLKey[i_key], key_len) == 0) {
                    if (text[key_len] == ' ' || 
                        text[key_len] == '-' ||
                        text[key_len] == '_' ||
                        text[key_len] == '/' ||
                        text[key_len] == '|' ||
                        text[key_len] == '@' ||
                        text[key_len] == '#' ||
                        text[key_len] == 246 ||
                        isdigit(text[key_len])
                        ) {
                        strcpy(key, GLKey[i_key]);
                        retVal = 1;
                    } else {
                        
                    }
                }
            }
        }
    }

    return retVal;
}

int add_key_text_to_xml(float x, float y, float wx, float wy, float ht, char *pkey, char *text, int r, int g, int b, int a, int isMtext) {
    if (text) {
        char new_text[256], str[256], key[128];

        if (GLOutputToXMLOptions & 1) {

            prepare_key_from_text(text, key, new_text, isMtext);
            if (key[0]) {
                // sprintf(str, "<%s><X>%f</X><Y>%f</Y><WX>%f</WX><WY>%f</WY>", key, x, y, wx, wy);
                // AddStr(&GLOutputCode, str, &GLOutputCodeAllocated);
                sprintf(str, "<H>%0.3f</H><C>$d.%d.%d.%d</C>", ht, (int) r, (int) g, (int) b, (int) a);
                AddStr(&GLOutputCode, str, &GLOutputCodeAllocated);
                sprintf(str, "<T>%s</T><K></%s></K>\n", new_text);
                AddStr(&GLOutputCode, str, &GLOutputCodeAllocated);
            }

        } else {

            prepare_key_from_text(text, key, new_text, isMtext);
            if (key[0] && new_text[0]) {
                AddStr(&GLOutputCode, new_text, &GLOutputCodeAllocated);
                AddStr(&GLOutputCode, "\n", &GLOutputCodeAllocated);
            } else {
                // GLErrStr = NULL;
                // GLErrStrAllocated = 0;
                // GLNumErrStr = 0;
            }
        }
    }

    return 1;
}

int add_key_text_to_json(float x, float y, float wx, float wy, float ht, char *pkey, char *text, int r, int g, int b, int a, int isMtext) {
    if (text) {
        char out_text[256], str[256], key[128];

        prepare_key_from_text(text, key, out_text, isMtext);

        if (key[0]) {
            if (GLOutputCodeNumObj) AddStr(&GLOutputCode, ",\r\n", &GLOutputCodeAllocated);
            sprintf(str, "\"text\":");
            AddStr(&GLOutputCode, str, &GLOutputCodeAllocated);
            sprintf(str, "{\"key\":\"%s\",\"text\":\"\"}", key ? key : "", out_text);
            AddStr(&GLOutputCode, str, &GLOutputCodeAllocated);
            sprintf(str, "");
            AddStr(&GLOutputCode, str, &GLOutputCodeAllocated);
            GLOutputCodeNumObj++;
        }
    }
    return 1;
}


// TODO: REMOVE Disabling as debug
// #define DISABLE_TEXT_FOUND_CALLBACK
// #define DISABLE_TEXT_CALLBACK


extern int GLSearchText;
extern char GLSearchTextValue[256];
extern int GLSearchTextMode;

extern int GLOnClick;
extern int GLOnClickCount;
extern float GLOnClickX, GLOnClickY, GLOnClickGap;

int print_dwg_text(Dwg_Data *pdwg, Dwg_Object *pobject, Dwg_Color *pBlockColor, int Mode) {
    Dwg_Entity_TEXT *ptext = pobject->tio.entity->tio.TEXT;
    Dwg_Color pLayerColor;
    Dwg_Object_Ref pltype;
    unsigned char r, g, b, a = 255;
    char layer_on = 0;
    float layer_weight = 0.0f;


#ifdef DISABLE_TEXT_ON_DRAG
    if (GLdrawOptions & 1) return 0;
#endif

    if (GL_DISABLE_TEXT_ON_DRAG) {
        if (GLdrawOptions & 1) return 0;
    }




#ifdef DISABLE_TEXT_CALLBACK
    return 0;
#endif

    get_layer_info(pdwg, pobject->tio.entity->layer->handleref, pobject->tio.entity->layer->absolute_ref, &pLayerColor, &pltype, &layer_on, &layer_weight);

    if (layer_on) {

        set_gl_color(pdwg, &pobject->tio.entity->color, &pLayerColor, pBlockColor, &r, &g, &b);

        /*
        ptext->text_value;

        ptext->dataflags;
        ptext->insertion_pt;
        ptext->alignment_pt;

        ptext->oblique_ang;
        ptext->rotation_ang;
        ptext->height;
        ptext->width_factor;
        ptext->horiz_alignment;
        ptext->vert_alignment;
        ptext->style;
         */




        if (GLOutputToCanvas) {
            if (GLOutputToCanvasStep == 0) {
                // Prepare
            } else if (GLOutputToCanvasStep == 1) {
                // Ouput
            } else if (GLOutputToCanvasStep == 2) {
                char new_text[256], str[256], key[128];
                // N.B.: Incorporare la matrice corrente (chiamata da blocco)
                float x = ptext->insertion_pt.x;
                float y = ptext->insertion_pt.y;
                float z = 0.0f;
                float rect_width = 0.0f; // ptext->rect_width

#if defined(JNI_DLL) || defined(JNI_SO)
                if (GLCurMatrix) {
                    lib3ds_matrix_trasnform(GLMatrix, &x, &y, &z);
                }
#else
#endif

                prepare_key_from_text(ptext->text_value, key, new_text, FALSE);

                rect_width = strlen(new_text) * ptext->height * 0.8f;

                // "GLTextsData.push({x:100.0, y:-6100.0, t:\"Text Demo\", wh:1200, ht:1200, ang:0.0, r:1.00, g:0.0, b:0.0, a:1.00, tidx:0 });\n"
                sprintf(str, "GLTextsData.push({x:%0.2f,y:%0.2f,t:\"%s\",wh:%0.2f,ht:%0.2f,ang:%0.2f,r:%0.2f,g:%0.2f,b:%0.2f,a:%0.2f,tidx:%d,jh:%d,jv:%d});\n",
                        x, y + ptext->height,
                        new_text,
                        rect_width,
                        (float) ptext->height,
                        (float) ptext->rotation_ang /*57.295779f*/,
                        (float) r / 255.0f, (float) g / 255.0f, (float) b / 255.0f, (float) a / 255.0f,
                        GLCanvasTextsCounter++,
                        ptext->horiz_alignment, ptext->vert_alignment
                        );
                AddStr(&GLCanvasTexts, str, &GLCanvasTextsAllocated);
            }


        } else if (GLOutputToXML) {
            if (ptext->text_value) {
                add_key_text_to_xml(ptext->insertion_pt.x, ptext->insertion_pt.y, 0.0f, 0.0f, ptext->height, NULL, ptext->text_value, (int) r, (int) g, (int) b, (int) a, FALSE);
            }

        } else if (GLOutputToJSON) {
            if (ptext->text_value) {
                add_key_text_to_json(ptext->insertion_pt.x, ptext->insertion_pt.y, 0.0f, 0.0f, ptext->height, NULL, ptext->text_value, (int) r, (int) g, (int) b, (int) a, FALSE);
            }

        } else if (GLOnClick) {
            float ht = ptext->height > 0.0 ? ptext->height : 1.0f;
            if (GLOnClickX >= (ptext->insertion_pt.x - ht * GLOnClickGap) && GLOnClickX <= (ptext->insertion_pt.x + ht * GLOnClickGap + ptext->height * (ptext->text_value ? strlen(ptext->text_value) : 0))) {
                if (GLOnClickY >= (ptext->insertion_pt.y - ht * GLOnClickGap) && GLOnClickY <= (ptext->insertion_pt.y + ht * GLOnClickGap + ptext->height * 1)) {
                    // my_printf("print_dwg_text : clicked on %s", ptext->text_value);
                    onDrawText(ptext->text_value, "", ptext->horiz_alignment, ptext->vert_alignment, (float) ptext->insertion_pt.x, (float) ptext->insertion_pt.y, (float) ptext->height, (float) ptext->rotation_ang, (unsigned char) r, (unsigned char) g, (unsigned char) b, (unsigned char) a, (unsigned char) FALSE, 0 + 1, 0);
                }
            }
        } else if (GLSearchText) {
            Dwg_Object_LAYER *layer = (Dwg_Object_LAYER *) get_layer(pdwg, pobject->tio.entity->layer->handleref, pobject->tio.entity->layer->absolute_ref);
#ifndef DISABLE_TEXT_FOUND_CALLBACK
            onDrawText(ptext->text_value, layer->entry_name, ptext->horiz_alignment, ptext->vert_alignment, (float) ptext->insertion_pt.x, (float) ptext->insertion_pt.y, (float) ptext->height, (float) ptext->rotation_ang, (unsigned char) r, (unsigned char) g, (unsigned char) b, (unsigned char) a, (unsigned char) FALSE, 0 + 2, GLSearchTextMode);
#endif
        } else {
            char str[512];

            if (ptext->RTFlag & 1) {
                // Testo disabilitato
                return 0;
            }

            if (ptext->text_value) {
                if (wrap_text_for_search(ptext->text_value, str, sizeof (str), FALSE) > 0) {
                    ptext->text_value = realloc(ptext->text_value, strlen(str) + 1);
                    if (ptext->text_value) strcpy(ptext->text_value, str);
                }
            }
            // N.B.: Da implementare il porting della classe GLText in C per le prestazioni
            // onDrawTextDirect ( ptext->text_value, "", ptext->horiz_alignment, ptext->vert_alignment, (float)ptext->insertion_pt.x, (float)ptext->insertion_pt.y, (float)ptext->height, (float)ptext->rotation_ang, (unsigned char)r, (unsigned char)g, (unsigned char)b, (unsigned char)a, (char)FALSE, 0+0, 0);

            onDrawText(ptext->text_value, "", ptext->horiz_alignment, ptext->vert_alignment, (float) ptext->insertion_pt.x, (float) ptext->insertion_pt.y, (float) ptext->height, (float) ptext->rotation_ang, (unsigned char) r, (unsigned char) g, (unsigned char) b, (unsigned char) a, (unsigned char) FALSE, 0 + 0, 0);
        }
    }

    return 1;
}

int print_dwg_mtext(Dwg_Data *pdwg, Dwg_Object *pobject, Dwg_Color *pBlockColor, int Mode) {
    Dwg_Entity_MTEXT *ptext = pobject->tio.entity->tio.MTEXT;
    Dwg_Color pLayerColor;
    Dwg_Object_Ref pltype;
    unsigned char r, g, b, a = 255;
    char layer_on = 0;
    float layer_weight = 0.0f;


#ifdef DISABLE_TEXT_ON_DRAG
    if (GLdrawOptions & 1) return 0;
#endif

    if (GL_DISABLE_TEXT_ON_DRAG) {
        if (GLdrawOptions & 1) return 0;
    }

#ifdef DISABLE_TEXT_CALLBACK
    return 0;
#endif



    get_layer_info(pdwg, pobject->tio.entity->layer->handleref, pobject->tio.entity->layer->absolute_ref, &pLayerColor, &pltype, &layer_on, &layer_weight);

    if (layer_on) {
        set_gl_color(pdwg, &pobject->tio.entity->color, &pLayerColor, pBlockColor, &r, &g, &b);

        /*
        BITCODE_3BD insertion_pt;
        BITCODE_3BD extrusion;
        BITCODE_3BD x_axis_dir;
        BITCODE_BD ;
        BITCODE_BS attachment;
        BITCODE_BS drawing_dir;
        BITCODE_BD extends_ht; //spec typo?
        BITCODE_BD extends_wid;
        BITCODE_TV ;
        BITCODE_BS linespace_style;
        BITCODE_BD linespace_factor;
        BITCODE_B unknown_bit;
        BITCODE_BL unknown_long;
        BITCODE_H style;
        } Dwg_Entity_MTEXT;
         */

        if (GLOutputToCanvas) {
            if (GLOutputToCanvasStep == 0) {
                // Prepare
            } else if (GLOutputToCanvasStep == 1) {
                // Ouput
            } else if (GLOutputToCanvasStep == 2) {
                char new_text[512], str[1024], key[128];
                // N.B.: Incorporare la matrice corrente (chiamata da blocco)
                float x = ptext->insertion_pt.x;
                float y = ptext->insertion_pt.y;
                float z = 0.0f;
                float rect_width = 0.0f; // ptext->rect_width

#if defined(JNI_DLL) || defined(JNI_SO)
                if (GLCurMatrix) {
                    lib3ds_matrix_trasnform(GLMatrix, &x, &y, &z);
                }
#else
#endif

                prepare_key_from_text(ptext->text, key, new_text, TRUE);

                rect_width = strlen(new_text) * ptext->text_height * 0.8f;

                // "GLTextsData.push({x:100.0, y:-6100.0, t:\"Text Demo\", wh:1200, ht:1200, ang:0.0, r:1.00, g:0.0, b:0.0, a:1.00, tidx:0 });\n"
                sprintf(str, "GLTextsData.push({x:%0.2f,y:%0.2f,t:\"%s\",wh:%0.2f,ht:%0.2f,ang:%0.2f,r:%0.2f,g:%0.2f,b:%0.2f,a:%0.2f,tidx:%d,jh:%d,jv:%d});\n",
                        x, y,
                        new_text,
                        (float) rect_width,
                        (float) ptext->text_height,
                        0.0f,
                        (float) r / 255.0f, (float) g / 255.0f, (float) b / 255.0f, (float) a / 255.0f,
                        GLCanvasTextsCounter++,
                        0, 0
                        );
                AddStr(&GLCanvasTexts, str, &GLCanvasTextsAllocated);
            }

        } else if (GLOutputToXML) {
            if (ptext->text) {
                add_key_text_to_xml(ptext->insertion_pt.x, ptext->insertion_pt.y, ptext->rect_width, ptext->text_height, ptext->extends_ht, NULL, ptext->text, (int) r, (int) g, (int) b, (int) a, TRUE);
            }
        } else if (GLOutputToJSON) {
            if (ptext->text) {
                add_key_text_to_json(ptext->insertion_pt.x, ptext->insertion_pt.y, ptext->rect_width, ptext->text_height, ptext->extends_ht, NULL, ptext->text, (int) r, (int) g, (int) b, (int) a, TRUE);
            }
        } else if (GLOnClick) {
            float ht = ptext->text_height > 0.0 ? ptext->text_height : 1.0f;
            char new_text[512], str[1024], key[128];
            prepare_key_from_text(ptext->text, key, new_text, TRUE);
            if (GLOnClickX >= (ptext->insertion_pt.x - ht * GLOnClickGap) && GLOnClickX <= (ptext->insertion_pt.x + ht * GLOnClickGap + ptext->text_height * strlen(new_text))) {
                if (GLOnClickY >= (ptext->insertion_pt.y - ht * GLOnClickGap - ht * 1.0) && GLOnClickY <= (ptext->insertion_pt.y + ht * GLOnClickGap)) {
                    // my_printf("print_dwg_mtext : clicked on %s", ptext->text);
                    onDrawText(new_text, "", /*ptext->horiz_alignment*/0, /*ptext->vert_alignment*/0, (float) ptext->insertion_pt.x, (float) ptext->insertion_pt.y, (float) ptext->text_height, 0.0f, (unsigned char) r, (unsigned char) g, (unsigned char) b, (unsigned char) a, (unsigned char) TRUE, 0 + 1, 0);
                }
            }
        } else if (GLSearchText) {
            Dwg_Object_LAYER *layer = (Dwg_Object_LAYER *) get_layer(pdwg, pobject->tio.entity->layer->handleref, pobject->tio.entity->layer->absolute_ref);
#ifndef DISABLE_TEXT_FOUND_CALLBACK
            onDrawText(ptext->text, layer->entry_name, /*ptext->horiz_alignment*/0, /*ptext->vert_alignment*/0, (float) ptext->insertion_pt.x, (float) ptext->insertion_pt.y, (float) ptext->text_height, 0.0f, (unsigned char) r, (unsigned char) g, (unsigned char) b, (unsigned char) a, (unsigned char) TRUE, 0 + 2, GLSearchTextMode);
#endif
        } else {
            char str[512];

            if (ptext->RTFlag & 1) {
                // Testo disabilitato
                return 0;
            }

            if (ptext->text) {
                if (wrap_text_for_search(ptext->text, str, sizeof (str), TRUE) > 0) {
                    ptext->text = realloc(ptext->text, strlen(str) + 1);
                    if (ptext->text) strcpy(ptext->text, str);
                }
            }
            // N.B.: Da implementare il porting della classe GLText in C per le prestazioni
            // onDrawTextDirect ( ptext->text, "", ptext->horiz_alignment, ptext->vert_alignment, (float)ptext->insertion_pt.x, (float)ptext->insertion_pt.y, (float)ptext->text_height, 0.0f, r, g, b, a, (char)TRUE, 0+0);

            onDrawText(ptext->text, "", /*ptext->horiz_alignment*/0, /*ptext->vert_alignment*/0, (float) ptext->insertion_pt.x, (float) ptext->insertion_pt.y, (float) ptext->text_height, 0.0f, (unsigned char) r, (unsigned char) g, (unsigned char) b, (unsigned char) a, (unsigned char) TRUE, 0 + 0, 0);
        }
    }

    return 1;
}

int print_dwg_hatch(Dwg_Data *pdwg, Dwg_Object *pobject, Dwg_Color *pBlockColor, int Mode) {
    Dwg_Color pLayerColor;
    Dwg_Entity_HATCH *hatch = pobject->tio.entity->tio.HATCH;
    unsigned char r, g, b, a = 75;
    char layer_on;
    int ip, il;
    float layer_weight = 0.0f;


#ifdef DISABLE_HATCH_ON_DRAG
    if (GLdrawOptions & 1) return 0;
#endif

    if (GL_DISABLE_HATCH_ON_DRAG) {
        if (GLdrawOptions & 1) return 0;
    }


    if (GLOutputToCanvas) {
        return 0;

    } else if (GLOutputToXML) {
        return 0;

    } else if (GLOutputToJSON) {
        return 0;

    } else if (GLOnClick) {
        return 0;

    } else if (GLSearchText) {
        return 0;

    } else {

        // Debug
        // return 0;

        get_layer_info(pdwg, pobject->tio.entity->layer->handleref, pobject->tio.entity->layer->absolute_ref, &pLayerColor, NULL, &layer_on, &layer_weight);

        if (layer_on) {

            set_gl_color(pdwg, &pobject->tio.entity->color, &pLayerColor, pBlockColor, &r, &g, &b);


            for (ip = 0; ip < hatch->num_paths; ip++) {

                if (hatch->paths[ip].flag & 16) {
                }
                if (hatch->paths[ip].flag & 4) {
                }
                if (hatch->paths[ip].flag & 4) {
                }
                if (hatch->paths[ip].flag & 2) {
                    // polyline_paths
                }
                if (hatch->paths[ip].flag & 1) {
                }


                // my_printf("[hatch->__unknown_style:%d - __patterntype:%d - solid_fill:%d", (int)hatch->__unknown_style, (int)hatch->__patterntype, hatch->solid_fill);
                if (hatch->solid_fill) {
                    // a = 200;
                    if (hatch->paths[ip].polyline_paths) {
                        if (hatch->paths[ip].closed) {
                            if (hatch->paths[ip].num_path_segs == 2) {
                                if (hatch->paths[ip].bulges_present) {
                                    // Cerchio pieno
                                    a = 200;
                                }
                            }
                        }
                    }
                }

#ifdef JNI_DLL
                // Debug
                if (hatch->paths[ip].polyline_paths) {
                    char str[256];
                    // sprintf(str,"[Path %d][Point %d] - polyline_paths - %0.3f-%0.3f   [%0.3f]\r\n", ip, il, hatch->paths[ip].polyline_paths[il].point.x, hatch->paths[ip].polyline_paths[il].point.y, hatch->paths[ip].polyline_paths[il].bulge);
                    if (hatch->paths[ip].closed) {
                        // draw_filled_polygon(hatch, hatch->paths[ip].polyline_paths, NULL, hatch->paths[ip].num_path_segs, (int)hatch->paths[ip].closed, (int)hatch->paths[ip].bulges_present, r, g, b, a, false);
                    }
                }
#elif defined JNI_SO
#else
                if (hatch->paths[ip].polyline_paths) {
                    // sprintf(str,"[Path %d][Point %d] - polyline_paths - %0.3f-%0.3f   [%0.3f]\r\n", ip, il, hatch->paths[ip].polyline_paths[il].point.x, hatch->paths[ip].polyline_paths[il].point.y, hatch->paths[ip].polyline_paths[il].bulge);
                    if (hatch->paths[ip].closed) {
                        draw_filled_polygon(hatch, hatch->paths[ip].polyline_paths, NULL, hatch->paths[ip].num_path_segs, (int) hatch->paths[ip].closed, (int) hatch->paths[ip].bulges_present, r, g, b, a, false);
                    } else {
                        // my_printf("[Path %d][Point %d] - poly not closed!", ip, il);
                        gluBegin(GL_LINES, hatch->paths[ip].num_path_segs);
                        for (il = 0; il < hatch->paths[ip].num_path_segs; il++) {
                            glVertex3d(hatch->paths[ip].polyline_paths[il].point.x, hatch->paths[ip].polyline_paths[il].point.y, 0.0);
                            gluColor4ub(r, g, b, a);
                        }
                        gluEnd();
                    }
                }
#endif



#ifdef JNI_DLL
                if (hatch->paths[ip].segs) {
                    if (hatch->paths[ip].closed) {
                    }
                    // draw_filled_polygon(hatch, NULL, hatch->paths[ip].segs, hatch->paths[ip].num_path_segs, (int)0, (int)0, r, g, b, a, false);
                }
#elif defined JNI_SO
#else
                if (hatch->paths[ip].segs) {
                    if (hatch->paths[ip].closed) {
                        // my_printf("[Path %d][Point %d] - segs closed!", ip, il);
                    }
                    draw_filled_polygon(hatch, NULL, hatch->paths[ip].segs, hatch->paths[ip].num_path_segs, (int) 0, (int) 0, r, g, b, a, false);
                }
#endif

            }
        }
    }

    return 1;
}










#include <jni.h>

extern JNIEnv *GLJNIenv;
extern jobject GLJNIjobj;



///////////////////////////////////////////////////////////////////////////////////////////////
//
// N.B.: Se viene invocato da una classe con metodo static l'app in java esce
//

// []AAA[]01234[] = AAA01234

int wrap_text_for_search(char *text, char *out, int out_size, int isMText) {
    int i = 0, retVal = 0;

    if (text) {
        if (out) {
            char mtext[512];

            while (*text == ' ') text++;

            if (isMText) {
                if (extract_text_from_mtext(text, mtext, sizeof (mtext)) >= 0) {
                    text = mtext;
                }
            }


            while (isalpha(*text) && *text != ' ' && *text) {
                out[i++] = *text;
                text++;
                if (i >= out_size) {
                    out[i] = 0;
                    return retVal;
                }
            }

            // N.B.: Aggiunta su richiesta del 2/7/2018 888 Gianluca (es.: DI da 12 a 16, AP da 17 a 19)
            if (strnicmp(text, " da ", 4) == 0) {
                while (*text) {
                    out[i++] = *text;
                    text++;
                    if (i >= out_size) {
                        break;
                    }
                }
                out[i] = 0;
                return 1;
            }
            
            while (*text == ' ') text++;

            if (isdigit(*text)) {
                retVal = 1;
                while (isdigit(*text) && *text) {
                    out[i++] = *text;
                    text++;
                    if (i >= out_size) {
                        out[i] = 0;
                        return retVal;
                    }
                }
                // Dopo la paete numerica è tolletaro un carattere
                if (isalpha(*text) && *text) {
                    out[i++] = *text;
                    text++;
                    if (i >= out_size) {
                        out[i] = 0;
                        return retVal;
                    }
                }
            } else {
                while (*text) {
                    out[i++] = *text;
                    text++;
                    if (i >= out_size) {
                        out[i] = 0;
                        return retVal;
                    }
                }
            }

            out[i] = 0;

            if (i > 0)
                if (out[i - 1] == '*') {
                    out[i - 1] = 0;
                }
            retVal = 1;
        }
    }

    return retVal;
}

int extract_text_from_mtext(char *text, char *out, int out_size) {
    int res = 0;
    if (text) {
        char out_text[512], format_str[256];
        int out_text_size = 0, i, j, n = strlen(text);

        out_text[0] = 0;
        format_str[0] = 0;

        for (i = 0; i < n; i++) {
            if (text[i] == '\\') {
                i++;
                switch (text[i]) {
                    case 'L':
                        break;
                    case 'l':
                        break;
                    case 'O':
                        break;
                    case 'o':
                        break;
                    case 'k':
                        break;
                    case 'K':
                        break;
                    case 'P':
                        out_text[out_text_size++] = '\\';
                        out_text[out_text_size++] = 'n';
                        out_text[out_text_size] = 0;
                        break;
                    case 'X':
                        break;

                    case 'Q':
                    case 'F':
                    case 'f':
                    case 'S':
                    case 's':
                    case 'A':
                    case 'a':
                    case 'C':
                    case 'c':
                    case 'p':
                    case 'T':
                        j = i;
                        while (text[i] != ';' && text[i]) i++;
                        // format_str = text.substring(j + 1, i);
                        memcpy(&format_str, &text[j + 1], i - (j + 1));
                        format_str[i - (j + 1)] = 0;
                        // "Arial|b0|i0|c0|p34"
                        /*
                        String[] fmt_array = format_str.split("\\|");
                        for (String fmt : fmt_array) {
                            if (fmt.length() > 0) {
                                if (fmt.charAt(0) == 'p' || fmt.charAt(0) == 'p') {
                                    float text_ht = Float.parseFloat(fmt.substring(1));
                                    if (text_ht > 0.0) ht = text_ht;
                                }
                            }
                        }
                         */
                        break;
                    case 'w':
                    case 'W':
                        j = i;
                        while (text[i] != ';' && text[i]) i++;
                        // format_str = text.substring(j + 1, i);
                        memcpy(&format_str, &text[j + 1], i - (j + 1));
                        break;
                    case 'h':
                    case 'H':
                        j = i;
                        while (text[i] != ';' && text[i]) i++;
                        // format_str = text.substring(j + 1, i);
                        memcpy(&format_str, &text[j + 1], i - (j + 1));
                        format_str[i - (j + 1)] = 0;
                        break;
                    default:
                        break;
                }
            } else if (text[i] == '{') {
            } else if (text[i] == '}') {
                if (text[i + 1] == '*') {
                    i++;
                }
            } else {
                out_text[out_text_size++] = text[i];
                out_text[out_text_size] = 0;
            }
        }

        if (out) {
            strncpy(out, out_text, out_size);
            res = 1;
        }
    }

    return res;
}



/*

Code	Function
\L	Start underline
\l	Stop underline
\O	Start overstrike
\o	Stop overstrike
\K	Start strike-through
\k	Stop strike-through
\P	New paragraph (new line)
\pxi	Control codes for bullets, numbered paragraphs and columns
\X	Paragraph wrap on the dimension line (only in dimensions)
\Q	Slanting (obliquing) text by angle - e.g. \Q30;
\H	Text height - e.g. \H3x;
\W	Text width - e.g. \W0.8x;
\F	Font selection
e.g. \Fgdt;o - GDT-tolerance
e.g. \Fkroeger|b0|i0|c238|p10 - font Kroeger, non-bold, non-italic, codepage 238, pitch 10
\S	Stacking, fractions
e.g. \SA^B:
A
B
e.g. \SX/Y:
X
Y
e.g. \S1#4:
¼
\A	Alignment
\A0; = bottom
\A1; = center
\A2; = top
\C	Color change
\C1; = red
\C2; = yellow
\C3; = green
\C4; = cyan
\C5; = blue
\C6; = magenta
\C7; = white
\T	Tracking, char.spacing - e.g. \T2;
\~	Non-wrapping space, hard space
{}	Braces - define the text area influenced by the code
\	Escape character - e.g. \\ = "\", \{ = "{"

 */


#ifdef JNI_DLL
#define strcasecmp strcmpi
#endif

void onDrawText(unsigned char *text, unsigned char *layer,
        int horiz_alignment, int vert_alignment,
        float x, float y, float ht, float ang,
        unsigned char r, unsigned char g, unsigned char b, unsigned char a,
        unsigned char isMText, int Options, int SearchMode) {



#ifdef DISABLE_TEXT_ON_DRAG
    if (GLdrawOptions & 1) return;
#endif

    if (GL_DISABLE_TEXT_ON_DRAG) {
        if (GLdrawOptions & 1) return;
    }


    if (Options & 2) {
        // Key Search
        BOOL bContinue = FALSE;

        if (text) {
            if (SearchMode == 0) {
                // Exact Key Search
                char SearchTextValue[256];
                if (wrap_text_for_search(text, SearchTextValue, sizeof (SearchTextValue), isMText) > 0) {
                    if (strcasecmp(SearchTextValue, GLSearchTextValue) == 0) {
                        bContinue = TRUE;
                    }
                }
            } else if (SearchMode == 1) {
                // Free Search
                if (search_strstr(text, GLSearchTextValue, 0)) {
                } else {
                    bContinue = TRUE;
                }

            } else if (SearchMode == 2) {
                // Exact Search
                if (strcasecmp(text, GLSearchTextValue) == 0) {
                    bContinue = TRUE;
                }
            }
        }

        if (!bContinue) return;
    } else {
    }



    if (GLJNIenv) {
        if (text) {
            if (text[0]) {
                jstring Keyjstr = NULL, gKeyjstr = NULL;
                jstring Layerjstr = NULL, gLayerjstr = NULL;
                jclass jcl = NULL;

                jcl = (*GLJNIenv)->FindClass(GLJNIenv, "com/imuve/cristian/imuve/MainActivity");

                // try {	
                {
                    jclass gjcl = (*GLJNIenv)->NewGlobalRef(GLJNIenv, jcl);
                    if (gjcl) {
                        jmethodID methodId = NULL;

                        if (Options & 1) {
                            // onClick (float x, float y, float wh, float ht, float angle, String Key, String Layer, int Color, int TypeOf, int eventType) {
                            Keyjstr = (*GLJNIenv)->NewStringUTF(GLJNIenv, text);
                            Layerjstr = (*GLJNIenv)->NewStringUTF(GLJNIenv, "?");
                            gKeyjstr = (*GLJNIenv)->NewGlobalRef(GLJNIenv, Keyjstr);
                            gLayerjstr = (*GLJNIenv)->NewGlobalRef(GLJNIenv, Layerjstr);
                            methodId = (*GLJNIenv)->GetMethodID(GLJNIenv, gjcl, "onClickText", "(FFFFFLjava/lang/String;Ljava/lang/String;IIC)V");
                        } else if (Options & 2) {
                            Keyjstr = (*GLJNIenv)->NewStringUTF(GLJNIenv, text);
                            Layerjstr = (*GLJNIenv)->NewStringUTF(GLJNIenv, layer);
                            gKeyjstr = (*GLJNIenv)->NewGlobalRef(GLJNIenv, Keyjstr);
                            gLayerjstr = (*GLJNIenv)->NewGlobalRef(GLJNIenv, Layerjstr);
                            methodId = (*GLJNIenv)->GetMethodID(GLJNIenv, gjcl, "onClickText", "(FFFFFLjava/lang/String;Ljava/lang/String;IIC)V");
                        } else {
                            methodId = (*GLJNIenv)->GetMethodID(GLJNIenv, gjcl, "onDrawText", "(Ljava/lang/String;IIFFFFFFFFC)V");
                        }

                        if (Options & 1) {
                            // onClick (float x, float y, float wh, float ht, float angle, String Key, String Layer, int Color, int TypeOf, int eventType) {
                            int iColor = 0; // (float)r/255.0f, (float)g/255.0f, (float)b/255.0f, (float)a/255.0f
                            float wh = 0.0f;
                            if (methodId) {
                                (*GLJNIenv)->CallVoidMethod(GLJNIenv, GLJNIjobj, methodId, x, y, wh, ht, ang, gKeyjstr, gLayerjstr, iColor, isMText, 0 + 0);
                            }
                            // Conteggio oggetti cliccati
                            GLOnClickCount++;

                        } else if (Options & 2) {
                            // Search
                            int iColor = 0; // (float)r/255.0f, (float)g/255.0f, (float)b/255.0f, (float)a/255.0f
                            float wh = strlen(text) * ht * 0.9f;
                            if (methodId) {
                                (*GLJNIenv)->CallVoidMethod(GLJNIenv, GLJNIjobj, methodId, x, y, wh, ht, ang, gKeyjstr, gLayerjstr, iColor, isMText, 0 + 2);
                            }
                            GLOnClickCount++;

                        } else {
                            // my_printf("onDrawText2(%.3f,%.3f,%.3f,%s)", x, y, ht, text);
                            Keyjstr = (*GLJNIenv)->NewStringUTF(GLJNIenv, text);
                            gKeyjstr = (*GLJNIenv)->NewGlobalRef(GLJNIenv, Keyjstr);
                            if (methodId) {
                                (*GLJNIenv)->CallVoidMethod(GLJNIenv, GLJNIjobj, methodId, gKeyjstr, horiz_alignment, vert_alignment, x, y, ht, ang, (float) r / 255.0f, (float) g / 255.0f, (float) b / 255.0f, (float) a / 255.0f, isMText);
                            }
                        }

                    } else {
                        // my_printf("No jclass");
                    }

                    (*GLJNIenv)->DeleteLocalRef(GLJNIenv, Keyjstr);
                    (*GLJNIenv)->DeleteLocalRef(GLJNIenv, Layerjstr);
                    (*GLJNIenv)->DeleteLocalRef(GLJNIenv, jcl);

                    (*GLJNIenv)->DeleteGlobalRef(GLJNIenv, gKeyjstr);
                    (*GLJNIenv)->DeleteGlobalRef(GLJNIenv, gLayerjstr);
                    (*GLJNIenv)->DeleteGlobalRef(GLJNIenv, gjcl);

                }
                // } catch (Exception e) { }
            }
        }
    }
}





/*



int onPrepareText () {

if (GLJNIenv) {
    jclass jcl = NULL;
    jcl = (*GLJNIenv)->FindClass(GLJNIenv, "com/imuve/cristian/imuve/MainActivity");

    {	jclass gjcl = (*GLJNIenv)->NewGlobalRef(GLJNIenv, jcl);
        if (gjcl) {
        jmethodID methodId = NULL;
        methodId = (*GLJNIenv)->GetMethodID(GLJNIenv, gjcl, "onPrepareText", "(I)I");

        if (methodId) {
            return (*GLJNIenv)->CallIntMethod(GLJNIenv, GLJNIjobj, methodId, 0);
            } else {
            my_printf("No methodId");
            }
        } else {
        my_printf("No jclass");
        }
        (*GLJNIenv)->DeleteLocalRef(GLJNIenv, jcl);
        }
    }
return -1;
}


void onDrawTextDirect( char *text, char *layer, int horiz_alignment, int vert_alignment, float x, float y, float ht, float ang, float r, float g, float b, float a, char isMtext, int SearchMode ) {
if (isMtext == 1) {
    int i, j, n = text.length();
    String out_text = "", format_str = "";
    for (i = 0; i < n; i++) {
        if (text[i] == '\\') {
            i++;
            switch (text[i]) {
                case 'L':
                    break;
                case 'O':
                    break;
                case 'o':
                    break;
                case 'f':
                case 'F':
                    j = i;
                    while (text[i] != ';') i++;
                    strcpy(format_str, (char *)text[j + 1]);
                    format_str[j+1-i] = 0;
                    // "Arial|b0|i0|c0|p34"
                    String[] fmt_array = format_str.split("\\|");
                    for (String fmt : fmt_array) {
                        if (fmt.length() > 0) {
                            if (fmt.charAt(0) == 'p' || fmt.charAt(0) == 'p') {
                                float text_ht = Float.parseFloat(fmt.substring(1));
                                if (text_ht > 0.0) ht = text_ht;
                            }
                        }
                    }
                    break;
                case 'w':
                case 'W':
                    j = i;
                    while (text[i] != ';') i++;
                    // format_str = text.substring(j + 1, i);
                    strcpy(format_str, (char *)text[j + 1]);
                    format_str[j+1-i] = 0;
                    break;
                case 'h':
                case 'H':
                    j = i;
                    while (text[i] != ';') i++;
                    // format_str = text.substring(j + 1, i);
                    strcpy(format_str, (char *)text[j + 1]);
                    format_str[j+1-i] = 0;
                    break;
                default:
                    break;
            }
        } else if (text[i] == '{') {
        } else if (text[i] == '}') {
        } else {
            out_text += text[i];
        }
    }

    text = out_text;
    }

if (ht  > 0.0) {
    if (cameraWY/ht < 2.0f) {
        return;
    }
}


if (ht > 0.0f) {

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();

    glMatrixMode(GL_PROJECTION );

    glEnable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);



    // Compensa la chiamata dal DrawDwg
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_LIGHTING);
    glDisableClientState(GL_COLOR_ARRAY);



    glPushMatrix();

    glTranslatef(x, y, 0.0f);
    if (ang != 0.0f) {
        glRotatef(ang * 180.0f / 3.14159265354f, 0.0f, 0.0f, 1.0f);
    }

    float text_scale = ht / TEXT_BASE_SIZE;

    glText.setScale(text_scale);

    // glText.setAngle (ang);

    // glText.begin(r, g, b, a);
    glColor4f( r, g, b, a );        // Set Color+Alpha
    glBindTexture( GL_TEXTURE_2D, GLTextTextureId );  // Bind the Texture

    // DA CONVERTIRE IN C glText.draw(text, 0.0f, 0.0f);
    // DA CONVERTIRE IN C glText.end();


    glPopMatrix();

    // glDisable(GL_BLEND);
    glDisable(GL_TEXTURE_2D);
    }
}
 */







//drawOptions & 1   -> In transazione

void draw_dwg(void *ptr_dwg, int drawOptions) {
    Dwg_Data *pdwg = (Dwg_Data*) ptr_dwg;



#if defined(JNI_DLL) || defined(JNI_SO)
    // Inizializza la matrice corrente
    lib3ds_matrix_identity(GLMatrix);
#endif




    // GLScreenWX = 1200.0f;
    // GLScreenWY = 660.0f;


    GLdrawOptions = drawOptions;


    if (GLrenderMode == GL_RENDER) {
        unsigned int i;

        if (GLOutputToCanvas) {
        } else if (GLOutputToXML) {
        } else if (GLOutputToJSON) {
        } else if (GLOnClick) {
        } else if (GLSearchText) {
        } else {
            prepare_opengl_display();
        }


        for (i = 0; i < pdwg->num_objects; i++) {

#ifdef _DEBUG
            if (i == 2193) {
                int db = 1;
            }
#endif


            TRY{
                if (pdwg->object[i].supertype == DWG_SUPERTYPE_ENTITY || pdwg->object[i].supertype == DWG_SUPERTYPE_UNKNOWN) {
                    draw_dwg_object(pdwg, &pdwg->object[i], NULL, 0 + 0);

#ifdef _DEBUG
                    /*
                     *
                    if (GLOutputToCanvasStep== 2) {
                            fprintf(stdout,"[ent:%d]",i);
                            fflush(stdout);
                    }
                     */
#endif
                }
} CATCH{} ETRY;

            // NO
            if (i >= pdwg->num_objects - pdwg->num_entities) {
            }
        }


        if (GLOutputToCanvas) {
        } else if (GLOutputToXML) {
        } else if (GLOutputToJSON) {
        } else if (GLOnClick) {
        } else if (GLSearchText) {
        } else {
            terminate_opengl_display();
        }
    }
}

void set_rotation_by_screen() {

    /*
     switch (GLInterfaceOrientation) {
 case UIInterfaceOrientationPortrait:
 glRotatef(-90.0, 0.0, 0.0, 1.0);
 break;
 case UIInterfaceOrientationPortraitUpsideDown:
 glRotatef(90.0, 0.0, 0.0, 1.0);
 break;
 case UIInterfaceOrientationLandscapeLeft:
 glRotatef(-90.0, 0.0, 0.0, 1.0);
 break;
 case UIInterfaceOrientationLandscapeRight:
 glRotatef(90.0, 0.0, 0.0, 1.0);
 break;
 default:
 glRotatef(-90.0, 0.0, 0.0, 1.0);
 break;
 }
     */
}

void prepare_opengl_display() {


    // NON Risolve
    // GLCurDisplayList1B = 1;

#ifdef JNI_DLL
#elif defined JNI_SO
#else
    glPushMatrix();

    if (GLrenderMode == GL_RENDER) {


#ifdef USE_BLEND
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
#else
        glDisable(GL_BLEND);
#endif

        glDisable(GL_CULL_FACE);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glDepthMask(GL_TRUE);
        glDepthRangef(0.0f, 1.0f);


    } else {
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_DITHER);
        glDisable(GL_BLEND);
    }






    glNormal3f(0.0, 0.0, 1.0);
    // glEnable(GL_AUTO_NORMAL);
    // glEnable(GL_NORMALIZE);




    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();



    if (GLScreenWY) {
        gluPerspective(30, (float) (GLScreenWX) / (float) (GLScreenWY - 40), -1.0f, 10.0f);
    }

    glViewport(1, 1, GLScreenWX - 2, GLScreenWY - 2);

    glOrthof(GLCameraX, GLCameraX + GLCameraWX, GLCameraY, GLCameraY + GLCameraWY, 0.0f, 10.0f);

    // my_printf("Ortho : X,Y WX,WY");
    // my_printf("%0.3f,%0.3f - %0.3f,%0.3f", GLCameraX, GLCameraY, GLCameraWX, GLCameraWY);


    set_rotation_by_screen();

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();




    glColor4ub(255, 255, 255, 255);

    glEnableClientState(GL_VERTEX_ARRAY);
    // glDisableClientState(GL_COLOR_ARRAY);
    // glDisableClientState(GL_TEXTURE_COORD_ARRAY);
    // glDisableClientState(GL_NORMAL_ARRAY);


    // Abilita lo spessore delle linee
    glDisable(GL_LINE_SMOOTH);

    glShadeModel(GL_SMOOTH);



#ifdef USE_COLOR_MATERIAL
    {
        float light_ambient[4] = {GLLightA, GLLightA, GLLightA, 1.0f};
        float light_diffuse[4] = {GLLightD, GLLightD, GLLightD, 1.0f};
        float light_specular[4] = {GLLightS, GLLightS, GLLightS, 1.0f};
        float light_pos[] = {GLCameraX + GLCameraWX / 2.0f, GLCameraY + GLCameraWY / 2.0f, GLLightPosZ, GLLightPosA};
        float light_dir[] = {-light_pos[0], -light_pos[1], -light_pos[2]};

        float LightModelAmbient[] = {GLLightM, GLLightM, GLLightM, 1.0f};


        GLCt += 0.01;


        glEnable(GL_LIGHTING);


#ifdef USE_LIGHT0
        glEnable(GL_LIGHT0);

        glLightfv(GL_LIGHT0, GL_AMBIENT, light_ambient);
        glLightfv(GL_LIGHT0, GL_DIFFUSE, light_diffuse);
        glLightfv(GL_LIGHT0, GL_SPECULAR, light_specular);

        glLightfv(GL_LIGHT0, GL_POSITION, light_pos);
        glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, light_dir);
#endif



        glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 90);
        // glLightf(GL_LIGHT0, GL_SPOT_EXPONENT, 20.0f);
        glLightf(GL_LIGHT0, GL_SPOT_EXPONENT, 0.0f);
        glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 1.0f);
        glLightf(GL_LIGHT0, GL_LINEAR_ATTENUATION, 0.0f);
        glLightf(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, 0.0f);


        glEnable(GL_COLOR_MATERIAL);


        glLightModelfv(GL_LIGHT_MODEL_AMBIENT, LightModelAmbient);

        glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);


        glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, GL_TRUE);
        glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_FALSE);
        glLightModeli(GL_LIGHT_MODEL_COLOR_CONTROL, GL_SEPARATE_SPECULAR_COLOR);
    }

#else
    glEnable(GL_LIGHTING);
#ifdef USE_LIGHT0
    glEnable(GL_LIGHT0);
#else
    glDisable(GL_LIGHT0);
#endif
#endif

#endif
}

void terminate_opengl_display() {

#ifdef JNI_DLL
#elif defined JNI_SO
#else
    if (GLrenderMode == GL_RENDER) {
    } else {
    }

    glPopMatrix();

#endif

}

void free_canvas_data() {
    unsigned int i;

    if (GLOutputCode) free(GLOutputCode);
    GLOutputCode = NULL;
    GLOutputCodeAllocated = 0;


    // Linee
    if (GLCanvasLines) free(GLCanvasLines);
    GLCanvasLines = NULL;
    GLCanvasLinesAllocated = 0;

    GLNumCanvasLines = 0;



    // Testi
    if (GLCanvasTexts) free(GLCanvasTexts);
    GLCanvasTexts = NULL;
    GLCanvasTextsAllocated = 0;

    GLCanvasTextsCounter = 0;
}

int add_xml_init_string(char **out_string, UINT *out_string_allocated) {
    char str[512];

    char *init_header_str = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"\
                        "<document><db>\n"\
                        "<title>${TITLE}</title>\n"\
                        "<recs>${RECS}</recs>\n"\
                        ;

    char *init_footer_str = ""\
                        "\n";

    sprintf(str, "<GLCameraX>%f</GLCameraX>\n"\
                "<GLCameraY>%f</GLCameraY>\n"\
                "<GLCameraWX>%f</GLCameraWX>\n"\
                "<GLCameraWY>%f</GLCameraWY>\n", GLCameraX, GLCameraY, GLCameraWX, GLCameraWY);

    AddStr(out_string, init_header_str, out_string_allocated);
    AddStr(out_string, str, out_string_allocated);
    AddStr(out_string, init_footer_str, out_string_allocated);
    return 1;
}

int add_xml_ending_string(char **out_string, UINT *out_string_allocated) {
    char str[512];

    char *ending_header_str = "</db>"\
                            "</document>\n";

    char *ending_footer_str = ""\
                            "\n";

    str[0] = 0;

    AddStr(out_string, ending_header_str, out_string_allocated);
    AddStr(out_string, str, out_string_allocated);
    AddStr(out_string, ending_footer_str, out_string_allocated);
    return 1;
}







