#define EXTERN


#ifdef JNI_DLL
	#include <windows.h>
	#include <math.h>
	#include "WindowsToAndroid.h"
	#include "OpenGLWrapper.h"
	#include "spatialFilter/spatialFilter.h"
	#elif defined JNI_SO
	#include "WindowsToLinux.h"
	#include "OpenGLWrapper.h"
	#include "spatialFilter/spatialFilter.h"
	#else
	#include "WindowsToAndroid.h"
	#include "OpenGLWrapper.h"
	#include "spatialFilter/spatialFilter.h"
	#endif

#include <stdio.h>
#include <string.h>





// Errore	1	error LNK2005: _glVertex3d@24 already defined in OpenGLWrapper.obj	opengl32.lib	


// Precisione da rendere regolabile
#define GLCanvasFloatingPointToString   "%0.2f"



#define CACHE_SIZE  64
#define PI  3.1415926535f


int g_fVBOSupported = FALSE;

float GLWidth = 0.0;

long GLquadObj = 0;
int GLNumArcSubDivision = 16;


BOOL GLOutputToXML = FALSE;
BOOL GLOutputToJSON = FALSE;
BOOL GLOutputToCanvas = FALSE;
int GLOutputToCanvasStep = 0;
int GLOutputToXMLOptions = 0+0;



char *GLCanvasLines = NULL;
UINT GLNumCanvasLines = 0;
UINT GLCanvasLinesAllocated = 0;


char *GLOutputCode = NULL;
UINT GLOutputCodeAllocated = 0;
UINT GLOutputCodeNumObj = 0;


int GLdrawOptions = 0;



char *GLErrStr = NULL;
unsigned int GLErrStrAllocated = 0;
unsigned int GLNumErrStr = 0;




int GLSearchText = 0;
char GLSearchTextValue[256] = {0};
int GLSearchTextMode = 0+2;

int GLOnClick = 0;
int GLOnClickCount = 0;
float GLOnClickX = 0.0f, GLOnClickY = 0.0f, GLOnClickGap = 0.25f;





#ifdef JNI_DLL
	#elif defined JNI_SO
	void glPushMatrix() {};
	void glPopMatrix() {};
	void glColor3ub (unsigned char r, unsigned char g, unsigned char b) { gluColor3ub (r, g, b); }
	void glColor4ub (unsigned char r, unsigned char g, unsigned char b, unsigned char a) { gluColor4ub (r, g, b, a); }
    void glTranslatef(float x, float y, float z) {};
    void glRotatef(float ang, float x, float y, float z) {};
	void glVertex3d (double x, double y, double z) {
		float xf = (float)x, yf = (float)y, zf = (float)z;
		gluVertex3f (xf,yf,zf);
	}

	#else
	void glVertex3d (double x, double y, double z) {
		float xf = (float)x, yf = (float)y, zf = (float)z;
		gluVertex3f (xf,yf,zf);
	}
	#endif


typedef struct tag_DisplayList {

		uint Id;
		float *PointsArray;
		unsigned char *ColorArray;
		float *TexArray;
		float *NormalArray;
		uint NumPointsArray, NumColorsArray, NumTexArray, NumPointsArrayAllocated, NumNormalArray, NumNormalArrayAllocated;
		int pointsMode;
		int textId;
		float m[16];
		/*
		 BOOL MatColors;
		float AMBIENTColor[4];
		float DIFFUSEColor[4];
		float SPECULARColor[4];
		float SHININESSValue[1];
		float EMISSIONColor[4];
		 */

	} DISPLAY_LIST;


	DISPLAY_LIST RTDisplayList = {0};


	DISPLAY_LIST *GLDisplayList = NULL;

	int GLCurDisplayListMode = 0;
	uint GLCurDisplayListBase = 0;
	uint GLCurDisplayList1B = 0;
	uint GLNumDisplayList = 0;
	uint GLNumDisplayListAllocated = 0;



	GLenum GLrenderMode = GL_RENDER;




	int gluBegin(int Mode, uint NumItems)
	{

		DISPLAY_LIST *pDisplayList = NULL;

		if (GLCurDisplayList1B) {
			if (GLCurDisplayList1B-1 >= GLNumDisplayListAllocated) return 0;
			pDisplayList = &GLDisplayList[GLCurDisplayList1B-1];
			GLCurDisplayList1B++;
		} else {
			pDisplayList = &RTDisplayList;
		}


		if (Mode == GL_QUADS) {
		} else {
			// return 0;
		}

		pDisplayList->pointsMode = Mode;

		// pDisplayList->MatColors = NO;
	
		pDisplayList->NumPointsArray = 0;
		pDisplayList->NumColorsArray = 0;
		pDisplayList->NumTexArray = 0;
		pDisplayList->NumNormalArray = 0;
		pDisplayList->Id = 0;
	
	
		handle_alloc ( NumItems, pDisplayList );

		return 1;
	}
	
	

	int handle_alloc ( unsigned int NumItems, DISPLAY_LIST *pDisplayList ) {

		if (NumItems >= pDisplayList->NumPointsArrayAllocated) {
			pDisplayList->NumPointsArrayAllocated = NumItems;

		{	uint itemSize = sizeof(float)*NUM_POINT_COMP;
			pDisplayList->PointsArray = realloc(pDisplayList->PointsArray, pDisplayList->NumPointsArrayAllocated * itemSize +1);

			itemSize = sizeof(unsigned char)*NUM_COLOR_COMP;
			pDisplayList->ColorArray = realloc(pDisplayList->ColorArray, pDisplayList->NumPointsArrayAllocated * itemSize +1);

			itemSize = sizeof(float)*NUM_TEX_COMP;
			pDisplayList->TexArray = realloc(pDisplayList->TexArray, pDisplayList->NumPointsArrayAllocated * itemSize +1);
		}
		}
	return 1;
	}
	
	
	
	



	void gluVertex3f (float x, float y, float z)
	{

		DISPLAY_LIST *pDisplayList = NULL;

		if (GLCurDisplayList1B) {
			if (GLCurDisplayList1B-1 >= GLNumDisplayListAllocated) return;
			pDisplayList = &GLDisplayList[GLCurDisplayList1B-1];
		} else {
			pDisplayList = &RTDisplayList;
		}

		if (pDisplayList->NumPointsArray >= pDisplayList->NumPointsArrayAllocated) {
			// NSLog(@"gluVertex3f:Out of memory\n");
		} else {
			pDisplayList->PointsArray[(pDisplayList->NumPointsArray*NUM_POINT_COMP)+0] = (float)x;
			pDisplayList->PointsArray[(pDisplayList->NumPointsArray*NUM_POINT_COMP)+1] = (float)y;
			pDisplayList->PointsArray[(pDisplayList->NumPointsArray*NUM_POINT_COMP)+2] = (float)z;
			pDisplayList->NumPointsArray++;
		}

		return;
	}
	




#ifdef JNI_DLL
	#elif defined JNI_SO
	#else
#endif


int gluTexCoord2f (float u, float v)
{
	DISPLAY_LIST *pDisplayList = NULL;

	if (GLCurDisplayList1B) {
		if (GLCurDisplayList1B-1 >= GLNumDisplayListAllocated) return 0;
		pDisplayList = &GLDisplayList[GLCurDisplayList1B-1];
	} else {
		pDisplayList = &RTDisplayList;
	}

	if (pDisplayList->NumTexArray >= pDisplayList->NumPointsArrayAllocated) {
		// NSLog(@"gluTexCoord2f:Out of memory\n");
	} else {
		pDisplayList->TexArray[(pDisplayList->NumTexArray*NUM_TEX_COMP)+0] = (float)u;
		pDisplayList->TexArray[(pDisplayList->NumTexArray*NUM_TEX_COMP)+1] = (float)v;
		pDisplayList->NumTexArray++;
	}
	return 0;
}







void gluNormal3f (float nx, float ny, float nz)
{
	DISPLAY_LIST *pDisplayList = NULL;

	if (GLCurDisplayList1B) {
		if (GLCurDisplayList1B-1 >= GLNumDisplayListAllocated) return;
		pDisplayList = &GLDisplayList[GLCurDisplayList1B-1];
	} else {
		pDisplayList = &RTDisplayList;
	}

	if (pDisplayList->NumNormalArray >= pDisplayList->NumNormalArrayAllocated) {
		if (pDisplayList->NumPointsArrayAllocated) {
			pDisplayList->NumNormalArrayAllocated = pDisplayList->NumPointsArrayAllocated;
			{	uint itemSize = sizeof(float)*NUM_NORMAL_COMP;
				pDisplayList->NormalArray = realloc(pDisplayList->NormalArray, pDisplayList->NumNormalArrayAllocated * itemSize +1);
			}
		} else {
			glNormal3f (nx, ny, nz);
			return;
		}
	}

	if (pDisplayList->NumNormalArray >= pDisplayList->NumNormalArrayAllocated) {
		// NSLog(@"gluNormal3f:Out of memory\n");
		glNormal3f (nx, ny, nz);
	} else {
		pDisplayList->NormalArray[(pDisplayList->NumNormalArray*NUM_NORMAL_COMP)+0] = (float)nx;
		pDisplayList->NormalArray[(pDisplayList->NumNormalArray*NUM_NORMAL_COMP)+1] = (float)ny;
		pDisplayList->NormalArray[(pDisplayList->NumNormalArray*NUM_NORMAL_COMP)+2] = (float)nz;
		pDisplayList->NumNormalArray++;
	}
}


