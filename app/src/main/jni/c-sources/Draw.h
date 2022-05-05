//
//  Draw.h
//  DWGTester
//
//  Created by cristian andreon on 31/03/11.
//  Copyright 2011 CA. All rights reserved.
//


#ifndef DRAW_H
    #ifdef EXTERN
        #ifdef __cplusplus
            #define DRAW_H extern "C"
        #else
            #define DRAW_H extern
        #endif
    #else
        #define DRAW_H
    #endif



    DRAW_H float GLCameraX, GLCameraY, GLCameraWX, GLCameraWY, GLCameraZ, GLCt;
    DRAW_H float GLCameraBackupX, GLCameraBackupY, GLCameraBackupWX, GLCameraBackupWY;
    DRAW_H float GLScreenWX, GLScreenWY;




    DRAW_H void prepare_opengl_display();
    DRAW_H void terminate_opengl_display();
    DRAW_H void draw_dwg ( void *ptr_dwg, int drawOptions );

    #endif