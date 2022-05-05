#define EXTERN

    #define _STLP_HAS_INCLUDE_NEXT  1
    #define _STLP_USE_MALLOC   1
    #define _STLP_USE_NO_IOSTREAMS  1
    #define _STLP_VENDOR_GLOBAL_STD 1
    #define _STLP_NEW_DONT_THROW_BAD_ALLOC  1
    #define _STLP_NO_EXCEPTION_HEADER   1
    #define _STLP_NO_BAD_ALLOC  1
    #define _STLP_DONT_USE_EXCEPTIONS   1



#ifdef JNI_DLL
	#include <windows.h>
	#include <jni-gl.h>
	#include "utility\\OpenGLWrapper.h"
	#include "math.h"
	#elif defined JNI_SO
	// #include "WindowsToAndroid.h"
    #else
    #include <stdlib.h>
    #include <memory.h>
    #include <math.h>
    // using namespace std;
	#include <GLES/gl.h>
	#include "utility/WindowsToAndroid.h"
	#include "utility/OpenGLWrapper.h"
	#endif










#ifdef JNI_DLL
	#elif defined JNI_SO
	#else

	#include "triangulate.h"
	#include "lib-dwg-master/dwg.h"
	#include "lib-dwg-master/logging.h"


	static const float EPSILON=0.0000000001f;

	float Triangulate::Area(const Vector2dVector &contour) {

	  int n = contour.size();

	  float A=0.0f;

	  for(int p=n-1,q=0; q<n; p=q++)
	  {
		A+= contour[p].mX*contour[q].mY - contour[q].mX*contour[p].mY;
	  }
	  return A*0.5f;
	}

	   /*
		 InsideTriangle decides if a point P is Inside of the triangle
		 defined by A, B, C.
	   */
	bool Triangulate::InsideTriangle(float Ax, float Ay,
						  float Bx, float By,
						  float Cx, float Cy,
						  float Px, float Py) {
	  float ax, ay, bx, by, cx, cy, apx, apy, bpx, bpy, cpx, cpy;
	  float cCROSSap, bCROSScp, aCROSSbp;

	  ax = Cx - Bx;  ay = Cy - By;
	  bx = Ax - Cx;  by = Ay - Cy;
	  cx = Bx - Ax;  cy = By - Ay;
	  apx= Px - Ax;  apy= Py - Ay;
	  bpx= Px - Bx;  bpy= Py - By;
	  cpx= Px - Cx;  cpy= Py - Cy;

	  aCROSSbp = ax*bpy - ay*bpx;
	  cCROSSap = cx*apy - cy*apx;
	  bCROSScp = bx*cpy - by*cpx;

	  return ((aCROSSbp >= 0.0f) && (bCROSScp >= 0.0f) && (cCROSSap >= 0.0f));
	};

	bool Triangulate::Snip(const Vector2dVector &contour,int u,int v,int w,int n,int *V) {
	  int p;
	  float Ax, Ay, Bx, By, Cx, Cy, Px, Py;

	  Ax = contour[V[u]].mX;
	  Ay = contour[V[u]].mY;

	  Bx = contour[V[v]].mX;
	  By = contour[V[v]].mY;

	  Cx = contour[V[w]].mX;
	  Cy = contour[V[w]].mY;

	  if ( EPSILON > (((Bx-Ax)*(Cy-Ay)) - ((By-Ay)*(Cx-Ax))) ) return false;

	  for (p=0;p<n;p++)
	  {
		if( (p == u) || (p == v) || (p == w) ) continue;
		Px = contour[V[p]].mX;
		Py = contour[V[p]].mY;
		if (InsideTriangle(Ax,Ay,Bx,By,Cx,Cy,Px,Py)) return false;
	  }

	  return true;
	}

	bool Triangulate::Process(const Vector2dVector &contour,Vector2dVector &result) {
	  /* allocate and initialize list of Vertices in polygon */

	  int n = contour.size();
	  if ( n < 3 ) {
		// my_printf("Triangulate: ERROR < 3");
		return false;
	  }

	  int *V = new int[n];

	  /* we want a counter-clockwise polygon in V */

	  if ( 0.0f < Area(contour) )
		for (int v=0; v<n; v++) V[v] = v;
	  else
		for(int v=0; v<n; v++) V[v] = (n-1)-v;

	  int nv = n;

	  /*  remove nv-2 Vertices, creating 1 triangle every time */
	  int count = 2*nv;   /* error detection */

	  for(int m=0, v=nv-1; nv>2; )
	  {
		/* if we loop, it is probably a non-simple polygon */
		if (0 >= (count--))
		{
		  //** Triangulate: ERROR - probable bad polygon!
		  // my_printf("Triangulate: ERROR - probable bad polygon");
		  return false;
		}

		/* three consecutive vertices in current polygon, <u,v,w> */
		int u = v  ; if (nv <= u) u = 0;     /* previous */
		v = u+1; if (nv <= v) v = 0;     /* new v    */
		int w = v+1; if (nv <= w) w = 0;     /* next     */

		if ( Snip(contour,u,v,w,nv,V) )
		{
		  int a,b,c,s,t;

		  /* true names of the vertices */
		  a = V[u]; b = V[v]; c = V[w];

		  /* output Triangle */
		  result.push_back( contour[a] );
		  result.push_back( contour[b] );
		  result.push_back( contour[c] );

		  m++;

		  /* remove v from remaining polygon */
		  for(s=v,t=v+1;t<nv;s++,t++) V[s] = V[t]; nv--;

		  /* resest error detection counter */
		  count = 2*nv;
		}
	  }



	  delete V;

	  return true;
	}




	extern "C" void *my_realloc (void *ptr, unsigned int size );


	//  BITCODE_BL num_path_segs;
	//  Dwg_Entity_HATCH_PathSeg* segs;
	//  Dwg_Entity_HATCH_PolylinePath* polyline_paths;


	extern "C" int draw_filled_polygon( Dwg_Entity_HATCH *pHatch,
										Dwg_Entity_HATCH_PolylinePath *polyline_paths, Dwg_Entity_HATCH_PathSeg *segs, BITCODE_BL num_path_segs,
										int closed, int has_bulge,
										unsigned char r, unsigned char g, unsigned char b, unsigned char a,
										int bBorder) {

		Vector2dVector vct;
		int il;



		/*
		try {
		} catch (Exception e) {
		}
		*/

        /*
        r = 0;
        g = 0;
        b = 0;
         */

    if (polyline_paths) {

        if (polyline_paths->RTPts) {

            // my_printf("Getting hatch from cache...");

            float *pPoints = (float *)polyline_paths->RTPts;
            long unsigned int nPts = 0;


            memcpy (&nPts, pPoints, sizeof(long unsigned int));
            pPoints += sizeof(long unsigned int) / sizeof(pPoints[0]);

            int tcount = nPts / 3;

            for (int i=0; i<tcount; i++) {

                /*
                pPoints[i*3*2+0] = 1.0f;
                pPoints[i*3*2+1] = 1.0f;

                pPoints[i*3*2+2] = 1.0f;
                pPoints[i*3*2+3] = -1.0f;

                pPoints[i*3*2+4] = 2.0;
                pPoints[i*3*2+5] = 2.0;
                 */

                gluBegin(GL_TRIANGLES,3*2);

                glVertex3f ( pPoints[i*3*2+0], pPoints[i*3*2+1], 0.0 );
                // gluColor4ub(255,255,0,255);
                gluColor4ub(r,g,b,a);

                glVertex3f ( pPoints[i*3*2+2], pPoints[i*3*2+3], 0.0 );
                // gluColor4ub(255,255,0,255);
                gluColor4ub(r,g,b,a);

                glVertex3f ( pPoints[i*3*2+4], pPoints[i*3*2+5], 0.0 );
                // cgluColor4ub(255,255,0,255);
                gluColor4ub(r,g,b,a);

                gluEnd();
             }
        }
   }


    if (segs) {

        if (segs->RTPts) {

            // my_printf("Getting hatch from cache...");

            float *pPoints = (float *)segs->RTPts;
            long unsigned int nPts = 0;

            memcpy (&nPts, pPoints, sizeof(long unsigned int));
            pPoints += sizeof(long unsigned int) / sizeof(pPoints[0]);

            int tcount = nPts / 3;

            for (int i=0; i<tcount; i++) {

                gluBegin(GL_TRIANGLES,3*2);

                glVertex3f ( pPoints[i*3*2+0], pPoints[i*3*2+1], 0.0 );
                // gluColor4ub(255,255,0,255);
                gluColor4ub(r,g,b,a);

                glVertex3f ( pPoints[i*3*2+2], pPoints[i*3*2+3], 0.0 );
                // gluColor4ub(255,255,0,255);
                gluColor4ub(r,g,b,a);

                glVertex3f ( pPoints[i*3*2+4], pPoints[i*3*2+5], 0.0 );
                // cgluColor4ub(255,255,0,255);
                gluColor4ub(r,g,b,a);

                gluEnd();
             }
            return 1;
        }
    }



    if (polyline_paths) {
        if (polyline_paths->RTPts) {
            return 1;
        }
    }
    if (segs) {
        if (segs->RTPts) {
            return 1;
        }
    }


    if (polyline_paths) {
        if (num_path_segs > 2) {
            if (bBorder) gluBegin(GL_LINE_STRIP,(num_path_segs+1));
            for (il=0; il<num_path_segs; il++) {
                vct.push_back(Vector2d(polyline_paths[il].point.x, polyline_paths[il].point.y));
                if (bBorder) {
                    gluVertex3d ( polyline_paths[il].point.x, polyline_paths[il].point.y, 0.0 );
                    gluColor4ub(255,0,0,255);
                }
            }

            if (closed) {
                il = 0;
                // vct.push_back(Vector2d(polyline_paths[il].point.x, polyline_paths[il].point.y));
                if (bBorder) {
                    gluVertex3d ( polyline_paths[il].point.x, polyline_paths[il].point.y, 0.0 );
                    gluColor4ub(0,0,255,255);
                    }
                } else {
                // my_printf("Open polyline_paths - num_path_segs:%d",num_path_segs);
                }
            if (bBorder) gluEnd();

            } else if (num_path_segs == 2) {
            // Like filled circle if bulge = 1.0
            if (has_bulge) {

                if (fabs(polyline_paths[0].bulge) > 0.0f) {
                    float cen[3], rad, sang, eang, x, y;

                    float *cosx = NULL, *sinx = NULL;
                    int NumArcSubDivision = 1, QuadDrawStyle = 0, i;
                    char *ptr = NULL;
                    long addr = 0, index = 0;

                    if (!GLquadObj) GLquadObj = gluNewQuadric();
                    glPushMatrix();
                    gluQuadricDrawStyle (GLquadObj, GL_LINES);
                    glTranslated (cen[0], cen[1], 0.0f);

                    ptr = (char *)GLquadObj;


                    get_arc_coords_from_bulge ((float)polyline_paths[0].point.x, (float)polyline_paths[0].point.y,
                                                (float)polyline_paths[1].point.x, (float)polyline_paths[1].point.y,
                                                (float)polyline_paths[0].bulge,
                                                (float*)&cen[0], (float*)&cen[1], (float*)&rad, (float*)&sang, (float*)&eang);

                    memcpy(&addr, &ptr[index], sizeof(float*)); cosx = (float*)addr; index += sizeof(float*);
                    memcpy(&addr, &ptr[index], sizeof(float*)); sinx = (float*)addr; index += sizeof(float*);
                    memcpy(&QuadDrawStyle, &ptr[index], sizeof(int)); index += sizeof(int);
                    memcpy(&NumArcSubDivision, &ptr[index], sizeof(int)); index += sizeof(int);


                    if (bBorder) gluBegin(GL_LINE_STRIP,(NumArcSubDivision+1));

		    	    // my_printf("[Closed %d][Num Point %d][center %0.3f,%0.3f][radius %0.3f]", closed, NumArcSubDivision, cen[0], cen[1], rad);

                    if (closed) {
                        for (i=0; i<NumArcSubDivision; i++) {
                            x = rad * cosx[i];
                            y = rad * sinx[i];
                            vct.push_back(Vector2d(cen[0]+x, cen[1]+y));
                            if (bBorder) {
                                gluVertex3d ( x, y, 0.0 );
                                gluColor4ub(255,0,0,255);
                                }
		    	            // my_printf("[pt %d][%0.3f,%0.3f]", i+1, x, y);
                            }
                        } else {
                        }

                    if (bBorder) gluEnd();

                    glPopMatrix();

                    } else {
                    if (closed) {
                        }
                    }
                }

                // return 0;

            } else {
                return 0;
            }




    } else if (segs) {
    il = 0;
    // vct.push_back(Vector2d(segs[il].first_endpoint.x, segs[il].first_endpoint.y));
    // my_printf("segs %d - %0.3f,%0.3f",il, segs[il].first_endpoint.x, segs[il].first_endpoint.y);

    if (num_path_segs > 2) {
	    /* LINE
		    first_endpoint;
			second_endpoint; 
			*/

		if (bBorder) {
			gluBegin(GL_LINE_STRIP,(num_path_segs+1));
            gluVertex3d ( segs[il].first_endpoint.x, segs[il].first_endpoint.y, 0.0 );
			gluColor4ub(0,255,0,255);
		    }

		for (il=0; il<num_path_segs; il++) {
            // my_printf("segs %d - %0.3f,%0.3f",il, segs[il].second_endpoint.x, segs[il].second_endpoint.y);
            vct.push_back(Vector2d(segs[il].second_endpoint.x, segs[il].second_endpoint.y));
            if (bBorder) {
                gluVertex3d ( segs[il].second_endpoint.x, segs[il].second_endpoint.y, 0.0 );
                gluColor4ub(0,255,0,255);
                }
            }
	    if (bBorder) gluEnd();
		
		} else if (num_path_segs == 1) {
		if (segs->type_status & 2) {
			/* CIRCULAR ARC
				center;
				radius;
				start_angle;
				end_angle;
				is_ccw;
				*/
	                
			float x, y;

            float *cosx = NULL, *sinx = NULL;
            int NumArcSubDivision = 1, QuadDrawStyle = 0, i;
            char *ptr = NULL;
            long addr = 0, index = 0;

            if (!GLquadObj) GLquadObj = gluNewQuadric();
            glPushMatrix();
            gluQuadricDrawStyle (GLquadObj, GL_LINES);
            // glTranslated (cen[0], cen[1], 0.0f);

            ptr = (char *)GLquadObj;

            memcpy(&addr, &ptr[index], sizeof(float*)); cosx = (float*)addr; index += sizeof(float*);
            memcpy(&addr, &ptr[index], sizeof(float*)); sinx = (float*)addr; index += sizeof(float*);
            memcpy(&QuadDrawStyle, &ptr[index], sizeof(int)); index += sizeof(int);
            memcpy(&NumArcSubDivision, &ptr[index], sizeof(int)); index += sizeof(int);


			if (segs->is_ccw) {
				} else {
				}

			for (i=0; i<NumArcSubDivision; i++) {
				x = segs->radius * cosx[i];
				y = segs->radius * sinx[i];
				vct.push_back(Vector2d(segs->center.x+x, segs->center.y+y));
				}

			} else {
			// Line ???
			}


        } else {
        // Invalid no segs
        return 0;
        }
    }





    if (vct.size() <= 2) return 0;


    Vector2dVector result;

    // Invoke the triangulator to triangulate this polygon.
    Triangulate::Process(vct,result);

    // the results.
    // my_printf("Triangle vertex :%d -> %d",vct.size(), result.size() );
    int tcount = result.size()/3;





    // cache the work
    float *pPoints = (float *)NULL;
    long unsigned int nPts = 0, nPtsAllocated = 0;

    nPts = (tcount>0?tcount:1) * 3;
    nPtsAllocated = sizeof(float) * 2 * nPts + sizeof(long unsigned int);

    // cache punti triangoli (Campo Aggiunto)
    if (polyline_paths) {
        // my_printf("Storing Hatch [%d] polyline_paths, pts:%d, nPtsAllocated:%d", (int)pHatch, nPts/3, nPtsAllocated);
        polyline_paths->RTPts = (void *)my_realloc((void*)polyline_paths->RTPts, nPtsAllocated);
        pPoints = (float *)polyline_paths->RTPts;

        } else if (segs) {
        // my_printf("Storing Hatch [%d] segs, pts:%d, nPtsAllocated:%d", (int)pHatch, nPts/3, nPtsAllocated);
        segs->RTPts = (void *)my_realloc((void*)segs->RTPts, nPtsAllocated);
        pPoints = (float *)segs->RTPts;
        }

    if (pPoints) {
        memcpy (pPoints, &nPts, sizeof(long unsigned int));
        pPoints += sizeof(long unsigned int) / sizeof(pPoints[0]);
    }





    if (tcount<=0) {
        const Vector2d &p1 = vct[0*3+0];
        const Vector2d &p2 = vct[0*3+1];
        const Vector2d &p3 = vct[0*3+2];

        gluBegin(GL_TRIANGLES,3*2);

        glVertex3f ( p1.mX, p1.mY, 0.0 );
        gluColor4ub(r,g,b,a);

        glVertex3f ( p2.mX, p2.mY, 0.0 );
        gluColor4ub(r,g,b,a);

        glVertex3f ( p3.mX, p3.mY, 0.0 );
        gluColor4ub(r,g,b,a);

        gluEnd();

        if (pPoints) {
            pPoints[0*3*2+0] = p1.mX;
            pPoints[0*3*2+1] = p1.mY;
            pPoints[0*3*2+2] = p2.mX;
            pPoints[0*3*2+3] = p2.mY;
            pPoints[0*3*2+4] = p3.mX;
            pPoints[0*3*2+5] = p3.mY;
        }

        // my_printf("Triangle => (%0.0f,%0.0f) (%0.0f,%0.0f) (%0.0f,%0.0f)\n",p1.mX,p1.mY,p2.mX,p2.mY,p3.mX,p3.mY);
        // my_printf("num_path_segs : %d - polyline_paths:%d  -  segs:%d", num_path_segs, (int)polyline_paths, (int)segs);
    } else {
        // gluBegin(GL_TRIANGLE_FAN,tcount*3*2);

        for (int i=0; i<tcount; i++) {
            const Vector2d &p1 = result[i*3+0];
            const Vector2d &p2 = result[i*3+1];
            const Vector2d &p3 = result[i*3+2];
            if (segs) {
                // my_printf("Triangle %d => (%0.0f,%0.0f) (%0.0f,%0.0f) (%0.0f,%0.0f)\n",i+1,p1.mX,p1.mY,p2.mX,p2.mY,p3.mX,p3.mY);
                }

            gluBegin(GL_TRIANGLES,3*2);

            glVertex3f ( p1.mX, p1.mY, 0.0 );
            // gluColor4ub(255,255,0,255);
            gluColor4ub(r,g,b,a);

            glVertex3f ( p2.mX, p2.mY, 0.0 );
            // gluColor4ub(255,255,0,255);
            gluColor4ub(r,g,b,a);

            glVertex3f ( p3.mX, p3.mY, 0.0 );
            // cgluColor4ub(255,255,0,255);
            gluColor4ub(r,g,b,a);

            gluEnd();

            // cache the work
            if (pPoints) {
                pPoints[i*3*2+0] = p1.mX;
                pPoints[i*3*2+1] = p1.mY;
                pPoints[i*3*2+2] = p2.mX;
                pPoints[i*3*2+3] = p2.mY;
                pPoints[i*3*2+4] = p3.mX;
                pPoints[i*3*2+5] = p3.mY;
            }
        }
    }
}



#endif