#ifdef JNI_DLL
	// OpenGL in Windows (Dummy compile)
	#elif defined JNI_SO
	// No openGL in Linux
	void glNormal3f (float nx, float ny, float nz) {}
	#else
	#endif



void gluColor4ub (unsigned char r, unsigned char g, unsigned char b, unsigned char a)
{
	DISPLAY_LIST *pDisplayList = NULL;

	if (GLCurDisplayList1B) {
		if (GLCurDisplayList1B-1 >= GLNumDisplayListAllocated) return;
		pDisplayList = &GLDisplayList[GLCurDisplayList1B-1];
	} else {
		pDisplayList = &RTDisplayList;
	}

	if (pDisplayList->NumColorsArray >= pDisplayList->NumPointsArrayAllocated) {
		handle_alloc ( pDisplayList->NumColorsArray+1, pDisplayList );
	}

	if (pDisplayList->NumColorsArray >= pDisplayList->NumPointsArrayAllocated) {
		// NSLog(@"gluColor4ub:Out of memory\n");
	} else {
		if (pDisplayList->ColorArray) {
			pDisplayList->ColorArray[(pDisplayList->NumColorsArray*NUM_COLOR_COMP)+0] = r;
			pDisplayList->ColorArray[(pDisplayList->NumColorsArray*NUM_COLOR_COMP)+1] = g;
			pDisplayList->ColorArray[(pDisplayList->NumColorsArray*NUM_COLOR_COMP)+2] = b;
			pDisplayList->ColorArray[(pDisplayList->NumColorsArray*NUM_COLOR_COMP)+3] = a;
			pDisplayList->NumColorsArray++;
		}
	}
	return;
}



void gluColor4f (float r, float g, float b, float a)
{
	DISPLAY_LIST *pDisplayList = NULL;

	if (GLCurDisplayList1B) {
		if (GLCurDisplayList1B-1 >= GLNumDisplayListAllocated) return;
		pDisplayList = &GLDisplayList[GLCurDisplayList1B-1];
	} else {
		pDisplayList = &RTDisplayList;
	}


	if (pDisplayList->NumColorsArray >= pDisplayList->NumPointsArrayAllocated) {
		handle_alloc ( pDisplayList->NumColorsArray+1, pDisplayList );
	}

	if (pDisplayList->NumColorsArray >= pDisplayList->NumPointsArrayAllocated) {
		// NSLog(@"gluColor4ub:Out of memory\n");
	} else {
		if (pDisplayList->ColorArray) {
			pDisplayList->ColorArray[(pDisplayList->NumColorsArray*NUM_COLOR_COMP)+0] = (unsigned char)(r*255.0);
			pDisplayList->ColorArray[(pDisplayList->NumColorsArray*NUM_COLOR_COMP)+1] = (unsigned char)(g*255.0);
			pDisplayList->ColorArray[(pDisplayList->NumColorsArray*NUM_COLOR_COMP)+2] = (unsigned char)(b*255.0);
			pDisplayList->ColorArray[(pDisplayList->NumColorsArray*NUM_COLOR_COMP)+3] = (unsigned char)(a*255.0);
			pDisplayList->NumColorsArray++;
		}
	}
	return;
}


void gluColor3ub (unsigned char r, unsigned char g, unsigned char b)
{
	DISPLAY_LIST *pDisplayList = NULL;

	if (GLCurDisplayList1B) {
		if (GLCurDisplayList1B-1 >= GLNumDisplayListAllocated) return;
		pDisplayList = &GLDisplayList[GLCurDisplayList1B-1];
	} else {
		pDisplayList = &RTDisplayList;
	}

	if (pDisplayList->NumColorsArray >= pDisplayList->NumPointsArrayAllocated) {
		handle_alloc ( pDisplayList->NumColorsArray+1, pDisplayList );
	}
	

	if (pDisplayList->NumColorsArray > pDisplayList->NumPointsArrayAllocated) {
		// NSLog(@"gluColor4ub:Out of memory\n");
	} else {
		pDisplayList->ColorArray[(pDisplayList->NumColorsArray*NUM_COLOR_COMP)+0] = r;
		pDisplayList->ColorArray[(pDisplayList->NumColorsArray*NUM_COLOR_COMP)+1] = g;
		pDisplayList->ColorArray[(pDisplayList->NumColorsArray*NUM_COLOR_COMP)+2] = b;
		pDisplayList->ColorArray[(pDisplayList->NumColorsArray*NUM_COLOR_COMP)+3] = 255;
		pDisplayList->NumColorsArray++;
	}
	return;
}


	





void gluBindTexture ( int mode, int textId ) {
	if (GLCurDisplayList1B) {
		DISPLAY_LIST *pDisplayList = NULL;
		if (GLCurDisplayList1B-1 >= GLNumDisplayListAllocated) return;
		pDisplayList = &GLDisplayList[GLCurDisplayList1B-1];
		if (textId || pDisplayList->NumPointsArray==0) {
			pDisplayList->textId = textId;
		}
	} else {
		glBindTexture ( mode, textId );
	}
}


#ifdef JNI_DLL
	// OpenGL in Windows (Dummy compile)
	#elif defined JNI_SO
	// No openGL in Linux
	void glBindTexture ( int mode, int textId ) {}
	#else
	#endif






