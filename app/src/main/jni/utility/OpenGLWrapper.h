#ifdef JNI_DLL
	// #include "gl\gl.h"
	#include "jni-gl.h"
	#pragma comment (lib, "opengl32.lib")
	#pragma comment (lib, "glu32.lib")
	// #pragma comment (lib, "glaux.lib")
	#elif defined JNI_SO
	#include "jni-gl-linux.h"
	#else
	#include <GLES/gl.h>
	#include <GLES2/gl2.h>
	#include <GLES2/gl2ext.h>
	#include <GLES2/gl2platform.h>
	#endif



#ifdef JNI_DLL
	#define GLenum int
	#define GLfloat float
	#define GLint int
	#elif defined JNI_SO
	#define GLenum int
	#define GLfloat float
	#define GLint int
	#else
	#define APIENTRY

	#ifndef max
		#define max(_a,_b)	_a > _b ? _a : _b;
		#endif
	#ifndef min
		#define min(_a,_b)	_a < _b ? _a : _b;
		#endif

	#endif



/*
#define GL_VERTEX_ARRAY                   0x8074
#define GL_NORMAL_ARRAY                   0x8075
#define GL_COLOR_ARRAY                    0x8076
#define GL_TEXTURE_COORD_ARRAY            0x8078
*/

#ifndef OPENGLWRAPPER_KEY
    #ifdef EXTERN
	#ifdef __cplusplus
		#define OPENGLWRAPPER_KEY   extern "C"
		#else
		#define OPENGLWRAPPER_KEY   extern
		#endif
    	#else
		#define OPENGLWRAPPER_KEY
    	#endif


	OPENGLWRAPPER_KEY float GLWidth;

	#ifdef JNI_DLL
		OPENGLWRAPPER_KEY void gluLineWidth(float wh);
		#elif defined JNI_SO
		OPENGLWRAPPER_KEY void gluLineWidth(float wh);
		#else
		#define gluLineWidth glLineWidth
		#endif

	OPENGLWRAPPER_KEY long GLquadObj;
	OPENGLWRAPPER_KEY int GLNumArcSubDivision;

	OPENGLWRAPPER_KEY int GLCurDisplayListMode;
	OPENGLWRAPPER_KEY unsigned int  GLCurDisplayListBase;
	OPENGLWRAPPER_KEY unsigned int  GLCurDisplayList1B;
	OPENGLWRAPPER_KEY unsigned int  GLNumDisplayList;
	OPENGLWRAPPER_KEY unsigned int  GLNumDisplayListAllocated;

	OPENGLWRAPPER_KEY int GLdrawOptions;

	OPENGLWRAPPER_KEY BOOL GLOutputToXML;
	OPENGLWRAPPER_KEY BOOL GLOutputToJSON;
	OPENGLWRAPPER_KEY BOOL GLOutputToCanvas;
	OPENGLWRAPPER_KEY int GLOutputToCanvasStep;
	OPENGLWRAPPER_KEY int GLOutputToXMLOptions;
	OPENGLWRAPPER_KEY char *GLOutputCode;
	OPENGLWRAPPER_KEY unsigned int GLOutputCodeAllocated;
	OPENGLWRAPPER_KEY unsigned int GLOutputCodeNumObj;



	OPENGLWRAPPER_KEY char *GLErrStr;
	OPENGLWRAPPER_KEY unsigned int GLErrStrAllocated;
	OPENGLWRAPPER_KEY unsigned int GLNumErrStr;



	#define MAX_PTS_IN_GRP  512

	OPENGLWRAPPER_KEY char *GLCanvasLines;
	OPENGLWRAPPER_KEY unsigned int GLNumCanvasLines;
	OPENGLWRAPPER_KEY unsigned int GLCanvasLinesAllocated;

	OPENGLWRAPPER_KEY char *GLCanvasTexts;
	OPENGLWRAPPER_KEY unsigned int GLCanvasTextsAllocated;
	OPENGLWRAPPER_KEY unsigned int GLCanvasTextsCounter;

	OPENGLWRAPPER_KEY char *GLCanvasSurfaces;
	OPENGLWRAPPER_KEY unsigned int GLCanvasSurfacesAllocated;
	OPENGLWRAPPER_KEY unsigned int GLCanvasSurfacesCounter;


	OPENGLWRAPPER_KEY int GLSearchText;
	OPENGLWRAPPER_KEY int GLSearchTextMode;
	OPENGLWRAPPER_KEY char GLSearchTextValue[256];

	OPENGLWRAPPER_KEY int GLOnClick;
	OPENGLWRAPPER_KEY int GLOnClickCount;
	OPENGLWRAPPER_KEY float GLOnClickX, GLOnClickY, GLOnClickGap;







	#ifndef MAKELPARAM
		#define MAKELPARAM(l, h)   ((LPARAM) MAKELONG(l, h))
		#endif


	#define GL_RENDER			0x01
	#define GL_SELECT			0x02


	#define GLU_POINT                          100010
	#define GLU_LINE                           100011
	#define GLU_FILL                           100012
	#define GLU_SILHOUETTE                     100013


	#define GL_COMPILE			0x01
	#define GL_EXECUTE			0x02

	#define GL_QUADS			0x01


	#define NUM_POINT_COMP		3
	#define NUM_COLOR_COMP		4
	#define NUM_TEX_COMP		2
	#define NUM_NORMAL_COMP		3



	#define PiGreco	3.14159265354

	#define GL_CURRENT_BIT		1
	#define GL_LIGHTING_BIT		2
	#define GL_ENABLE_BIT		3
	#define GL_POLYGON_BIT		4
	#define GL_PIXEL_MODE_BIT	5
	#define GL_TEXTURE_BIT		6

	#define GL_POLYGON_MODE		1
	#define GL_LINE				10

	#ifndef FPOLAR
		#define FPOLAR(__pt, __direction, __distance, __out_pt)	__out_pt[0] = __pt[0] + __distance * cosf(__direction); __out_pt[1] = __pt[1] + __distance * sinf(__direction); __out_pt[2] = __pt[2];
		#endif

	#ifndef FANGLE
		#define FANGLE(__pt1, __pt2)	atan2f(__pt2[1]-__pt1[1], __pt2[0]-__pt1[0])
		#endif


	#define HIGH_DOUBLE_VALUE   +999999999999999999.9
	#define LOW_DOUBLE_VALUE     -999999999999999999.9


	#endif










