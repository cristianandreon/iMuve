#ifdef JNI_DLL

#include <windows.h>
#include <math.h>
#include "c-sources/Draw.h"
#include "lib-dwg-master\dwg.h"
#include "utility/OpenGLWrapper.h"
#include "utility/WindowsToAndroid.h"
#include "spatialFilter/spatialFilter.h"


#include "base64encode/base64decode.h"

#define strcasecmp strcmpi

#elif defined JNI_SO

#include <math.h>
#include "iMuveCPP.h"
#include "c-sources/Draw.h"
#include "lib-dwg-master/dwg.h"

#include "utility/WindowsToLinux.h"
#include "utility/OpenGLWrapper.h"
#include "utility/memory_allocator.h"
#include "spatialFilter/spatialFilter.h"

#else

#include <math.h>

#include "utility/WindowsToAndroid.h"
#include "utility/OpenGLWrapper.h"
#include "utility/memory_allocator.h"
#include "spatialFilter/spatialFilter.h"

#include "iMuveCPP.h"
#include "c-sources/Draw.h"
#include "lib-dwg-master/dwg.h"
#include "lib-dwg-master/logging.h"


#endif



#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "jni.h"




#if defined(JNI_DLL) || defined(JNI_SO)
// xxxxx
#else
#endif



Dwg_Data GLDwgData = {0};
int GLDwgID = 0;






JNIEnv *GLJNIenv = NULL;
jobject GLJNIjobj = NULL;
char GLLogStr[512] = {0};



float GLCanvasVersion = 3.75;

JNIEXPORT jfloat JNICALL Java_com_imuve_cristian_imuve_MainActivity_GetVersion(JNIEnv *jenv, jobject jobj) {
    GLJNIenv = jenv;
    GLJNIjobj = jobj;

#ifdef JNI_SO

    /*
    my_printf("[sizeof(int):%d]", (int)sizeof(int));
    my_printf("[sizeof(long):%d]", (int)sizeof(long));
    my_printf("[sizeof(my_long):%d]", (int)sizeof(my_long));
    my_printf("[sizeof(void*):%d]", (int)sizeof(void *));

    my_printf("[GLNumErrStr:%d]", GLNumErrStr);
    my_printf("[my_realloc test:%ld]", (long)GLErrStr);

    int i=0;
    for (i=0; i<250; i++) {
        GLErrStr = my_realloc((void*)GLErrStr, 20000+i*100);
        my_printf("[%d-my_free test:%ld]", i, (long)GLErrStr);
    }

    my_free(GLErrStr);
        GLErrStr = NULL;

        my_printf("[Done]");
     */

    /*
    {	char str[256];
                unsigned int i = 0;

                strcpy (str, "***TEST***");
                for (i=0; i<2500; i++) {
                        AddStr (&GLCanvasLines, str, &GLCanvasLinesAllocated);
                        }
                if(GLCanvasLines) free(GLCanvasLines);
                GLCanvasLines = NULL;
                GLCanvasLinesAllocated = 0;
                }
        my_printf("[AddStr Test Done]");
     */

#endif



    return (GLCanvasVersion);
}