#define OUTPUT_LINES_NONE				0
#define OUTPUT_LINES_FULL_FOMAT		1
#define OUTPUT_LINES_TO_FLOAT32_ARRAY	2


	int gluEnd ()
	{
		DISPLAY_LIST *pDisplayList = NULL;

		if (GLCurDisplayList1B) {
			if (GLCurDisplayList1B-1 >= GLNumDisplayListAllocated) return 0;
			pDisplayList = &GLDisplayList[GLCurDisplayList1B-1];
			if (GLCurDisplayListMode == GL_COMPILE) {
				return 0;
			} else {
			#ifdef JNI_DLL
				#elif defined JNI_SO
				#else
				glBindTexture(GL_TEXTURE_2D, pDisplayList->textId);
				glMultMatrixf(pDisplayList->m);
				#endif
			}

		} else {
			pDisplayList = &RTDisplayList;
		}
		


		if (pDisplayList->NumPointsArray) {

			#ifdef JNI_DLL
				#elif defined JNI_SO
				#else
				glVertexPointer(NUM_POINT_COMP, GL_FLOAT, 0, pDisplayList->PointsArray);

				if (pDisplayList->NumColorsArray) {
					glEnableClientState(GL_COLOR_ARRAY);
					glColorPointer(NUM_COLOR_COMP, GL_UNSIGNED_BYTE, 0, pDisplayList->ColorArray);
				} else {
					glDisableClientState(GL_COLOR_ARRAY);
				}

				if (pDisplayList->NumTexArray) {
					glEnable(GL_TEXTURE_2D);
					glEnableClientState(GL_TEXTURE_COORD_ARRAY);
					glTexCoordPointer(NUM_TEX_COMP, GL_FLOAT, 0, pDisplayList->TexArray);
				} else {
					glDisableClientState(GL_TEXTURE_COORD_ARRAY);
				}

				if (pDisplayList->NumNormalArray) {
					glEnableClientState(GL_NORMAL_ARRAY);
					glNormalPointer(GL_FLOAT, 0, pDisplayList->NormalArray);
				} else {
					glDisableClientState(GL_NORMAL_ARRAY);
				}

				glDrawArrays(pDisplayList->pointsMode, 0, pDisplayList->NumPointsArray);

			#endif



			if (GLOutputToCanvas) {
				char calling_string_header[256], calling_string_footer[256], str[256], typeof_draw[128];
				char *pstr = NULL;
				int OutputType = OUTPUT_LINES_NONE;
				UINT i = 0;



				switch(pDisplayList->pointsMode) {
					case GL_LINES:
						strcpy (typeof_draw, "gl.LINES");
						break;
					case GL_LINE_STRIP:
						strcpy (typeof_draw, "gl.LINE_STRIP");
						break;
					case GL_LINE_LOOP:
						strcpy (typeof_draw, "gl.LINE_LOOP");
						break;
					case GL_TRIANGLES:
						strcpy (typeof_draw, "gl.TRIANGLES");
						break;
					default:
						typeof_draw[0] = 0;
						break;
					}


				if (GLOutputToCanvasStep == 0) {
					// Prepare
					OutputType = OUTPUT_LINES_FULL_FOMAT;
					} else if (GLOutputToCanvasStep == 1) {
					// Ouput : lascia alla chiamante il compito
					// GLOutputToCanvasStep = OUTPUT_LINES_FULL_FOMAT;
					GLOutputToCanvasStep = OUTPUT_LINES_NONE;
					} else if (GLOutputToCanvasStep == 2) {
					OutputType = OUTPUT_LINES_NONE;
					}

				if (OutputType == OUTPUT_LINES_FULL_FOMAT) {
					float r = 1.0, g = 1.0, b = 1.0, a = 1.0;
					unsigned int CanvasLinesNumPoints = 0;
					
					char **TargetCanvasLines = &GLCanvasLines;
					UINT *TargetCanvasLinesAllocated = &GLCanvasLinesAllocated;
					UINT *TargetNumCanvasLines = &GLNumCanvasLines;

					int ix, iy, bitFieldX = 0, bitFieldY = 0, bitVal = 0;
					int sQuadX = 0, sQuadY = 0, eQuadX = 0, eQuadY = 0;


					if (pDisplayList->NumColorsArray) {
						r = (float)pDisplayList->ColorArray[(0*NUM_COLOR_COMP)+0] / 255.0;
						g = (float)pDisplayList->ColorArray[(0*NUM_COLOR_COMP)+1] / 255.0;
						b = (float)pDisplayList->ColorArray[(0*NUM_COLOR_COMP)+2] / 255.0;
						a = (float)pDisplayList->ColorArray[(0*NUM_COLOR_COMP)+3] / 255.0;
						}

					/////////////////////////////////////////////////////////////
					// Filtro spaziale : memorizzazione in pi� quadranti
					//
					if (GLSpatialFilter.Active) {
						float x = HIGH_DOUBLE_VALUE, y = HIGH_DOUBLE_VALUE, x2 = LOW_DOUBLE_VALUE, y2 = LOW_DOUBLE_VALUE;
						for (i=0; i<pDisplayList->NumPointsArray; i++) {
							x = min(x, pDisplayList->PointsArray[i*NUM_POINT_COMP+0]);
							y = min(y, pDisplayList->PointsArray[i*NUM_POINT_COMP+1]);
							x2 = max(x2, pDisplayList->PointsArray[i*NUM_POINT_COMP+0]);
							y2 = max(y2, pDisplayList->PointsArray[i*NUM_POINT_COMP+1]);
							}

						sQuadX = (x - GLSpatialFilter.X) / GLSpatialFilter.quadWX;
						sQuadY = (y - GLSpatialFilter.Y) / GLSpatialFilter.quadWY;
						eQuadX = (x2 - GLSpatialFilter.X) / GLSpatialFilter.quadWX;
						eQuadY = (y2 - GLSpatialFilter.Y) / GLSpatialFilter.quadWY;

						if (sQuadX < 0 || sQuadX >= GLSpatialFilter.NumSpatialQuadsCols) sQuadX = 0;
						if (sQuadY < 0 || sQuadY >= GLSpatialFilter.NumSpatialQuadsRecs) sQuadY = 0;
						if (eQuadX < 0 || eQuadX >= GLSpatialFilter.NumSpatialQuadsRecs) eQuadX = 0;
						if (eQuadY < 0 || eQuadY >= GLSpatialFilter.NumSpatialQuadsCols) eQuadY = 0;

						// Assegnamento del bitField
						for (ix=sQuadX; ix<eQuadX+1; ix++) {
							bitFieldX += (1 << (ix));
							}
						for (iy=sQuadY; iy<eQuadY+1; iy++) {
							bitFieldY += (1 << (iy));
							}

						} else {
						}



					for (iy=sQuadY; iy<eQuadY+1; iy++) {

						for (ix=sQuadX; ix<eQuadX+1; ix++) {

							if (GLSpatialFilter.Active) {
								TargetCanvasLines = &GLSpatialFilter.SpatialQuads[ix+iy*GLSpatialFilter.NumSpatialQuadsCols].JSData;
								TargetCanvasLinesAllocated = &GLSpatialFilter.SpatialQuads[ix+iy*GLSpatialFilter.NumSpatialQuadsCols].NumJSDataAllocated;
								TargetNumCanvasLines = &GLSpatialFilter.SpatialQuads[ix+iy*GLSpatialFilter.NumSpatialQuadsCols].NumJSData;
								}

							sprintf (calling_string_header, "GLLinesData.push({type:%s,pts:", typeof_draw);
							AddStr (TargetCanvasLines, calling_string_header, TargetCanvasLinesAllocated);
							AddStr (TargetCanvasLines, "new Float32Array([", TargetCanvasLinesAllocated);

							for (i=0; i<pDisplayList->NumPointsArray; i++) {
								if (i) AddStr (TargetCanvasLines, ",", TargetCanvasLinesAllocated);
								sprintf (str, GLCanvasFloatingPointToString, pDisplayList->PointsArray[i*NUM_POINT_COMP+0]);
								if ((pstr=strstr(str,".00")) != NULL) pstr[0] = 0;
								AddStr (TargetCanvasLines, str, TargetCanvasLinesAllocated);
								AddStr (TargetCanvasLines, ",", TargetCanvasLinesAllocated);
								sprintf (str, GLCanvasFloatingPointToString, pDisplayList->PointsArray[i*NUM_POINT_COMP+1]);
								if ((pstr=strstr(str,".00")) != NULL) pstr[0] = 0;
								AddStr (TargetCanvasLines, str, TargetCanvasLinesAllocated);
								CanvasLinesNumPoints++;

								if (!(CanvasLinesNumPoints%MAX_PTS_IN_GRP)) {
									// Verificare che la rimanenza sia >= 2
									// Chiude la sequenza della struttura dati js
									AddStr (TargetCanvasLines, "])", TargetCanvasLinesAllocated);
									sprintf (calling_string_footer, ",npts:%d,r:%.2f,g:%.2f,b:%.2f,a:%.2f,w:%d});\n", CanvasLinesNumPoints, r, g, b, a, (int)GLWidth);
									AddStr (TargetCanvasLines, calling_string_footer, TargetCanvasLinesAllocated);
									// Riapre la sequenza della struttura dati js
									sprintf (calling_string_header, "GLLinesData.push({type:%s,pts:,", typeof_draw);
									AddStr (TargetCanvasLines, calling_string_header, TargetCanvasLinesAllocated);
									AddStr (TargetCanvasLines, "new Float32Array([", TargetCanvasLinesAllocated);
									*TargetNumCanvasLines++;
									CanvasLinesNumPoints = 0;
									}
								}

							// Chiude la sequenza della struttura dati js
							AddStr (TargetCanvasLines, "])", TargetCanvasLinesAllocated);
							sprintf (calling_string_footer, ",npts:%d,r:%.2f,g:%.2f,b:%.2f,a:%.2f,w:%d,bfX:%d,bfY:%d});\n", CanvasLinesNumPoints, r, g, b, a, (int)GLWidth, bitFieldX, bitFieldY);
							AddStr (TargetCanvasLines, calling_string_footer, TargetCanvasLinesAllocated);
							*TargetNumCanvasLines++;

							}
						}

					} else if (OutputType == OUTPUT_LINES_TO_FLOAT32_ARRAY) {
					}

				} else if (GLOutputToXML) {
				} else if (GLOutputToJSON) {
				}



			if (!GLCurDisplayList1B) {
				pDisplayList->pointsMode = 0;
				pDisplayList->NumPointsArray = 0;
				pDisplayList->NumColorsArray = 0;
				pDisplayList->NumTexArray = 0;
			}

			return 1;
		}
		
		return 0;
	}



//////////////////////
// Spessore linee
//
#ifdef JNI_DLL
	void gluLineWidth(float wh) {
		GLWidth = wh;
		}
	#elif defined JNI_SO
	void gluLineWidth(float wh) {
		GLWidth = wh;
		}
	#else
	#define gluLineWidth glLineWidth
	#endif






//////////////////////
// Archi e cerchi
//
float GLirclePts[CACHE_SIZE] = { 0 };
unsigned char GLCircleColors[CACHE_SIZE*4] = { 0 };