#define GL_TEXTURE_GEN_S	1
#define GL_TEXTURE_GEN_T	2
#define GL_TEXTURE_GEN_R	4
#define GL_TEXTURE_GEN_Q	8

#define GL_TEXTURE_WRAP_R	0x2804


#define GL_S	1
#define GL_T	2
#define GL_R	3

#define GL_TEXTURE_GEN_MODE	1
#define GL_EYE_PLANE		2
#define GL_EYE_LINEAR		3
#define GL_OBJECT_PLANE		4

#define GL_NORMAL_MAP_EXT	1
#define GL_SPHERE_MAP		2
#define GL_REFLECTION_MAP	4
#define GL_OBJECT_LINEAR	8







#define GL_NORMAL_MAP 0

#ifndef GL_SAMPLE_COVERAGE
	#define GL_SAMPLE_COVERAGE                
    #endif


#define GL_TEXTURE0_ARB	0
#define GL_TEXTURE1_ARB	1


// void glMultiTexCoord2dARB( int type, GLfloat x, GLfloat y1 );






#define GL_ACCUM	0x01
#define GL_RETURN	0x02





#define GL_LIGHT_MODEL_LOCAL_VIEWER	0x0ff
#define GL_LIGHT_MODEL_COLOR_CONTROL	0x0ff
#define GL_SEPARATE_SPECULAR_COLOR		1