void zoom_to_extens(void) {
    GLCameraX = GLDwgData.header_vars.EXTMIN_MSPACE.x;
    GLCameraY = GLDwgData.header_vars.EXTMIN_MSPACE.y;
    GLCameraWX = GLDwgData.header_vars.EXTMAX_MSPACE.x - GLDwgData.header_vars.EXTMIN_MSPACE.x;
    GLCameraWY = GLDwgData.header_vars.EXTMAX_MSPACE.y - GLDwgData.header_vars.EXTMIN_MSPACE.y;

    if (GLScreenWX <= 0.0f)
        GLScreenWX = 1200.0f;
    if (GLScreenWY <= 0.0f)
        GLScreenWY = 660.0f;

    float Ratio = GLScreenWY / GLScreenWX;
    if (GLCameraWY / GLCameraWX > Ratio) {
        GLCameraWX = GLCameraWY / Ratio;
    } else {
        GLCameraWY = GLCameraWX * Ratio;
    }

    GLCameraZ = 1.0f;
    GLCt = 0.0f;
}

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_LoadDwg(JNIEnv *jenv, jobject jobj, jbyteArray pData, jint pDataSize, jint dwgID) {
    GLJNIenv = jenv;
    GLJNIjobj = jobj;

    if (dwgID > 0) {
        if (GLDwgID == dwgID) {
            return GLDwgData.num_objects;
        }
    }

    GLDwgData.num_objects = 0;

    if (pData) {
        jboolean isCopy;
        char *cData = (*jenv)->GetByteArrayElements(jenv, pData, &isCopy);
        char Version[6] = {0}, str[256];

#ifdef JNI_SO
        my_printf("[LoadDwg...]");
#endif

        FREE_POINTER(GLErrStr);
        GLErrStrAllocated = 0;
        GLNumErrStr = 0;


        if (cData) {

            dwg_free(&GLDwgData);

            strncpy(Version, (char *) &cData[2], 4);
            if (atoi(Version) >= 1021 || atoi(Version) < 1012) {
                sprintf(str, "INVALID DWG SIGN.:%c%c%c%c%c%c", cData[0], cData[1], cData[2], cData[3], cData[4], cData[5]);
                CpyStr(&GLErrStr, str, &GLErrStrAllocated);
                my_printf(str);
                return 0;
            }

            my_printf("DWG SIGN.:%c%c%c%c%c%c", cData[0], cData[1], cData[2], cData[3], cData[4], cData[5]);


            if (dwg_read_data((char*) cData, (unsigned int) pDataSize, (Dwg_Data *) & GLDwgData) > 0) {

                sprintf(str, "Error decoding dwg!");
                CpyStr(&GLErrStr, str, &GLErrStrAllocated);
                my_printf(str);
                return 0;

            } else {

                zoom_to_extens();

                if (dwgID > 0) {
                    GLDwgID = dwgID;
                } else {
                    GLDwgID = 0;
                }
            }


        } else {
            return 0;
        }
    }

    return GLDwgData.num_objects;
}

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_GetDwgID(JNIEnv *jenv, jobject jobj, jint Options) {
    return GLDwgID;
}

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_SetLayer(JNIEnv *jenv, jobject jobj, jstring jstr_layer_name, jboolean isON) {
    UINT i;
    char str[256], layer_name[256];
    int retVal = 0;

    GLJNIenv = jenv;
    GLJNIjobj = jobj;



    {
        const char *nativeString = (*jenv)->GetStringUTFChars(jenv, jstr_layer_name, 0);
        int len = strlen(nativeString ? nativeString : "");
        if (len>sizeof (layer_name)) len = sizeof (layer_name);
        strncpy(layer_name, nativeString, len);
        layer_name[len] = 0;


        for (i = 0; i < GLDwgData.num_objects; i++) {
            if (GLDwgData.object[i].type == DWG_TYPE_LAYER) {
                Dwg_Object_LAYER *layer = GLDwgData.object[i].tio.object->tio.LAYER;

                strcpy(str, layer->entry_name ? layer->entry_name : "");

                if (str[0]) {
                    if (str[strlen(str) - 1] == '*') {
                        str[strlen(str) - 1] = 0;
                    }

                    if (strcasecmp(layer_name, str) == 0) {
                        if (isON) {
                            if (layer->values & 2) layer->values -= 2;
                        } else {
                            if (!(layer->values & 2)) layer->values += 2;
                        }

                        retVal = 1;
                    }
                }
            }
        }
    }


    return retVal;
}

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_GetLayers(JNIEnv *jenv, jobject jobj) {
    UINT i;
    char str[256];
    int retVal = 0;

    GLJNIenv = jenv;
    GLJNIjobj = jobj;

    for (i = 0; i < GLDwgData.num_objects; i++) {
        if (GLDwgData.object[i].type == DWG_TYPE_LAYER) {
            Dwg_Object_LAYER *layer = GLDwgData.object[i].tio.object->tio.LAYER;

            strcpy(str, layer->entry_name ? layer->entry_name : "");
            if (str[0]) {
                if (str[strlen(str) - 1] == '*') {
                    str[strlen(str) - 1] = 0;
                }
                if (strcasecmp(str, "Defpoints") != 0) {
                    jstring Layerjstr = NULL, gLayerjstr = NULL;
                    jclass jcl = NULL;

                    jcl = (*GLJNIenv)->FindClass(GLJNIenv, "com/imuve/cristian/imuve/MainActivity");

                    // try {
                    {
                        jclass gjcl = (*GLJNIenv)->NewGlobalRef(GLJNIenv, jcl);
                        if (gjcl) {
                            jmethodID methodId = NULL;

                            Layerjstr = (*GLJNIenv)->NewStringUTF(GLJNIenv, str);
                            gLayerjstr = (*GLJNIenv)->NewGlobalRef(GLJNIenv, Layerjstr);
                            methodId = (*GLJNIenv)->GetMethodID(GLJNIenv, gjcl, "onGetLayers", "(Ljava/lang/String;I)V");

                            if (methodId) {
                                (*GLJNIenv)->CallVoidMethod(GLJNIenv, GLJNIjobj, methodId, gLayerjstr, layer->values);
                            }
                        }

                        if (Layerjstr) (*GLJNIenv)->DeleteLocalRef(GLJNIenv, Layerjstr);
                        if (jcl) (*GLJNIenv)->DeleteLocalRef(GLJNIenv, jcl);

                        if (gLayerjstr) (*GLJNIenv)->DeleteGlobalRef(GLJNIenv, gLayerjstr);
                        if (gjcl) (*GLJNIenv)->DeleteGlobalRef(GLJNIenv, gjcl);
                    }

                    retVal++;
                }
            }
        }
    }

    return retVal;
}