void gluDisk2 (long quadObj, float center_x, float center_y, float radius, unsigned char r, unsigned char g, unsigned char b) {
	float *cosx = NULL, *sinx = NULL;
	int NumArcSubDivision = 1, QuadDrawStyle = 0, i;
	char *ptr = (char *)quadObj;
	long addr = 0, index = 0;


// my_printf ("gluDisk2:ptr:%ld", (long)ptr);

memcpy(&addr, &ptr[index], sizeof(float*)); cosx = (float*)addr; index += sizeof(float*);
memcpy(&addr, &ptr[index], sizeof(float*)); sinx = (float*)addr; index += sizeof(float*);
memcpy(&QuadDrawStyle, &ptr[index], sizeof(int)); index += sizeof(int);
memcpy(&NumArcSubDivision, &ptr[index], sizeof(int)); index += sizeof(int);

// my_printf ("gluDisk2:NumArcSubDivision:%d", NumArcSubDivision);
// return;


for (i=0; i<NumArcSubDivision; i++) {
	GLirclePts[i*2+0] = radius * cosx[i];
	GLirclePts[i*2+1] = radius * sinx[i];

	GLCircleColors[i*4+0] = r;
	GLCircleColors[i*4+1] = g;
	GLCircleColors[i*4+2] = b;
	GLCircleColors[i*4+3] = 255;
}

GLirclePts[i*2+0] = radius * cosx[0];
GLirclePts[i*2+1] = radius * sinx[0];

GLCircleColors[i*4+0] = r;
GLCircleColors[i*4+1] = g;
GLCircleColors[i*4+2] = b;
GLCircleColors[i*4+3] = 255;

if (GLOutputToCanvas) {
	char calling_string_header[512], calling_string_footer[512], str[256], typeof_draw[128];
	char *pstr = NULL;

	char **TargetCanvasLines = &GLCanvasLines;
	UINT *TargetCanvasLinesAllocated = &GLCanvasLinesAllocated;
	UINT *TargetNumCanvasLines = &GLNumCanvasLines;

	sprintf (calling_string_header, "GLLinesData.push({type:%s,pts:", "gl.LINE_STRIP");
	AddStr (TargetCanvasLines, calling_string_header, TargetCanvasLinesAllocated);
	AddStr (TargetCanvasLines, "new Float32Array([", TargetCanvasLinesAllocated);

	for(i=0; i<NumArcSubDivision+1; i++) {
		if (i) AddStr (TargetCanvasLines, ",", TargetCanvasLinesAllocated);
		sprintf (str, GLCanvasFloatingPointToString, center_x+GLirclePts[i*2+0]);
		if ((pstr=strstr(str,".00")) != NULL) pstr[0] = 0;
		AddStr (TargetCanvasLines, str, TargetCanvasLinesAllocated);
		AddStr (TargetCanvasLines, ",", TargetCanvasLinesAllocated);
		sprintf (str, GLCanvasFloatingPointToString, center_y+GLirclePts[i*2+1]);
		if ((pstr=strstr(str,".00")) != NULL) pstr[0] = 0;
		AddStr (TargetCanvasLines, str, TargetCanvasLinesAllocated);
		}

	// Chiude la sequenza della struttura dati js
	AddStr (TargetCanvasLines, "])", TargetCanvasLinesAllocated);
	sprintf (calling_string_footer, ",npts:%d,r:%.2f,g:%.2f,b:%.2f,a:%.2f,w:%d});//A\n", NumArcSubDivision+1, GLCircleColors[0]/255.0, GLCircleColors[1]/255.0, GLCircleColors[2]/255.0, GLCircleColors[3]/255.0, (int)GLWidth);
	AddStr (TargetCanvasLines, calling_string_footer, TargetCanvasLinesAllocated);
	*TargetNumCanvasLines++;

	} else {

#ifdef JNI_DLL
	#elif defined JNI_SO
	#else
	// glDisableClientState(GL_COLOR_ARRAY);
	glEnableClientState(GL_COLOR_ARRAY);
	glColorPointer(4, GL_UNSIGNED_BYTE, 0, GLCircleColors);

	// gluEnd();
	glVertexPointer(2, GL_FLOAT, 0, GLirclePts);

	glEnableClientState(GL_VERTEX_ARRAY);
	glDrawArrays(GL_LINE_STRIP, 0, NumArcSubDivision+1);
#endif

	}	
}




long gluNewQuadric() {
	float *cosx = malloc(sizeof(float*)*GLNumArcSubDivision+1);
	float *sinx = malloc(sizeof(float*)*GLNumArcSubDivision+1);
	float alpha = 0.0, dalplha = 2.0 * PiGreco / (float)GLNumArcSubDivision;
	int QuadDrawStyle = 0;
	unsigned int i;

	// my_printf ("gluNewQuadric");

	for (i=0; i<GLNumArcSubDivision; i++) {
		cosx[i] = (float)cos(alpha);
		sinx[i] = (float)sin(alpha);
		alpha += dalplha;
		}

	{	long addr = 0, index = 0;
		char *ptr = (char *)malloc(sizeof(float*)*2+sizeof(int)+sizeof(int)+1);

		// my_printf ("gluNewQuadric:ptr:%ld", (long)ptr);

		addr = cosx;
		memcpy(&ptr[index], &addr, sizeof(float*)); index += sizeof(float*);
		addr = sinx;
		memcpy(&ptr[index], &addr, sizeof(float*)); index += sizeof(float*);
		memcpy(&ptr[index], &QuadDrawStyle, sizeof(int)); index += sizeof(int);
		memcpy(&ptr[index], &GLNumArcSubDivision, sizeof(int)); index += sizeof(int);

		// my_printf ("gluNewQuadric:NumArcSubDivision:%d", GLNumArcSubDivision);

		return (long)ptr;
		}
	}


	int gluQuadricDrawStyle (long quadId, int Mode) {
		return 0;
	}