#ifdef JNI_DLL

	OPENGLWRAPPER_KEY int gluEnd();
	OPENGLWRAPPER_KEY int gluBegin(int Mode, unsigned int NumItems);
	OPENGLWRAPPER_KEY void gluVertex3f (float x, float y, float z);
	// OPENGLWRAPPER_KEY void glVertex3d (double x, double y, double z);

	#elif defined JNI_SO

	#define GL_LINES	1
	#define GL_LINE_STRIP	2
	#define GL_LINE_LOOP	3
	#define GL_TRIANGLES	4

	#define gluVertex3d glVertex3d

	OPENGLWRAPPER_KEY int gluEnd();
	OPENGLWRAPPER_KEY int gluBegin(int Mode, uint NumItems);
	OPENGLWRAPPER_KEY void gluVertex3f (float x, float y, float z);
	OPENGLWRAPPER_KEY void glVertex3d (double x, double y, double z);
	OPENGLWRAPPER_KEY void gluColor4ub (unsigned char r, unsigned char g, unsigned char b, unsigned char a);
	OPENGLWRAPPER_KEY void gluColor3ub (unsigned char r, unsigned char g, unsigned char b);
	OPENGLWRAPPER_KEY void glColor4ub (unsigned char r, unsigned char g, unsigned char b, unsigned char a);
	OPENGLWRAPPER_KEY void glColor3ub (unsigned char r, unsigned char g, unsigned char b);
	OPENGLWRAPPER_KEY void glTranslatef(float x, float y, float z);
	OPENGLWRAPPER_KEY void glRotatef(float ang, float x, float y, float z);
	OPENGLWRAPPER_KEY void glNormal3f (float nx, float ny, float nz);
	OPENGLWRAPPER_KEY void glEnableClientState(int);
	OPENGLWRAPPER_KEY void glVertexPointer(int, int, int, int);
	OPENGLWRAPPER_KEY void glDisableClientState(int);
	OPENGLWRAPPER_KEY void glDrawArrays(int, int, int);


	#else

	OPENGLWRAPPER_KEY int gluBegin(int Mode, unsigned int NumItems);
	OPENGLWRAPPER_KEY void gluVertex3f (float x, float y, float z);
	OPENGLWRAPPER_KEY void glVertex3d (double x, double y, double z);
	OPENGLWRAPPER_KEY int gluTexCoord2f (float u, float v);
	OPENGLWRAPPER_KEY void gluNormal3f (float nx, float ny, float nz);
	OPENGLWRAPPER_KEY void gluColor4ub (unsigned char r, unsigned char g, unsigned char b, unsigned char a);
	OPENGLWRAPPER_KEY void gluColor3ub (unsigned char r, unsigned char g, unsigned char b);
	OPENGLWRAPPER_KEY void gluColor4f (float r, float g, float b, float a);
	OPENGLWRAPPER_KEY int gluEnd ();

	OPENGLWRAPPER_KEY unsigned int glGenLists( GLsizei range );
	OPENGLWRAPPER_KEY int glNewList ( int base, int mode );
	OPENGLWRAPPER_KEY int gluLoadName (int name);
	OPENGLWRAPPER_KEY int gluListBase ( int base );
	OPENGLWRAPPER_KEY void gluCallList ( int base );
	OPENGLWRAPPER_KEY void gluCallLists( GLsizei n, GLenum type, const GLvoid *lists );
	OPENGLWRAPPER_KEY void glEndList ( void );
	OPENGLWRAPPER_KEY void glDeleteLists( unsigned int list, GLsizei range );
	OPENGLWRAPPER_KEY GLint glRenderMode( GLenum mode );

	#endif


OPENGLWRAPPER_KEY GLenum GLrenderMode;

#define glTranslated	glTranslatef
#define glScaled	glScalef
#define glRotated	glRotatef
#define glBegin(__mode)	gluBegin(__mode,4)
#define glVertex3f	gluVertex3f
#define glTexCoord2f	gluTexCoord2f

#define glEnd	gluEnd
#define glPushName	gluPushName
#define glPopName	gluPopName
#define glCallList	gluCallList
#define glListBase	gluListBase
#define glMultMatrixd glMultMatrixf

// #define glNormal3d	glNormal3f
// #define gluVertex3d	gluVertex3f

#define SET_LINETYPE_CONTINUOUS



#ifdef JNI_DLL
	#define glOrthof glOrtho
	#define glFrustumf glFrustum
	#define glDepthRangef glDepthRange
	#define glNormal3d	glNormal3f
	#define gluVertex3d	glVertex3d
	#elif defined JNI_SO
	#else
	#define glFrustum glFrustumf
	#define gluVertex3d	glVertex3f
	#endif