JNIEXPORT jstring JNICALL Java_com_imuve_cristian_imuve_MainActivity_GetError(JNIEnv *jenv, jobject jobj) {

    /*
    try {
      } catch (...) {
      }
     */
    jstring jString = (*jenv)->NewStringUTF(jenv, GLErrStr ? GLErrStr : "");

    FREE_POINTER(GLErrStr);
    GLErrStrAllocated = 0;
    GLNumErrStr = 0;

    return jString;
}


static BOOL GLIsBusy = FALSE;

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_DrawDwg(JNIEnv *pjenv, jobject jobj, jint curTab, jint drawOptions) {
    JavaVM *jvm = 0;
    JNIEnv *jenv = pjenv;

    if (GLIsBusy) return 0;


    GLIsBusy = TRUE;

    (*jenv)->GetJavaVM(jenv, &jvm);

    if (jvm) {
        (*jvm)->AttachCurrentThread(jvm, &jenv, NULL);
    } else {
        my_printf("DrawDwg : no jvm!");
    }

    // GLJNIenv = jenv->NewGlobalRef(jenv);
    GLJNIenv = jenv;
    GLJNIjobj = jobj;

    // my_printf("Draw DWG : %d entities", GLDwgData.num_objects);

    if (GLDwgData.num_objects) {
        draw_dwg(&GLDwgData, drawOptions);
    }


    // jenv->DeleteGlobalRef(jenv, GLJNIenv);

    GLIsBusy = FALSE;

    return GLDwgData.num_objects;
}

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_Pan(JNIEnv *jenv, jobject jobj, jfloat dx, jfloat dy, jint isTransacting) {
    GLJNIenv = jenv;
    GLJNIjobj = jobj;

    // my_printf("Pan DWG : %d entities", GLDwgData.num_objects);

    GLCameraX -= GLCameraWX * dx / GLScreenWX;
    GLCameraY += GLCameraWY * dy / GLScreenWY;

    if (!isTransacting) my_printf("Pan : GLCameraX:%f - GLCameraY:%f", GLCameraX, GLCameraY);

    return 1;
}

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_Zoom(JNIEnv *jenv, jobject jobj, jfloat camera_cx, jfloat camera_cy, jfloat zoom, jint isTransacting) {
    GLJNIenv = jenv;
    GLJNIjobj = jobj;


    if (zoom < 0.001f) zoom = 0.001f;
    if (zoom > 1000.0f) zoom = 10000.0f;

    if (fabs(zoom) >= 0.001f && fabs(zoom) <= 1000.0f) {

        float camera_x = GLCameraX;
        float camera_y = GLCameraY;

        float camera_wx = GLCameraWX;
        float camera_wy = GLCameraWY;

        GLCameraWX /= zoom;
        GLCameraWY /= zoom;

        GLCameraX -= (camera_cx - camera_x) / camera_wx * (GLCameraWX - camera_wx);
        GLCameraY -= (camera_cy - camera_y) / camera_wy * (GLCameraWY - camera_wy);


        if (!isTransacting) my_printf("Zoom Camera : [%0.1f-%0.1f] - [%0.1f-%0.1f]  ", GLCameraX, GLCameraY, GLCameraWX, GLCameraWY);
    }


    return 1;
}

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_PushCamera(JNIEnv *jenv, jobject jobj) {
    GLCameraX = GLCameraBackupX;
    GLCameraY = GLCameraBackupY;
    GLCameraWY = GLCameraBackupWX;
    GLCameraWX = GLCameraBackupWY;
    return 1;
}

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_PopCamera(JNIEnv *jenv, jobject jobj) {
    GLCameraBackupX = GLCameraX;
    GLCameraBackupY = GLCameraY;
    GLCameraBackupWX = GLCameraWY;
    GLCameraBackupWY = GLCameraWX;
    return 1;
}

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_onClickIntToDwg(JNIEnv *jenv, jobject jobj, jfloat touchX, jfloat touchY, jint Event, jboolean reSearch) {
    int OnClickCount = 0;
    float OnClickGap = GLOnClickGap;

    GLJNIenv = jenv;
    GLJNIjobj = jobj;

    GLOnClickGap = 0.1;
    GLOnClickCount = 0;
    GLOnClick = TRUE;

try_to_search:

    my_printf("onClick screen: %0.3f - %0.3f", touchX, touchY);

    GLOnClickX = GLCameraX + touchX / GLScreenWX * GLCameraWX;
    GLOnClickY = GLCameraY + touchY / GLScreenWY * GLCameraWY;

    my_printf("onClick dwg: %0.3f - %0.3f", GLOnClickX, GLOnClickY);

    // Codice di disegno
    draw_dwg(&GLDwgData, 0 + 0);

    if (!GLOnClickCount) {
        if (reSearch && OnClickCount < 3) {
            GLOnClickGap += 0.15f;
            OnClickCount++;
            goto try_to_search;
        }
    }

    GLOnClick = FALSE;
    GLOnClickGap = OnClickGap;

    return GLOnClickCount;
}