#ifdef JNI_DLL
	#elif defined JNI_SO
	#else

	unsigned int glGenLists( GLsizei range )
	{
		uint neededDL = GLNumDisplayList+range;
		
		if (neededDL >= GLNumDisplayListAllocated) {
			if (check_general_structure_allocated(0, (void **)&GLDisplayList, sizeof(GLDisplayList[0]), neededDL, &GLNumDisplayListAllocated, 1, NULL, NULL) < 0) {
				return 0;
			}
		}
		
		if (neededDL < GLNumDisplayListAllocated) {
			unsigned int i, retVal = GLNumDisplayList;

			for (i=0; i<range; i++) {
				GLDisplayList[retVal+i].Id = retVal+i+1;
			}

			GLNumDisplayList += range;
			return retVal;
		}
		
		return 0;
	}



	void glPushAttrib (int attr ) {
	}


	void glPopAttrib () {
	}
	
	void gluPushName (int name ) {
	}
	
	void glPopName () {
	}
	
	void glLoadName (int name ) {
	}
	


	void glPolygonMode( int back_front, int mode) {
	}

	void glTexGeni(int type, int attr, int mode) {
	}
	

	int glNewList ( int base, int mode )
	{
		GLCurDisplayListMode = mode;
		if (base<GLNumDisplayList) {
			GLCurDisplayList1B = base+1;
			GLDisplayList[base].NumPointsArray = 0;
			GLDisplayList[base].NumColorsArray = 0;
			GLDisplayList[base].NumTexArray = 0;
			glGetIntegerv(GL_TEXTURE_BINDING_2D, &GLDisplayList[base].textId);
			glPushMatrix();
		}
		return 0;
	}



	void glEndList ( void )
	{
		if (GLCurDisplayList1B-1 < GLNumDisplayListAllocated) {
			/// glGetFloatv(GL_MODELVIEW_MATRIX, GLDisplayList[GLCurDisplayList1B-1].m);
			glPopMatrix();
		}

		GLCurDisplayList1B = 0;
	}
	



	int gluListBase ( int base )
	{
		if (base<GLNumDisplayList) {
			GLCurDisplayListBase = base;
			return 1;
		} else {
			GLCurDisplayListBase = base;
		}

		return 0;
	}
	

	void gluCallList( int dl )
	{
		if (dl<GLNumDisplayList) {
			GLCurDisplayListMode = GL_EXECUTE;
			GLCurDisplayList1B = dl+1;
			gluEnd ();
		}
		GLCurDisplayList1B = 0;
	}
	
	
	
	
	
	void gluCallLists( GLsizei n, GLenum type, const GLvoid *lists )
	{   uint i;

		for (i=0; i<n; i++) {
			if (type == GL_UNSIGNED_BYTE) {
				unsigned char dl = (unsigned char)((unsigned char*)lists)[i];
				GLCurDisplayListMode = GL_EXECUTE;
				GLCurDisplayList1B = GLCurDisplayListBase+dl+1;
				gluEnd ();
			}
		}
	
		GLCurDisplayList1B = 0;
	}
	
	
	
	
	
	
	int gluLoadName (int name )
	{
		return 1;
	}



	void glInitNames (void) {
	}
	

	void glDeleteLists( GLuint list, GLsizei range )
	{
	}
	
	
	GLint glRenderMode( GLenum mode )
	{
		GLrenderMode = mode;
		return 0;
	}

	
	
	
	
	
	
	
	
	void __gluMakeIdentityf(GLfloat m[16])
	{
		m[0+4*0] = 1; m[0+4*1] = 0; m[0+4*2] = 0; m[0+4*3] = 0;
		m[1+4*0] = 0; m[1+4*1] = 1; m[1+4*2] = 0; m[1+4*3] = 0;
		m[2+4*0] = 0; m[2+4*1] = 0; m[2+4*2] = 1; m[2+4*3] = 0;
		m[3+4*0] = 0; m[3+4*1] = 0; m[3+4*2] = 0; m[3+4*3] = 1;
	}
	
	
	
	/*
	void gluPerspective(float fovy, float aspect, float zNear, float zFar)
	{
		float m[4][4];
		float sine, cotangent, deltaZ = 0.0;
		float radians = fovy / 2.0 ;
	
	
		deltaZ = zFar - zNear;
	

		sine = sin(radians);
		if ((deltaZ == 0) || (sine == 0) || (aspect == 0))
		{
			return;
		}
		cotangent = cos(radians) / sine;

		__gluMakeIdentityf(&m[0][0]);
		m[0][0] = cotangent / aspect;
		m[1][1] = cotangent;
		m[2][2] = -(zFar + zNear) / deltaZ;
		m[2][3] = -1.0;
		m[3][2] = -2.0 * zNear * zFar / deltaZ;
		m[3][3] = 0.0;
		glMultMatrixf(&m[0][0]);
	}
	*/
	
	/*
	void gluPerspective(float fov, float aspect, float near, float far)
	{
		float top = tan(fov* 3.141592 / 180.0) * near;
		float bottom = -top;
		float left = aspect * bottom;
		float right = aspect * top;
		glFrustumf(left, right, bottom, top, near, far);
	}
	*/
	
	void gluPerspective(float fovy, float aspect, float zNear, float zFar)
	{
		// glMatrixMode(GL_PROJECTION);
		// glLoadIdentity();

		double xmin, xmax, ymin, ymax;

		ymax = zNear * tan(fovy * 3.14159265354 / 360.0);
		ymin = -ymax;
		xmin = ymin * aspect;
		xmax = ymax * aspect;


		glFrustumf(xmin, xmax, ymin, ymax, zNear, zFar);
		
		
		
		// glMatrixMode(GL_MODELVIEW);
		// glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		
		// glDepthMask(GL_TRUE);
	}


	
	
	void CrossProd(float x1, float y1, float z1, float x2, float y2, float z2, float res[3])
	{
		res[0] = y1*z2 - y2*z1;
		res[1] = x2*z1 - x1*z2;
		res[2] = x1*y2 - x2*y1;
	}
	
	
	
	
	/*
	// my own implementation
	void gluLookAt(float eyeX, float eyeY, float eyeZ, float lookAtX, float lookAtY, float lookAtZ, float upX, float upY, float upZ)
	{
		// i am not using here proper implementation for vectors.
		// if you want, you can replace the arrays with your own
		// vector types
		float f[3];

		// calculating the viewing vector
		f[0] = lookAtX - eyeX;
		f[1] = lookAtY - eyeY;
		f[2] = lookAtZ - eyeZ;

		float fMag, upMag;
		fMag = sqrt(f[0]*f[0] + f[1]*f[1] + f[2]*f[2]);
		upMag = sqrt(upX*upX + upY*upY + upZ*upZ);

		// normalizing the viewing vector
		if( fMag != 0)
		{
			f[0] = f[0]/fMag;
			f[1] = f[1]/fMag;
			f[2] = f[2]/fMag;
		}

		// normalising the up vector. no need for this here if you have your
		// up vector already normalised, which is mostly the case.
		if( upMag != 0 )
		{
			upX = upX/upMag;
			upY = upY/upMag;
			upZ = upZ/upMag;
		}

		float s[3], u[3];

		CrossProd(f[0], f[1], f[2], upX, upY, upZ, s);
		CrossProd(s[0], s[1], s[2], f[0], f[1], f[2], u);

		float M[]=
		{
			s[0], u[0], -f[0], 0,
			s[1], u[1], -f[1], 0,
			s[2], u[2], -f[2], 0,
			0, 0, 0, 1
		};

		glMultMatrixf(M);
		glTranslatef (-eyeX, -eyeY, -eyeZ);
	}
	*/

	
	
	void gluLookAt(GLfloat eyex, GLfloat eyey, GLfloat eyez,

				   GLfloat centerx, GLfloat centery, GLfloat centerz,

				   GLfloat upx, GLfloat upy, GLfloat upz)
	
	{

		GLfloat m[16];

		GLfloat x[3], y[3], z[3];

		GLfloat mag;



		/* Make rotation matrix */



		/* Z vector */

		z[0] = eyex - centerx;

		z[1] = eyey - centery;

		z[2] = eyez - centerz;

		mag = sqrt(z[0] * z[0] + z[1] * z[1] + z[2] * z[2]);

		if (mag) {                   /* mpichler, 19950515 */

			z[0] /= mag;

			z[1] /= mag;

			z[2] /= mag;

		}



		/* Y vector */

		y[0] = upx;

		y[1] = upy;

		y[2] = upz;



		/* X vector = Y cross Z */

		x[0] = y[1] * z[2] - y[2] * z[1];

		x[1] = -y[0] * z[2] + y[2] * z[0];

		x[2] = y[0] * z[1] - y[1] * z[0];



		/* Recompute Y = Z cross X */

		y[0] = z[1] * x[2] - z[2] * x[1];

		y[1] = -z[0] * x[2] + z[2] * x[0];

		y[2] = z[0] * x[1] - z[1] * x[0];





		mag = sqrt(x[0] * x[0] + x[1] * x[1] + x[2] * x[2]);

		if (mag) {

			x[0] /= mag;

			x[1] /= mag;

			x[2] /= mag;

		}

		mag = sqrt(y[0] * y[0] + y[1] * y[1] + y[2] * y[2]);

		if (mag) {

			y[0] /= mag;

			y[1] /= mag;

			y[2] /= mag;

		}



	#define M(row,col)  m[col*4+row]

		M(0, 0) = x[0];

		M(0, 1) = x[1];

		M(0, 2) = x[2];

		M(0, 3) = 0.0;

		M(1, 0) = y[0];

		M(1, 1) = y[1];

		M(1, 2) = y[2];

		M(1, 3) = 0.0;

		M(2, 0) = z[0];

		M(2, 1) = z[1];

		M(2, 2) = z[2];

		M(2, 3) = 0.0;

		M(3, 0) = 0.0;

		M(3, 1) = 0.0;

		M(3, 2) = 0.0;

		M(3, 3) = 1.0;

	#undef M

		glMultMatrixf(m);



		/* Translate Eye to Origin */

		glTranslatef(-eyex, -eyey, -eyez);



	}
	
	
	
	
	
	
	
	/*
	 * Transform a point (column vector) by a 4x4 matrix.  I.e.  out = m * in
	 * Input:  m - the 4x4 matrix
	 *         in - the 4x1 vector
	 * Output:  out - the resulting 4x1 vector.
	 */
	static void transform_point(GLfloat out[4], const GLfloat m[16], const GLfloat in[4])
	{
	#define M(row,col)  m[col*4+row]
		out[0] =
		M(0, 0) * in[0] + M(0, 1) * in[1] + M(0, 2) * in[2] + M(0, 3) * in[3];
		out[1] =
		M(1, 0) * in[0] + M(1, 1) * in[1] + M(1, 2) * in[2] + M(1, 3) * in[3];
		out[2] =
		M(2, 0) * in[0] + M(2, 1) * in[1] + M(2, 2) * in[2] + M(2, 3) * in[3];
		out[3] =
		M(3, 0) * in[0] + M(3, 1) * in[1] + M(3, 2) * in[2] + M(3, 3) * in[3];
	#undef M
	}
	
	
	
	
	/*
	 * Perform a 4x4 matrix multiplication  (product = a x b).
	 * Input:  a, b - matrices to multiply
	 * Output:  product - product of a and b
	 */
	static void matmul(GLfloat * product, const GLfloat * a, const GLfloat * b)
	{
		/* This matmul was contributed by Thomas Malik */
		GLfloat temp[16];
		GLint i;

	#define A(row,col)  a[(col<<2)+row]
	#define B(row,col)  b[(col<<2)+row]
	#define T(row,col)  temp[(col<<2)+row]

		/* i-te Zeile */
		for (i = 0; i < 4; i++) {
			T(i, 0) =
			A(i, 0) * B(0, 0) + A(i, 1) * B(1, 0) + A(i, 2) * B(2, 0) + A(i,
																		  3) *
			B(3, 0);
			T(i, 1) =
			A(i, 0) * B(0, 1) + A(i, 1) * B(1, 1) + A(i, 2) * B(2, 1) + A(i,
																		  3) *
			B(3, 1);
			T(i, 2) =
			A(i, 0) * B(0, 2) + A(i, 1) * B(1, 2) + A(i, 2) * B(2, 2) + A(i,
																		  3) *
			B(3, 2);
			T(i, 3) =
			A(i, 0) * B(0, 3) + A(i, 1) * B(1, 3) + A(i, 2) * B(2, 3) + A(i,
																		  3) *
			B(3, 3);
		}

	#undef A
	#undef B
	#undef T
		memcpy(product, temp, 16 * sizeof(GLfloat));
	}

	
	
	
	/*
	 * Compute inverse of 4x4 transformation matrix.
	 */
	static BOOL invert_matrix(const GLfloat * m, GLfloat * out)
	{
		/* NB. OpenGL Matrices are COLUMN major. */
	#define SWAP_ROWS(a, b) { GLfloat *_tmp = a; (a)=(b); (b)=_tmp; }
	#define MAT(m,r,c) (m)[(c)*4+(r)]

		GLfloat wtmp[4][8];
		GLfloat m0, m1, m2, m3, s;
		GLfloat *r0, *r1, *r2, *r3;

		r0 = wtmp[0], r1 = wtmp[1], r2 = wtmp[2], r3 = wtmp[3];

		r0[0] = MAT(m, 0, 0), r0[1] = MAT(m, 0, 1),
		r0[2] = MAT(m, 0, 2), r0[3] = MAT(m, 0, 3),
		r0[4] = 1.0, r0[5] = r0[6] = r0[7] = 0.0,
		r1[0] = MAT(m, 1, 0), r1[1] = MAT(m, 1, 1),
		r1[2] = MAT(m, 1, 2), r1[3] = MAT(m, 1, 3),
		r1[5] = 1.0, r1[4] = r1[6] = r1[7] = 0.0,
		r2[0] = MAT(m, 2, 0), r2[1] = MAT(m, 2, 1),
		r2[2] = MAT(m, 2, 2), r2[3] = MAT(m, 2, 3),
		r2[6] = 1.0, r2[4] = r2[5] = r2[7] = 0.0,
		r3[0] = MAT(m, 3, 0), r3[1] = MAT(m, 3, 1),
		r3[2] = MAT(m, 3, 2), r3[3] = MAT(m, 3, 3),
		r3[7] = 1.0, r3[4] = r3[5] = r3[6] = 0.0;

		/* choose pivot - or die */
		if (fabs(r3[0]) > fabs(r2[0]))
			SWAP_ROWS(r3, r2);
		if (fabs(r2[0]) > fabs(r1[0]))
			SWAP_ROWS(r2, r1);
		if (fabs(r1[0]) > fabs(r0[0]))
			SWAP_ROWS(r1, r0);
		if (0.0 == r0[0])
			return GL_FALSE;

		/* eliminate first variable     */
		m1 = r1[0] / r0[0];
		m2 = r2[0] / r0[0];
		m3 = r3[0] / r0[0];
		s = r0[1];
		r1[1] -= m1 * s;
		r2[1] -= m2 * s;
		r3[1] -= m3 * s;
		s = r0[2];
		r1[2] -= m1 * s;
		r2[2] -= m2 * s;
		r3[2] -= m3 * s;
		s = r0[3];
		r1[3] -= m1 * s;
		r2[3] -= m2 * s;
		r3[3] -= m3 * s;
		s = r0[4];
		if (s != 0.0) {
			r1[4] -= m1 * s;
			r2[4] -= m2 * s;
			r3[4] -= m3 * s;
		}
		s = r0[5];
		if (s != 0.0) {
			r1[5] -= m1 * s;
			r2[5] -= m2 * s;
			r3[5] -= m3 * s;
		}
		s = r0[6];
		if (s != 0.0) {
			r1[6] -= m1 * s;
			r2[6] -= m2 * s;
			r3[6] -= m3 * s;
		}
		s = r0[7];
		if (s != 0.0) {
			r1[7] -= m1 * s;
			r2[7] -= m2 * s;
			r3[7] -= m3 * s;
		}

		/* choose pivot - or die */
		if (fabs(r3[1]) > fabs(r2[1]))
			SWAP_ROWS(r3, r2);
		if (fabs(r2[1]) > fabs(r1[1]))
			SWAP_ROWS(r2, r1);
		if (0.0 == r1[1])
			return GL_FALSE;

		/* eliminate second variable */
		m2 = r2[1] / r1[1];
		m3 = r3[1] / r1[1];
		r2[2] -= m2 * r1[2];
		r3[2] -= m3 * r1[2];
		r2[3] -= m2 * r1[3];
		r3[3] -= m3 * r1[3];
		s = r1[4];
		if (0.0 != s) {
			r2[4] -= m2 * s;
			r3[4] -= m3 * s;
		}
		s = r1[5];
		if (0.0 != s) {
			r2[5] -= m2 * s;
			r3[5] -= m3 * s;
		}
		s = r1[6];
		if (0.0 != s) {
			r2[6] -= m2 * s;
			r3[6] -= m3 * s;
		}
		s = r1[7];
		if (0.0 != s) {
			r2[7] -= m2 * s;
			r3[7] -= m3 * s;
		}

		/* choose pivot - or die */
		if (fabs(r3[2]) > fabs(r2[2]))
			SWAP_ROWS(r3, r2);
		if (0.0 == r2[2])
			return GL_FALSE;

		/* eliminate third variable */
		m3 = r3[2] / r2[2];
		r3[3] -= m3 * r2[3], r3[4] -= m3 * r2[4],
		r3[5] -= m3 * r2[5], r3[6] -= m3 * r2[6], r3[7] -= m3 * r2[7];

		/* last check */
		if (0.0 == r3[3])
			return GL_FALSE;

		s = 1.0 / r3[3];		/* now back substitute row 3 */
		r3[4] *= s;
		r3[5] *= s;
		r3[6] *= s;
		r3[7] *= s;

		m2 = r2[3];			/* now back substitute row 2 */
		s = 1.0 / r2[2];
		r2[4] = s * (r2[4] - r3[4] * m2), r2[5] = s * (r2[5] - r3[5] * m2),
		r2[6] = s * (r2[6] - r3[6] * m2), r2[7] = s * (r2[7] - r3[7] * m2);
		m1 = r1[3];
		r1[4] -= r3[4] * m1, r1[5] -= r3[5] * m1,
		r1[6] -= r3[6] * m1, r1[7] -= r3[7] * m1;
		m0 = r0[3];
		r0[4] -= r3[4] * m0, r0[5] -= r3[5] * m0,
		r0[6] -= r3[6] * m0, r0[7] -= r3[7] * m0;

		m1 = r1[2];			/* now back substitute row 1 */
		s = 1.0 / r1[1];
		r1[4] = s * (r1[4] - r2[4] * m1), r1[5] = s * (r1[5] - r2[5] * m1),
		r1[6] = s * (r1[6] - r2[6] * m1), r1[7] = s * (r1[7] - r2[7] * m1);
		m0 = r0[2];
		r0[4] -= r2[4] * m0, r0[5] -= r2[5] * m0,
		r0[6] -= r2[6] * m0, r0[7] -= r2[7] * m0;
		
		m0 = r0[1];			/* now back substitute row 0 */
		s = 1.0 / r0[0];
		r0[4] = s * (r0[4] - r1[4] * m0), r0[5] = s * (r0[5] - r1[5] * m0),
		r0[6] = s * (r0[6] - r1[6] * m0), r0[7] = s * (r0[7] - r1[7] * m0);
		
		MAT(out, 0, 0) = r0[4];
		MAT(out, 0, 1) = r0[5], MAT(out, 0, 2) = r0[6];
		MAT(out, 0, 3) = r0[7], MAT(out, 1, 0) = r1[4];
		MAT(out, 1, 1) = r1[5], MAT(out, 1, 2) = r1[6];
		MAT(out, 1, 3) = r1[7], MAT(out, 2, 0) = r2[4];
		MAT(out, 2, 1) = r2[5], MAT(out, 2, 2) = r2[6];
		MAT(out, 2, 3) = r2[7], MAT(out, 3, 0) = r3[4];
		MAT(out, 3, 1) = r3[5], MAT(out, 3, 2) = r3[6];
		MAT(out, 3, 3) = r3[7];
		
		return GL_TRUE;
		
	#undef MAT
	#undef SWAP_ROWS
	}




	
	
	/* projection du point (objx,objy,obz) sur l'ecran (winx,winy,winz) */
	GLint gluProject(GLfloat objx, GLfloat objy, GLfloat objz,
			   const GLfloat model[16], const GLfloat proj[16],
			   const GLint viewport[4],
			   GLfloat * winx, GLfloat * winy, GLfloat * winz)
	{
		/* matrice de transformation */
		GLfloat in[4], out[4];

		/* initilise la matrice et le vecteur a transformer */
		in[0] = objx;
		in[1] = objy;
		in[2] = objz;
		in[3] = 1.0;
		
		transform_point(out, model, in);
		transform_point(in, proj, out);
		
		/* d'ou le resultat normalise entre -1 et 1 */
		if (in[3] == 0.0)
			return GL_FALSE;
		
		in[0] /= in[3];
		in[1] /= in[3];
		in[2] /= in[3];
		
		/* en coordonnees ecran */
		*winx = viewport[0] + (1 + in[0]) * viewport[2] / 2;
		*winy = viewport[1] + (1 + in[1]) * viewport[3] / 2;
		/* entre 0 et 1 suivant z */
		*winz = (1 + in[2]) / 2;
		return GL_TRUE;
	}
	
	
	
	/* transformation du point ecran (winx,winy,winz) en point objet */
	GLint gluUnProject(GLfloat winx, GLfloat winy, GLfloat winz,
				 const GLfloat model[16], const GLfloat proj[16],
				 const GLint viewport[4],
				 GLfloat * objx, GLfloat * objy, GLfloat * objz)
	{
		/* matrice de transformation */
		GLfloat m[16], A[16];
		GLfloat in[4], out[4];

		/* transformation coordonnees normalisees entre -1 et 1 */
		in[0] = (winx - viewport[0]) * 2 / viewport[2] - 1.0;
		in[1] = (winy - viewport[1]) * 2 / viewport[3] - 1.0;
		in[2] = 2 * winz - 1.0;
		in[3] = 1.0;

		/* calcul transformation inverse */
		matmul(A, proj, model);
		invert_matrix(A, m);

		/* d'ou les coordonnees objets */
		transform_point(out, m, in);
		if (out[3] == 0.0)
			return GL_FALSE;
		*objx = out[0] / out[3];
		*objy = out[1] / out[3];
		*objz = out[2] / out[3];
		return GL_TRUE;
	}
	
	
	
	
	void glMultiTexCoord2dARB( int type, GLfloat x, GLfloat y1 ) {
	}
	
	
	void glAccum(int Mode, float pan) {
	}
	
	
	void glLightModeli (int Mode, int Value ) {
	}
	
	void glColorMaterial (int Mode, int Value ) {
	}
	
	int glAreTexturesResident( GLsizei n, const GLuint *textures, GLboolean *residences ) {
	if (residences) *residences = 0;
	return 0;
	}
	
	
	
	int glIsList( GLuint list )
	{
		return TRUE;
	}
	
	
	
	
	const GLubyte * gluErrorString( GLenum error ) {
		return NULL;
	}
	
	
	void glTexGenf( GLenum coord,
				   GLenum pname,
				   GLfloat param ) {
	}
	
	void glTexGenfv( GLenum coord,
					GLenum pname,
					const GLfloat *params ) {
	}
	
	
	
	
	
	
	
	// void gluDisk (int quadObj, float radius, float radius2, int arcSubDivision, int mode ) { }