#ifdef JNI_DLL
	#elif defined JNI_SO
	#else
	OPENGLWRAPPER_KEY void glTexGeni(int type, int attr, int mode);
	OPENGLWRAPPER_KEY void glPolygonMode( int back_front, int mode );
	OPENGLWRAPPER_KEY void glAccum(int Mode, float pan);
	OPENGLWRAPPER_KEY void glLightModeli (int Mode, int Value );
	OPENGLWRAPPER_KEY void glColorMaterial (int Mode, int Value );
	OPENGLWRAPPER_KEY void gluPerspective(float fovy, float aspect, float zNear, float zFar);
	OPENGLWRAPPER_KEY void gluLookAt(GLfloat eyex, GLfloat eyey, GLfloat eyez,GLfloat centerx, GLfloat centery, GLfloat centerz, GLfloat upx, GLfloat upy, GLfloat upz);

	OPENGLWRAPPER_KEY void glPushAttrib (int attr );
	OPENGLWRAPPER_KEY void glPopAttrib ();

	OPENGLWRAPPER_KEY void glLoadName (int name );
	OPENGLWRAPPER_KEY void gluPushName (int name );
	OPENGLWRAPPER_KEY void gluPopName ();
	OPENGLWRAPPER_KEY void glInitNames (void);



	OPENGLWRAPPER_KEY int glAreTexturesResident( GLsizei n, const unsigned int *textures, GLboolean *residences );
	OPENGLWRAPPER_KEY int glIsList( unsigned int list );
	OPENGLWRAPPER_KEY const GLubyte * gluErrorString( GLenum error );
	OPENGLWRAPPER_KEY void glTexGenf( GLenum coord, GLenum pname, GLfloat param );
	OPENGLWRAPPER_KEY void glTexGenfv( GLenum coord, GLenum pname, const GLfloat *params );



	// OPENGLWRAPPER_KEY void gluDisk (int quadObj, float radius, float radius2, int NumArcSubDivision, int mode );
	// MY OWN


	#endif

	OPENGLWRAPPER_KEY void gluPartialDisk(void *qobj, GLfloat cx, GLfloat cy, GLfloat innerRadius, GLfloat outerRadius, GLint slices, GLint loops, GLfloat startAngle, GLfloat sweepAngle, GLfloat r, GLfloat g, GLfloat b);
	OPENGLWRAPPER_KEY void gluDisk2 (long quadObj, float center_x, float center_y, float radius, unsigned char r, unsigned char g, unsigned char b );
	OPENGLWRAPPER_KEY void gluDisk(void *qobj, GLfloat innerRadius, GLfloat outerRadius, GLint slices, GLint loops);
	OPENGLWRAPPER_KEY int gluQuadricDrawStyle (long quadId, int Mode);
	OPENGLWRAPPER_KEY long gluNewQuadric();

	OPENGLWRAPPER_KEY int get_arc_coords_from_bulge ( float pt1_x, float pt1_y, float pt2_x, float pt2_y, float bulge, float *out_cen_x, float *out_cen_y, float *out_rad, float *out_sang, float *out_eang);




#define LINETYPE_CONTINUOUS	0
#define LINETYPE_DASHDOT		1
#define LINETYPE_DASHED		2
#define LINETYPE_DOTTED		3
#define LINETYPE_FINE_DOT		4
#define LINETYPE_FINE_DOT2		5
#define LINETYPE_FINE_DOT3		6



#define SET_LINETYPE_CONTINUOUS \
    glDisable(GL_LINE_STIPPLE);

#define SET_LINETYPE_FINE_DOT3(__factor)	\
    glEnable (GL_LINE_STIPPLE); \
    glLineStipple (__factor, 0x1F1F);

#define SET_LINETYPE_FINE_DOT2(__factor)	\
    glEnable (GL_LINE_STIPPLE); \
    glLineStipple (__factor, 0x1212);

#define SET_LINETYPE_FINE_DOT(__factor)	\
    glEnable (GL_LINE_STIPPLE); \
    glLineStipple (__factor, 0x1111);