JNIEXPORT jstring JNICALL Java_com_imuve_cristian_imuve_MainActivity_DwgToCanvas(JNIEnv *jenv, jobject jobj, jint width, jint height, jint Options) {
    UINT i;


#ifdef JNI_DLL

#elif defined JNI_SO


    GLJNIenv = jenv;
    GLJNIjobj = jobj;

    GLOutputToCanvas = TRUE;


    free_canvas_data();



    GLdrawOptions = 0 + 0;

    /////////////////////////////////////////
    // Codice di disegno a stato Prepare
    //
    GLOutputToCanvasStep = 0;
    draw_dwg(&GLDwgData, 0 + 0);



    /////////////////////////////////////////
    // Codice di disegno a stato Output
    //

    GLOutputToCanvasStep = 2;
    draw_dwg(&GLDwgData, 0 + 0);




    //////////////////////////////
    // Preparazione codice js
    //
    create_canvas_string_code(width, height, &GLOutputCode, &GLOutputCodeAllocated, NULL, 0 + 0);



    GLNumCanvasLines = 0;

    GLOutputToCanvas = FALSE;

    {
        jstring jString = (*jenv)->NewStringUTF(jenv, GLOutputCode ? GLOutputCode : "");

        free_canvas_data();

        return jString;
    }
#else
#endif

    return NULL;
}

