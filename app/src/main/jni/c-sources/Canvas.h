//
//  Canvas.h
//  DWGTester
//
//  Created by cristian andreon on 31/03/11.
//  Copyright 2011 CA. All rights reserved.
//


#ifndef CANVAS_H
    #ifdef EXTERN
        #ifdef __cplusplus
            #define CANVAS_H extern "C"
        #else
            #define CANVAS_H extern
        #endif
    #else
        #define CANVAS_H
    #endif


	typedef struct tag_CanvasData {

		float Version;
		float CameraX;
		float CameraY;
		float CameraWX;
		float CameraWY;
		char *CanvasLines;
		char *CanvasTexts;
		char *CanvasSurfaces;
		void *SpatialFilter;

		} CANVAS_DATA, *LP_CANVAS_DATA, **LPP_CANVAS_DATA;


    CANVAS_H int create_canvas_string_code ( int width, int height, char **out_string, UINT *out_string_allocated, void *pSpatialFilter, int Options );
    CANVAS_H int create_canvas_string_code_ex ( int width, int height, char **out_string, UINT *out_string_allocated, void *pvCanvasData, int Options );

    #endif