#define SET_LINETYPE_DOTTED(__factor)	\
    glEnable (GL_LINE_STIPPLE); \
    glLineStipple (__factor, 0x0101);

#define SET_LINETYPE_DASHED(__factor)	\
    glEnable (GL_LINE_STIPPLE); \
    glLineStipple (__factor, 0x0F0F);

#define SET_LINETYPE_DASHDOT(__factor)	\
    glEnable (GL_LINE_STIPPLE); \
    glLineStipple (__factor, 0xFFFF-BIT10-BIT6);


#define OGL_DRAW_LINE(__pt1_x, __pt1_y, __pt1_z, __pt2_x, __pt2_y, __pt2_z)	\
    gluBegin(GL_LINES,2);\
    gluVertex3d(__pt1_x, __pt1_y, __pt1_z);\
    gluVertex3d(__pt2_x, __pt2_y, __pt2_z);\
    gluEnd();

#define OGL_DRAW_PT(__pt1_x, __pt1_y, __pt1_z, __pt_size)	\
    glBegin(GL_POINTS);\
    glPointSize(__pt_size);\
    glVertex3d(__pt1_x, __pt1_y, __pt1_z);\
    glPointSize(1.0);\
    glEnd();

#define OGL_DRAW_CIRCLE(__center_x, __center_y, __radius, __r, __g, __b)	\
    if (!GLquadObj) GLquadObj = gluNewQuadric();\
    gluQuadricDrawStyle (GLquadObj, GL_LINES);\
    glPushMatrix();\
    glTranslated ((float)__center_x, (float)__center_y, (float)0.0f);\
    gluDisk2 (GLquadObj, (float)__center_x, (float)__center_y, (float)__radius, __r, __g, __b);\
    glPopMatrix();\




#define OGL_DRAW_ELLIPSE(__center_x, __center_y, __radius_x, __radius_y, start_rad_angle, end_rad_angle, __r, __g, __b)\
    if (!GLquadObj) GLquadObj = gluNewQuadric();\
    glPushMatrix();\
    gluQuadricDrawStyle (GLquadObj, GL_LINES);\
    glTranslatef ((float)__center_x, (float)__center_y, (float)0.0f);\
    if (end_rad_angle >= start_rad_angle) {\
    	gluPartialDisk (GLquadObj, __center_x, __center_y, (__radius_x+__radius_y)/2.0f, (__radius_x+__radius_y)/2.0f, GLNumArcSubDivision, 1, 90.0f-end_rad_angle*180.0f/PiGreco, (end_rad_angle-start_rad_angle)*180.0f/PiGreco, __r, __g, __b);\
    	} else {\
    	gluPartialDisk (GLquadObj, __center_x, __center_y, (__radius_x+__radius_y)/2.0f, (__radius_x+__radius_y)/2.0f, GLNumArcSubDivision, 1, 90.0f-end_rad_angle*180.0f/PiGreco, (2.0f*PiGreco-start_rad_angle+end_rad_angle)*180.0f/PiGreco, __r, __g, __b);\
    	}\
    glPopMatrix();



#define OGL_DRAW_ARC(__center_x, __center_y, __radius, start_rad_angle, end_rad_angle, __r, __g, __b)\
    if (!GLquadObj) GLquadObj = gluNewQuadric();\
    glPushMatrix();\
    gluQuadricDrawStyle (GLquadObj, GL_LINES);\
    glTranslatef ((float)__center_x, (float)__center_y, (float)0.0f);\
    if (end_rad_angle >= start_rad_angle) {\
    	gluPartialDisk (GLquadObj, __center_x, __center_y, __radius, __radius, GLNumArcSubDivision, 1, 90.0f-end_rad_angle*180.0f/PiGreco, (end_rad_angle-start_rad_angle)*180.0f/PiGreco, __r, __g, __b);\
    	} else {\
    	gluPartialDisk (GLquadObj, __center_x, __center_y, __radius, __radius, GLNumArcSubDivision, 1, 90.0f-end_rad_angle*180.0f/PiGreco, (2.0f*PiGreco-start_rad_angle+end_rad_angle)*180.0f/PiGreco, __r, __g, __b);\
    	}\
    glPopMatrix();