JNIEXPORT jstring JNICALL Java_com_imuve_cristian_imuve_MainActivity_DwgToCanvasEx(JNIEnv *jenv, jobject jobj, jint width, jint height,
        jstring fileName, jint maxEntities,
        jint geoRefMethod,
        jdouble Long, jdouble Lat, jdouble Long2, jdouble Lat2,
        jint Options) {
    UINT i;
    char str[256];
    BOOL DebugMode = TRUE;


    GLJNIenv = jenv;
    GLJNIjobj = jobj;


    GLOutputToCanvas = TRUE;


    free_canvas_data();


#ifdef xxx

#elif defined JNI_SO || JNI_DLL

    GLSpatialFilter.Active = TRUE;
    GLSpatialFilter.GeoRefMethod = geoRefMethod;
    GLSpatialFilter.Long = Long;
    GLSpatialFilter.Lat = Lat;
    GLSpatialFilter.Long2 = Long2;
    GLSpatialFilter.Lat2 = Lat2;

    GLSpatialFilter.MaxEntities = maxEntities;


    {
        const char *nativeString = (*jenv)->GetStringUTFChars(jenv, fileName, 0);
        // COPY_POINTER(GLSpatialFilter.FileName, nativeString);
        GLSpatialFilter.FileName = nativeString;
    }




    /////////////////////////////////////////
    // Calcolo numero di quadranti
    //
    {
        UINT numQuads = (GLDwgData.num_objects / maxEntities), ix, iy;
        float Ratio = 1.0f;

        if (numQuads > MAX_QUADS) {
            numQuads = MAX_QUADS;
            GLSpatialFilter.MaxEntities = GLDwgData.num_objects / numQuads;
        }


        GLSpatialFilter.X = GLDwgData.header_vars.EXTMIN_MSPACE.x;
        GLSpatialFilter.X2 = GLDwgData.header_vars.EXTMAX_MSPACE.x;
        GLSpatialFilter.Y = GLDwgData.header_vars.EXTMIN_MSPACE.y;
        GLSpatialFilter.Y2 = GLDwgData.header_vars.EXTMAX_MSPACE.y;

        GLSpatialFilter.WX = GLSpatialFilter.X2 - GLSpatialFilter.X;
        GLSpatialFilter.WY = GLSpatialFilter.Y2 - GLSpatialFilter.Y;

        Ratio = GLSpatialFilter.WX / GLSpatialFilter.WY;

        GLSpatialFilter.NumSpatialQuadsCols = (uint) ceil(sqrt((double) numQuads * (double) Ratio));
        if (GLSpatialFilter.NumSpatialQuadsCols < 1) GLSpatialFilter.NumSpatialQuadsCols = 1;
        GLSpatialFilter.NumSpatialQuadsRecs = (uint) ceil((double) numQuads / (double) GLSpatialFilter.NumSpatialQuadsCols);
        GLSpatialFilter.NumSpatialQuads = GLSpatialFilter.NumSpatialQuadsCols * GLSpatialFilter.NumSpatialQuadsRecs;


        GLSpatialFilter.quadWX = GLSpatialFilter.WX / GLSpatialFilter.NumSpatialQuadsCols;
        GLSpatialFilter.quadWY = GLSpatialFilter.WY / GLSpatialFilter.NumSpatialQuadsRecs;

        for (iy = 0; iy < GLSpatialFilter.NumSpatialQuadsRecs; iy++) {
            for (ix = 0; ix < GLSpatialFilter.NumSpatialQuadsCols; ix++) {
                GLSpatialFilter.SpatialQuads[ix + iy * GLSpatialFilter.NumSpatialQuadsCols].X = GLSpatialFilter.X + ix * GLSpatialFilter.quadWX;
                GLSpatialFilter.SpatialQuads[ix + iy * GLSpatialFilter.NumSpatialQuadsCols].Y = GLSpatialFilter.Y + iy * GLSpatialFilter.quadWY;
                GLSpatialFilter.SpatialQuads[ix + iy * GLSpatialFilter.NumSpatialQuadsCols].X2 = GLSpatialFilter.SpatialQuads[ix + iy * GLSpatialFilter.NumSpatialQuadsCols].X + GLSpatialFilter.quadWX;
                GLSpatialFilter.SpatialQuads[ix + iy * GLSpatialFilter.NumSpatialQuadsCols].Y2 = GLSpatialFilter.SpatialQuads[ix + iy * GLSpatialFilter.NumSpatialQuadsCols].Y + GLSpatialFilter.quadWY;
            }
        }
    }


    GLdrawOptions = 0 + 0;

    /////////////////////////////////////////
    // Codice di disegno a stato Prepare
    //
    GLOutputToCanvasStep = 0;
    draw_dwg(&GLDwgData, 0 + 0);



    /////////////////////////////////////////
    // Codice di disegno a stato Output
    //

    GLOutputToCanvasStep = 2;
    draw_dwg(&GLDwgData, 0 + 0);




    /////////////////////////////////////////
    // Suddivisione spaziale
    //



    // Preparazione codice json
    if (DebugMode) {
    } else {
        AddStr(&GLOutputCode, "{spatial_filtered_dwg:[\r\n", &GLOutputCodeAllocated);
    }


    /////////////////////////////////////////
    // Camera iniziale
    //
    GLCameraX = GLSpatialFilter.SpatialQuads[0].X;
    GLCameraY = GLSpatialFilter.SpatialQuads[0].Y;
    GLCameraWX = GLSpatialFilter.SpatialQuads[0].X2 - GLSpatialFilter.SpatialQuads[0].X;
    GLCameraWY = GLSpatialFilter.SpatialQuads[0].Y2 - GLSpatialFilter.SpatialQuads[0].Y;




    //////////////////////////////
    // Preparazione codice js
    //
    {
        char *CanvasCode = NULL;
        UINT CanvasCodeAllocated = 0;

        // Options & BIT1	->	Modalità spatial Filter
        // Options & BIT2	->	Modalità Trasparente
        create_canvas_string_code(width, height, &CanvasCode, &CanvasCodeAllocated, &GLSpatialFilter, 0 + 1 + 2);

        AddStr(&GLOutputCode, CanvasCode, &GLOutputCodeNumObj);

        FREE_POINTER(CanvasCode);
        CanvasCodeAllocated = 0;
    }


    if (DebugMode) {

    } else {

        AddStr(&GLOutputCode, "\r\n]\r\n", &GLOutputCodeNumObj);
        sprintf(str, ",\"numObjects\":\"%d\"", GLDwgData.num_objects);
        AddStr(&GLOutputCode, str, &GLOutputCodeAllocated);
        sprintf(str, ",\"noQuads\":\"%d\"\r\n", GLSpatialFilter.NumSpatialQuads);
        sprintf(str, ",\"noEntitiesPerQuad\":\"%d\"\r\n", GLSpatialFilter.MaxEntities);
        AddStr(&GLOutputCode, str, &GLOutputCodeAllocated);

        for (i = 0; i < GLSpatialFilter.NumSpatialQuads; i++) {
            sprintf(str, ",\"quadData#%d\":\"", i + 1);
            AddStr(&GLOutputCode, str, &GLOutputCodeAllocated);

            AddStr(&GLOutputCode, GLSpatialFilter.SpatialQuads[i].JSData, &GLOutputCodeAllocated);

            sprintf(str, "\"\r\n");
            AddStr(&GLOutputCode, str, &GLOutputCodeAllocated);
        }

        AddStr(&GLOutputCode, "}", &GLOutputCodeAllocated);
    }


    GLNumCanvasLines = 0;

    GLOutputToCanvas = FALSE;

    GLSpatialFilter.Active = FALSE;


#else
    GLOutputCode[0] = 0;
#endif


    {
        jstring jString = (*jenv)->NewStringUTF(jenv, GLOutputCode ? GLOutputCode : "");

        free_canvas_data();

        return jString;
    }
}