#endif



#ifdef JNI_DLL
	// OpenGL in Windows (Dummy compile)
	#elif defined JNI_SO
	// No openGL in Linux
	void glEnableClientState(int a) {};
	void glVertexPointer(int a, int b, int c, int d) {};
	void glDisableClientState(int a) {};
	void glDrawArrays(int a, int b, int c) {};
	#else
	#endif




	void gluPartialDisk(void *qobj, GLfloat center_x, GLfloat center_y, GLfloat innerRadius, GLfloat outerRadius, GLint slices, GLint loops, GLfloat startAngle, GLfloat sweepAngle,
		GLfloat r, GLfloat g, GLfloat b ) {

	   GLint i, j;
	   GLfloat sinCache[CACHE_SIZE];
	   GLfloat cosCache[CACHE_SIZE];
	   GLfloat angle;
	   GLfloat sintemp, costemp;
	   GLfloat vertices[(CACHE_SIZE+1)*2][3];
	   GLfloat texcoords[(CACHE_SIZE+1)*2][2];
	   GLfloat deltaRadius;
	   GLfloat radiusLow, radiusHigh;
	   GLfloat texLow = 0.0, texHigh = 0.0;
	   GLfloat angleOffset;
	   GLint slices2;
	   GLint finish;
	   GLboolean texcoord_enabled;
	   GLboolean normal_enabled;
	   GLboolean vertex_enabled;
	   GLboolean color_enabled;



	   // Colore corrente (globale)
	   GLCircleColors[0] = r;
	   GLCircleColors[1] = g;
	   GLCircleColors[2] = b;
	   GLCircleColors[3] = 255;

	   if (slices>=CACHE_SIZE)	   {
		  slices=CACHE_SIZE-1;
	   }
	
	   if (slices<2 || loops<1 || outerRadius<=0.0 || innerRadius<0.0 ||innerRadius > outerRadius) {
		  // gluQuadricError(qobj, GLU_INVALID_VALUE);
		  return;
	   }
	
	   if (sweepAngle<-360.0) {
		  sweepAngle=-360.0;
	   }
	   if (sweepAngle>360.0) {
		  sweepAngle=360.0;
	   }
	
	   if (sweepAngle<0) {
		  startAngle+=sweepAngle;
		  sweepAngle=-sweepAngle;
	   }
	
	   if (sweepAngle==360.0) {
		  slices2=slices;
	   } else {
		  slices2=slices+1;
	   }
	
	   /* Compute length (needed for normal calculations) */
	   deltaRadius=outerRadius-innerRadius;

	   /* Cache is the vertex locations cache */
	   angleOffset=startAngle/180.0f*PI;
	   for (i=0; i<=slices; i++) {
		  angle=angleOffset+((PI*sweepAngle)/180.0f)*i/slices;
		  sinCache[i]=sin(angle);
		  cosCache[i]=cos(angle);
	   }


	   if (sweepAngle==360.0f) {
		  sinCache[slices]=sinCache[0];
		  cosCache[slices]=cosCache[0];
	   }

	
	

	   /* Store status of enabled arrays */
	   texcoord_enabled=GL_FALSE; //glIsEnabled(GL_TEXTURE_COORD_ARRAY);
	   normal_enabled=GL_FALSE; //glIsEnabled(GL_NORMAL_ARRAY);
	   vertex_enabled=GL_FALSE; //glIsEnabled(GL_VERTEX_ARRAY);
	   color_enabled=GL_FALSE; //glIsEnabled(GL_COLOR_ARRAY);

	   /* Enable arrays */
	   glEnableClientState(GL_VERTEX_ARRAY);
	   glVertexPointer(3, GL_FLOAT, 0, vertices);


	   glDisableClientState(GL_NORMAL_ARRAY);
	   glDisableClientState(GL_COLOR_ARRAY);


	   // switch (qobj->drawStyle) {
	   {	int drawStyle = GLU_LINE;
		   	switch (drawStyle) {

			  case GLU_FILL:
			   break;

			  case GLU_POINT:
			   break;



			case GLU_LINE:

				if (innerRadius==outerRadius) {
					for (i=0; i<=slices; i++) {
						vertices[i][0]=innerRadius*sinCache[i];
						vertices[i][1]=innerRadius*cosCache[i];
						vertices[i][2]=0.0f;
						}
					if (GLOutputToCanvas) {
						if (GLOutputToCanvasStep == 2) {
							char calling_string_header[512], calling_string_footer[512], str[256], typeof_draw[128];
							char *pstr = NULL;

							char **TargetCanvasLines = &GLCanvasLines;
							UINT *TargetCanvasLinesAllocated = &GLCanvasLinesAllocated;
							UINT *TargetNumCanvasLines = &GLNumCanvasLines;

							sprintf (calling_string_header, "GLLinesData.push({type:%s,pts:", "gl.LINE_STRIP");
							AddStr (TargetCanvasLines, calling_string_header, TargetCanvasLinesAllocated);
							AddStr (TargetCanvasLines, "new Float32Array([", TargetCanvasLinesAllocated);

							for(i=0; i<slices+1; i++) {
								if (i) AddStr (TargetCanvasLines, ",", TargetCanvasLinesAllocated);
								sprintf (str, GLCanvasFloatingPointToString, center_x+vertices[i][0]);
								if ((pstr=strstr(str,".00")) != NULL) pstr[0] = 0;
								AddStr (TargetCanvasLines, str, TargetCanvasLinesAllocated);
								AddStr (TargetCanvasLines, ",", TargetCanvasLinesAllocated);
								sprintf (str, GLCanvasFloatingPointToString, center_y+vertices[i][1]);
								if ((pstr=strstr(str,".00")) != NULL) pstr[0] = 0;
								AddStr (TargetCanvasLines, str, TargetCanvasLinesAllocated);
								}

							// Chiude la sequenza della struttura dati js
							AddStr (TargetCanvasLines, "])", TargetCanvasLinesAllocated);
							sprintf (calling_string_footer, ",npts:%d,r:%.2f,g:%.2f,b:%.2f,a:%.2f,w:%d});//A\n", slices+1, GLCircleColors[0]/255.0, GLCircleColors[1]/255.0, GLCircleColors[2]/255.0, GLCircleColors[3]/255.0, (int)GLWidth);
							AddStr (TargetCanvasLines, calling_string_footer, TargetCanvasLinesAllocated);
							*TargetNumCanvasLines++;
							}

						} else {
						#ifdef JNI_DLL
							#elif defined JNI_SO
							#else
							glDrawArrays(GL_LINE_STRIP, 0, slices+1);
						#endif
						}
					break;
					}

			   for (j=0; j<=loops; j++) {
				  radiusLow=outerRadius-deltaRadius*((GLfloat)j/loops);
				  for (i=0; i<=slices; i++) {
					vertices[i][0]=radiusLow*sinCache[i];
					vertices[i][1]=radiusLow*cosCache[i];
					vertices[i][2]=0.0f;
					}
				if (GLOutputToCanvas) {
					if (GLOutputToCanvasStep == 2) {
						char calling_string_header[512], calling_string_footer[512], str[256], typeof_draw[128];
						char *pstr = NULL;

						char **TargetCanvasLines = &GLCanvasLines;
						UINT *TargetCanvasLinesAllocated = &GLCanvasLinesAllocated;
						UINT *TargetNumCanvasLines = &GLNumCanvasLines;

						sprintf (calling_string_header, "GLLinesData.push({type:%s,pts:", "gl.LINE_STRIP");
						AddStr (TargetCanvasLines, calling_string_header, TargetCanvasLinesAllocated);
						AddStr (TargetCanvasLines, "new Float32Array([", TargetCanvasLinesAllocated);

						for(i=0; i<slices+1; i++) {
							if (i) AddStr (TargetCanvasLines, ",", TargetCanvasLinesAllocated);
							sprintf (str, GLCanvasFloatingPointToString, center_x+vertices[i][0]);
							if ((pstr=strstr(str,".00")) != NULL) pstr[0] = 0;
							AddStr (TargetCanvasLines, str, TargetCanvasLinesAllocated);
							AddStr (TargetCanvasLines, ",", TargetCanvasLinesAllocated);
							sprintf (str, GLCanvasFloatingPointToString, center_y+vertices[i][1]);
							if ((pstr=strstr(str,".00")) != NULL) pstr[0] = 0;
							AddStr (TargetCanvasLines, str, TargetCanvasLinesAllocated);
							}

						// Chiude la sequenza della struttura dati js
						AddStr (TargetCanvasLines, "])", TargetCanvasLinesAllocated);
						sprintf (calling_string_footer, ",npts:%d,r:%.2f,g:%.2f,b:%.2f,a:%.2f,w:%d});//A\n", slices+1, GLCircleColors[0]/255.0, GLCircleColors[1]/255.0, GLCircleColors[2]/255.0, GLCircleColors[3]/255.0, (int)GLWidth);
						AddStr (TargetCanvasLines, calling_string_footer, TargetCanvasLinesAllocated);
						*TargetNumCanvasLines++;
						}

					} else {
					#ifdef JNI_DLL
						#elif defined JNI_SO
						#else
						glDrawArrays(GL_LINE_STRIP, 0, slices+1);
					#endif
					}
				}




			   for (i=0; i<slices2; i++) {
				sintemp=sinCache[i];
				costemp=cosCache[i];
				for (j=0; j<=loops; j++) {
					radiusLow=outerRadius-deltaRadius*((GLfloat)j/loops);
					vertices[j][0]=radiusLow*sintemp;
					vertices[j][1]=radiusLow*costemp;
					vertices[j][2]=0.0f;
					}
				if (GLOutputToCanvas) {
					if (GLOutputToCanvasStep == 2) {
						char calling_string_header[512], calling_string_footer[512], str[256], typeof_draw[128];
						char *pstr = NULL;

						char **TargetCanvasLines = &GLCanvasLines;
						UINT *TargetCanvasLinesAllocated = &GLCanvasLinesAllocated;
						UINT *TargetNumCanvasLines = &GLNumCanvasLines;

						sprintf (calling_string_header, "GLLinesData.push({type:%s,pts:", "gl.LINE_STRIP");
						AddStr (TargetCanvasLines, calling_string_header, TargetCanvasLinesAllocated);
						AddStr (TargetCanvasLines, "new Float32Array([", TargetCanvasLinesAllocated);

						for(i=0; i<loops+1; i++) {
							if (i) AddStr (TargetCanvasLines, ",", TargetCanvasLinesAllocated);
							sprintf (str, GLCanvasFloatingPointToString, center_x+vertices[i][0]);
							if ((pstr=strstr(str,".00")) != NULL) pstr[0] = 0;
							AddStr (TargetCanvasLines, str, TargetCanvasLinesAllocated);
							AddStr (TargetCanvasLines, ",", TargetCanvasLinesAllocated);
							sprintf (str, GLCanvasFloatingPointToString, center_y+vertices[i][1]);
							if ((pstr=strstr(str,".00")) != NULL) pstr[0] = 0;
							AddStr (TargetCanvasLines, str, TargetCanvasLinesAllocated);
							}

						// Chiude la sequenza della struttura dati js
						AddStr (TargetCanvasLines, "])", TargetCanvasLinesAllocated);
						sprintf (calling_string_footer, ",npts:%d,r:%.2f,g:%.2f,b:%.2f,a:%.2f,w:%d});//A\n", loops+1, GLCircleColors[0]/255.0, GLCircleColors[1]/255.0, GLCircleColors[2]/255.0, GLCircleColors[3]/255.0, (int)GLWidth);
						AddStr (TargetCanvasLines, calling_string_footer, TargetCanvasLinesAllocated);
						*TargetNumCanvasLines++;
						}

					} else {
					#ifdef JNI_DLL
						#elif defined JNI_SO
						#else
						glDrawArrays(GL_LINE_STRIP, 0, loops+1);
					#endif
					}
				}
			   break;




			case GLU_SILHOUETTE:
				break;


			default:
				break;
			}
		}
	}


	/////////////////
	// Wrapper
	//
	void gluDisk(void *qobj, GLfloat innerRadius, GLfloat outerRadius, GLint slices, GLint loops)
	{ gluPartialDisk(qobj, 0.0f, 0.0f, innerRadius, outerRadius, slices, loops, 0.0, 360.0, 0.0, 0.0, 0.0); }











	int get_arc_coords_from_bulge ( float pt1_x, float pt1_y,
									float pt2_x, float pt2_y,
									float fbulge,
									float *out_cen_x, float *out_cen_y, float *out_rad, float *out_sang, float *out_eang) {

	int revDir = 0;
	float bulge, start_ang, end_ang, center[3];


	// Chiusura sequenza lineare

	if (fbulge < 0.0f) {
		bulge = fbulge * -1.0f;
		revDir = 1;
		} else {
		bulge = fbulge;
		}

	{   float p1[3] = { pt1_x, pt1_y, 0.0f}, p2[3] = {pt2_x, pt2_y, 0.0f};
		float dx = fabs(p1[0]-p2[0]);
		float dy = fabs(p1[1]-p2[1]);
		float chord = sqrt (dx*dx + dy*dy);
		float a = atan(bulge) * 2.0f;
		float s = chord / 2.0f * bulge;
		float radius = ((chord/2.0f)*(chord/2.0f) + s*s) / (2.0*s);
		float ang12 = revDir?(FANGLE(p2, p1)):(FANGLE(p1, p2));

		if (revDir) {
			FPOLAR(p2, (((PiGreco/2.0)-a)+ang12), radius, center);
			start_ang = FANGLE(center, p2);
			end_ang = FANGLE(center, p1);
			} else {
			FPOLAR(p1, (((PiGreco/2.0)-a)+ang12), radius, center);
			start_ang = FANGLE(center, p1);
			end_ang = FANGLE(center, p2);
			}

		if (start_ang<0.0f) start_ang += 2.0*PiGreco;
		if (end_ang<0.0f) end_ang += 2.0*PiGreco;



		if (out_rad) *out_rad = radius;
		if (out_sang) *out_sang = start_ang;
		if (out_eang) *out_eang = end_ang;


		if (out_cen_x) *out_cen_x = center[0];
		if (out_cen_y) *out_cen_y = center[1];

		}

		return 1;
	}

