//
//  spatialFiler.h
//  iMuve
//
//  Created by cristian andreon on 1/07/16.
//  Copyright 2016 CA. All rights reserved.
//


#ifndef SPATIAL_FILTER_H

    #ifdef EXTERN
        #ifdef __cplusplus
            #define SPATIAL_FILTER_H extern "C"
        #else
            #define SPATIAL_FILTER_H extern
        #endif
    #else
        #define SPATIAL_FILTER_H
    #endif


	typedef struct tag_SpatialQuad {

			double X, Y, X2, Y2;
			double Long, Lat, Long2, Lat2;

			char *JSData;
			UINT NumJSData, NumJSDataAllocated;

		} SPATIAL_QUAD, *LP_SPATIAL_QUAD, **LPP_SPATIAL_QUAD;

	#define MAX_QUADS	32*32

	typedef struct tag_SpatialFilter {

			BOOL Active;
			unsigned int MaxEntities;
			unsigned char *FileName;
			int GeoRefMethod;
			double X, Y, X2, Y2, WX, WY;
			double Long, Lat, Long2, Lat2;

			SPATIAL_QUAD SpatialQuads[MAX_QUADS];
			unsigned int  NumSpatialQuads;
			unsigned int  NumSpatialQuadsCols, NumSpatialQuadsRecs;
			float quadWX, quadWY;

		} SPATIAL_FILTER, *LP_SPATIAL_FILTER, **LPP_SPATIAL_FILTER;


    // SPATIAL_FILTER_H void draw_dwg ( void *ptr_dwg, int drawOptions );


	SPATIAL_FILTER_H SPATIAL_FILTER GLSpatialFilter;

    #endif