// Options & BIT1   ->  Flat text file

JNIEXPORT jstring JNICALL Java_com_imuve_cristian_imuve_MainActivity_GetDwgTags(JNIEnv *jenv, jobject jobj, jint Options) {
    UINT i;


    GLJNIenv = jenv;
    GLJNIjobj = jobj;

    GLOutputToXML = TRUE;
    GLOutputToXMLOptions = Options;
    if (GLOutputCode) free(GLOutputCode);
    GLOutputCode = NULL;
    GLOutputCodeAllocated = 0;
    GLOutputCodeNumObj = 0;


    if (Options & 1) {
        // Preparazione codice xml
        add_xml_init_string(&GLOutputCode, &GLOutputCodeAllocated);

        AddStr(&GLOutputCode, "<TextData>", &GLOutputCodeAllocated);
    } else {
        // Flat text file
    }


    // Codice di disegno
    draw_dwg(&GLDwgData, 0 + 0);


    if (Options & 1) {
        AddStr(&GLOutputCode, "</TextData>", &GLOutputCodeAllocated);
        add_xml_ending_string(&GLOutputCode, &GLOutputCodeAllocated);
    } else {
        // Flat text file
    }


    GLOutputToXML = FALSE;

    {
        jstring jString = NULL;

        if (Options == -1) {
            jString = (*jenv)->NewStringUTF(jenv, GLErrStr ? GLErrStr : "");

            if (GLErrStr) free(GLErrStr);
            GLErrStr = NULL;
            GLErrStrAllocated = 0;
            GLNumErrStr = 0;

        } else {
            jString = (*jenv)->NewStringUTF(jenv, GLOutputCode ? GLOutputCode : "");

            if (GLOutputCode) free(GLOutputCode);
            GLOutputCode = NULL;
            GLOutputCodeAllocated = FALSE;
        }

        return jString;
    }

}

JNIEXPORT jstring JNICALL Java_com_imuve_cristian_imuve_MainActivity_DwgToJSON(JNIEnv *jenv, jobject jobj, jint Aux, jint Options) {
    UINT i;
    char str[256];
    GLJNIenv = jenv;
    GLJNIjobj = jobj;

    GLOutputToJSON = TRUE;
    if (GLOutputCode) free(GLOutputCode);
    GLOutputCode = NULL;
    GLOutputCodeAllocated = 0;
    GLOutputCodeNumObj = 0;


    // Preparazione codice json
    AddStr(&GLOutputCode, "{dwg:[\r\n", &GLOutputCodeAllocated);

    // Codice di disegno
    draw_dwg(&GLDwgData, 0 + 0);


    AddStr(&GLOutputCode, "\r\n]\r\n,", &GLOutputCodeNumObj);
    sprintf(str, "\"no_keytext\":\"%d\"", GLOutputCodeNumObj);
    AddStr(&GLOutputCode, str, &GLOutputCodeAllocated);
    AddStr(&GLOutputCode, "\r\n}", &GLOutputCodeAllocated);

    GLOutputToJSON = FALSE;

    {
        jstring jString = (*jenv)->NewStringUTF(jenv, GLOutputCode ? GLOutputCode : "");

        if (GLOutputCode) free(GLOutputCode);
        GLOutputCodeAllocated = FALSE;

        return jString;
    }
}




// Options == 0	->	Exact Key search
// Options == 1	->	Free search
// Options == 2	->	Exact search

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_FindDwgText(JNIEnv *jenv, jobject jobj, jstring jstrSearch, jint Options) {
    UINT i;
    char str[256];
    GLJNIenv = jenv;
    GLJNIjobj = jobj;

    GLSearchText = TRUE;
    GLOnClickCount = 0;

    // Modalità ricerca
    GLSearchTextMode = Options;


    {
        const char *nativeString = (*jenv)->GetStringUTFChars(jenv, jstrSearch, 0);
        int len = strlen(nativeString ? nativeString : "");
        if (len>sizeof (GLSearchTextValue)) len = sizeof (GLSearchTextValue);
        strncpy(GLSearchTextValue, nativeString, len);
        GLSearchTextValue[len] = 0;
    }


    // Codice di disegno
    draw_dwg(&GLDwgData, 0 + 0);


    GLSearchText = FALSE;


    return GLOnClickCount;
}




// Mode = 2 ->  Show all
// Mode = 1 ->  Show if found
// Mode = 0 ->  Hide if found

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_FilterDwgText(JNIEnv *jenv, jobject jobj, jstring jstrFilters, jint numFilters, jint Mode) {
    UINT i, i_filt = 0, n_filt = 0, n_key_found = 0;
    char **str_array = (char**) calloc(1, sizeof (char*) *(numFilters + 1));
    GLJNIenv = jenv;
    GLJNIjobj = jobj;


    if (str_array) {
        const char *nativeString = (*jenv)->GetStringUTFChars(jenv, jstrFilters, 0);
        int len = strlen(nativeString ? nativeString : ""), start = 0;
        for (i = 0; i < len + 1; i++) {
            if (nativeString[i] == ',' || nativeString[i] == 0) {
                unsigned int needed = i - start + 1;
                str_array[i_filt] = (char*) malloc(needed + 1);
                if (str_array[i_filt]) {
                    memcpy(str_array[i_filt], &nativeString[start], needed - 1);
                    str_array[i_filt][needed - 1] = 0;
                    i_filt++;
                    start = i + 1;
                }
            }
        }
    } else {
        return 0;
    }

    n_filt = i_filt;

    for (i = 0; i < GLDwgData.num_objects; i++) {
        if (GLDwgData.object[i].supertype == DWG_SUPERTYPE_ENTITY || GLDwgData.object[i].supertype == DWG_SUPERTYPE_UNKNOWN) {
            switch (GLDwgData.object[i].type) {
                case DWG_TYPE_TEXT:
                {
                    Dwg_Entity_TEXT *ptext = GLDwgData.object[i].tio.entity->tio.TEXT;
                    char text[256], key[128];
                    prepare_key_from_text(ptext->text_value, key, text, FALSE);
                    if (Mode == 0) {
                        // Hide if found
                        if (ptext->RTFlag & 1) ptext->RTFlag -= 1;
                    } else if (Mode == 1) {
                        // Show if found
                        if (!(ptext->RTFlag & 1)) ptext->RTFlag += 1;
                    } else if (Mode == 2) {
                        // Show all
                        if (ptext->RTFlag & 1) ptext->RTFlag -= 1;
                    }

                    if (key[0] != 0 && Mode != 2) {
                        for (i_filt = 0; i_filt < n_filt; i_filt++) {
                            if (strcasecmp(text, str_array[i_filt]) == 0) {
                                n_key_found++;
                                if (Mode == 0) {
                                    // Hide if found
                                    if (!(ptext->RTFlag & 1)) ptext->RTFlag += 1;
                                } else {
                                    // Show if found
                                    // Show all
                                    if (ptext->RTFlag & 1) ptext->RTFlag -= 1;
                                }
                                break;
                            }
                        }
                    } else {
                        if (ptext->RTFlag & 1) ptext->RTFlag -= 1;
                    }
                    break;
                }

                case DWG_TYPE_MTEXT:
                {
                    Dwg_Entity_MTEXT *ptext = GLDwgData.object[i].tio.entity->tio.MTEXT;
                    char text[256], key[128];
                    prepare_key_from_text(ptext->text, key, text, TRUE);
                    if (Mode == 0) {
                        // Hide if found
                        if (ptext->RTFlag & 1) ptext->RTFlag -= 1;
                    } else if (Mode == 1) {
                        // Show if found
                        if (!(ptext->RTFlag & 1)) ptext->RTFlag += 1;
                    } else if (Mode == 2) {
                        // Show all
                        if (ptext->RTFlag & 1) ptext->RTFlag -= 1;
                    }
                    if (key[0] != 0 && Mode != 2) {
                        for (i_filt = 0; i_filt < n_filt; i_filt++) {
                            if (strcasecmp(text, str_array[i_filt]) == 0) {
                                n_key_found++;
                                if (Mode == 0) {
                                    // Hide if found
                                    if (!(ptext->RTFlag & 1)) ptext->RTFlag += 1;
                                } else {
                                    // Show if found
                                    // Show all
                                    if (ptext->RTFlag & 1) ptext->RTFlag -= 1;
                                }
                                break;
                            }
                        }
                    } else {
                        if (ptext->RTFlag & 1) ptext->RTFlag -= 1;
                    }
                    break;
                }
            }
        }
    }



    for (i_filt = 0; i_filt < n_filt; i_filt++) {
        if (str_array[i_filt]) free(str_array[i_filt]);
    }
    free(str_array);


    return n_key_found;
}

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_SetCamera(JNIEnv *jenv, jobject jobj, jfloat CameraX, jfloat CameraY, jfloat CameraWX, jfloat CameraWY) {
    if (CameraWX > 0.0f && CameraWY > 0.0f) {
        GLCameraX = CameraX;
        GLCameraY = CameraY;
        GLCameraWX = CameraWX;
        // GLCameraWY = CameraWY;
        GLCameraWY = GLCameraWX * (GLScreenWY / GLScreenWX);
        return 1;
    } else {
        zoom_to_extens();
        return 2;
    }
}

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_SetScreen(JNIEnv *jenv, jobject jobj, jfloat ScreenWX, jfloat ScreenWY) {
    if (ScreenWX > 0.0f && ScreenWY > 0.0f) {
        GLScreenWX = ScreenWX;
        GLScreenWY = ScreenWY;
        return 1;
    } else {
        zoom_to_extens();
        return 1;
    }
}

JNIEXPORT jstring JNICALL Java_com_imuve_cristian_imuve_MainActivity_WrapText(JNIEnv *jenv, jobject jobj, jstring jText, jint Mode) {
    UINT i;
    char text[256], str[256];
    GLJNIenv = jenv;
    GLJNIjobj = jobj;


    {
        const char *nativeString = (*jenv)->GetStringUTFChars(jenv, jText, 0);
        int len = nativeString ? strlen(nativeString) : 0;
        if (len >= sizeof (text)) len = sizeof (text);
        strncpy(text, nativeString, len);
        text[len] = 0;
    }

    // Correzione sul testo
    strncpy(str, text, sizeof (str));
    if (wrap_text_for_search(text, str, sizeof (str)) < 0) {
    }

    if (Mode == 1) {
        prepare_key_from_text(str, text);
        return ( *jenv)->NewStringUTF(jenv, text);
    } else {
        return ( *jenv)->NewStringUTF(jenv, str);
    }
}

JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_TestKey(JNIEnv *jenv, jobject jobj, jstring jText) {
    UINT i;
    char text[256], str[256], key[256];
    GLJNIenv = jenv;
    GLJNIjobj = jobj;


    {
        const char *nativeString = (*jenv)->GetStringUTFChars(jenv, jText, 0);
        int len = nativeString ? strlen(nativeString) : 0;
        if (len >= sizeof (text)) len = sizeof (text);
        strncpy(text, nativeString, len);
        text[len] = 0;
    }

    // Correzione sul testo
    strncpy(str, text, sizeof (str));
    prepare_key_from_text(text, key, str, 0);
    if (key[0]) {
            return 1;
        } else {
            return 0;
        }
    }

    JNIEXPORT jfloat JNICALL Java_com_imuve_cristian_imuve_MainActivity_GetCamera(JNIEnv *jenv, jobject jobj, jint Options) {
        // char str[512];

        switch (Options) {

            case 0:
                return GLCameraX;
                break;
            case 1:
                return GLCameraY;
                break;
            case 2:
                return GLCameraWX;
                break;
            case 3:
                return GLCameraWY;
                break;

            default:
                return 0.0f;
                break;
        }



        // sprintf(str, "%0.3f,%0.3f - %0.3f,%0.3f", GLCameraX, GLCameraY, GLCameraWX, GLCameraWY);
        // jstring jString = ( *jenv )->NewStringUTF(jenv, str);

        return 0.0f;
    }




    int GLBase64OutputLength = 0;
    char *GLBase64Output = NULL;

    JNIEXPORT jstring JNICALL Java_com_imuve_cristian_imuve_MainActivity_Base64Decode(JNIEnv *jenv, jobject jobj, jstring jstrData) {
        const char *nativeString = (*jenv)->GetStringUTFChars(jenv, jstrData, 0);
        int len = nativeString ? strlen(nativeString) : 0;

        FREE_POINTER(GLBase64Output);

        GLBase64OutputLength = 0;

        // Da implementare
        // GLBase64Output = NewBase64Encode(nativeString, len, FALSE, &GLBase64OutputLength);

        return ( *jenv)->NewStringUTF(jenv, GLBase64Output);
    }

    JNIEXPORT jint JNICALL Java_com_imuve_cristian_imuve_MainActivity_Base64DecodeGetSize(JNIEnv *jenv, jobject jobj) {
        int Base64OutputLength = GLBase64OutputLength;
        FREE_POINTER(GLBase64Output);
        GLBase64OutputLength = 0;
        return Base64OutputLength;
    }




#ifdef JNI_DLL

    // #elif defined JNI_SO
#else

    /*
    #include <string.h>
    #include <ctype.h>
    #include <sys/types.h>
     */

    int strnicmp(char const *s1, char const *s2, size_t len) {
        unsigned char c1 = '\0';
        unsigned char c2 = '\0';
        if (len > 0) {
            do {
                c1 = *s1;
                c2 = *s2;
                s1++;
                s2++;
                if (!c1)
                    break;
                if (!c2)
                    break;
                if (c1 == c2)
                    continue;
                c1 = tolower(c1);
                c2 = tolower(c2);
                if (c1 != c2)
                    break;
            } while (--len);
        }
        return (int) c1 - (int) c2;
    }
#pragma weak strncasecmp=strnicmp

#endif
