//
//  Canvas.c
//
//  Created by cristian andreon on 31/03/11.
//  Copyright 2011 CA. All rights reserved.
//
// OTTIMIZZAZIONE DA FARE : cache dei colori BYLAYER
//



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
	// Windows DLL
	#include <windows.h>
	#include "time.h"
	#include <jni-gl.h>
	#include "utility/WindowsToAndroid.h"
	#include "utility/OpenGLWrapper.h"
	#include "c-sources/types.h"
	#include "c-sources/matrix.h"
	#include "spatialFilter/spatialFilter.h"
	#elif defined JNI_WINDOWS
	// Windows 
	#include <windows.h>
	#include "time.h"
	// #include "utility/WindowsToAndroid.h"
	// #include "utility/OpenGLWrapper.h"
	// #include "c-sources/types.h"
	// #include "c-sources/matrix.h"
	#include "../spatialFilter/spatialFilter.h"
	#elif defined JNI_SO
	// Linux
	// #include "time.h"
	#include "utility/WindowsToLinux.h"
	#include "utility/OpenGLWrapper.h"
	#include "c-sources/types.h"
	#include "c-sources/matrix.h"
	#include "spatialFilter/spatialFilter.h"
	#else
	// Android
	#include "time.h"
	#include <GLES/gl.h>
	#include "utility/WindowsToAndroid.h"
	#include "utility/OpenGLWrapper.h"
	#include "spatialFilter/spatialFilter.h"
	#endif

#include "Canvas.h"




extern float GLCanvasVersion;
extern float GLCameraX, GLCameraY, GLCameraWX, GLCameraWY, GLCameraZ, GLCt;
extern float GLCameraBackupX, GLCameraBackupY, GLCameraBackupWX, GLCameraBackupWY;
extern float GLScreenWX, GLScreenWY;


extern char *GLCanvasLines;
extern char *GLCanvasTexts;
extern char *GLCanvasSurfaces;


//
// Options & BIT1	->	Modalità spatial Filter
// Options & BIT2	->	Modalità Trasparente
//
int create_canvas_string_code ( int width, int height, char **out_string, UINT *out_string_allocated, void *pvSpatialFilter, int Options ) {

	CANVAS_DATA CanvasData = {0};

	CanvasData.Version = GLCanvasVersion;
	CanvasData.CameraX = GLCameraX;
	CanvasData.CameraY = GLCameraY;
	CanvasData.CameraWX = GLCameraWX;
	CanvasData.CameraWY = GLCameraWY;
	CanvasData.CanvasLines = GLCanvasLines;
	CanvasData.CanvasTexts = GLCanvasTexts;
	CanvasData.CanvasSurfaces = GLCanvasSurfaces;
	CanvasData.SpatialFilter = pvSpatialFilter;

	return create_canvas_string_code_ex ( width, height, out_string, out_string_allocated, &CanvasData, Options );
}





//
// Options & BIT1	->	Modalità spatial Filter
// Options & BIT2	->	Modalità Trasparente
//
int create_canvas_string_code_ex ( int width, int height, char **out_string, UINT *out_string_allocated, void *pvCanvasData, int Options ) {
	LP_CANVAS_DATA pCanvasData = (LP_CANVAS_DATA)pvCanvasData;
	if (pvCanvasData) {
		LP_SPATIAL_FILTER pSpatialFilter = (LP_SPATIAL_FILTER)pCanvasData->SpatialFilter;
		char str[512];
		unsigned int i;



		char *init_header_str = ""\
			"var gl = null, pMatrix, mvMatrix, ldown=false, ldrag=false, ldown_x=0.0, ldown_y=0.0, GLCameraSX=0.0, GLCameraSY=0.0;\n"\
			"var GLReverseColor=0; GLPrint=0;\n"\
			"\n"\
			"var vertexShader = null;\n"\
			"var fragmentShader = null;\n"\
			"var shaderProgram = null;\n"\
			"\n"\
			"var textVertexShader = null;\n"\
			"var textFragmentShader = null;\n"\
			"var textProgram = null;\n\n"
			"\n"\
			"var DWGAsDriver = true;"\
			"\n"\
			"var CanvasText = null;\n\n"\
			"var textCtx = null;\n\n";




		char *init_footer_str1 = "\n"\
			"\n"\
			"var fragShaderSource = \"\\\n"\
			"precision highp float;\\\n"\
			"uniform vec4 u_color;\\\n"\
			"void main(void) {\\\n"\
			"    gl_FragColor = u_color;\\\n"\
			"}\\\n"\
			"\";\n"\
			"\n"\
			"var vtxShaderSource = \"\\\n"\
			"attribute vec3 a_position;\\\n"\
			"uniform vec4 u_color;\\\n"\
			"uniform mat4 u_mvMatrix;\\\n"\
			"uniform mat4 u_pMatrix;\\\n"\
			"void main(void) {\\\n"\
			"    gl_Position = u_pMatrix * u_mvMatrix * vec4(a_position, 1.0);\\\n"\
			"}\\\n"\
			"\";\n"\
			"\n"\
			"\n"\
			"\n"\
			"var textFragShaderSource = \"\\\n"\
			"precision mediump float;\\\n"\
			"varying vec2 vt_texcoord;\\\n"\
			"uniform sampler2D ut_texture;\\\n"\
			"uniform vec4 ut_color;\\\n"\
			"void main() {\\\n"\
			"    gl_FragColor = texture2D(ut_texture, vt_texcoord) * ut_color;\\\n"\
			"}\\\n"\
			"\";\n"\
			"\n"\
			"var textVtxShaderSource = \"\\\n"\
			"attribute vec3 at_position;\\\n"\
			"attribute vec2 at_texcoord;\\\n"\
			"uniform mat4 u_pMatrix;\\\n"\
			"varying vec2 vt_texcoord;\\\n"\
			"void main() {\\\n"\
			"    gl_Position = u_pMatrix * vec4(at_position, 1.0);\\\n"\
			"    vt_texcoord = at_texcoord;\\\n"\
			"}\\\n"\
			"\";\n"\
			"\n"\
			"\n"\
			"\n"\
			"\n"\
			"var GLtextTexID = new Array();\n"\
			"\n"\
			"var GLTextureCoords = new Float32Array([\n"\
			"  1.0,  0.0,\n"\
			"  0.0,  0.0,\n"\
			"  1.0,  1.0,\n"\
			"  1.0,  1.0,\n"\
			"  0.0,  0.0,\n"\
			"  0.0,  1.0]);\n"\
			"\n"\
			"\n"\
			"\n"\
			"\n"\
			"/////////////////////////////////\n"\
			"// Dati globali entita\n"\
			"//\n"\
			"\n"\
			"var GLLinesData = new Array ();\n"\
			"var GLTextsData = new Array ();\n"\
			"\n"\
			"function startup_global_data () {\n"\
			"\n";








		char *init_footer_str2 = "\n\n";


			// N.B.: Stringa già pronta da Draw.c (GLCanvasTexts)
			// char *texts_global_data_str = NULL;
			// unsigned int texts_global_data_str_allocated = 0;
			// "    GLTextsData.push({x:100.0, y:-6100.0, t:\"Text Demo\", wh:1200, ht:1200, ang:0.0, r:1.00, g:0.0, b:0.0, a:1.00, tidx:0 });\n"\

		char *init_footer_str3_0 = "\n"\
			"}\n"\
			"\n"\
			"\n"\
			"\n"\
			"\n"\
			"\n"\
			"\n"\
			"function get_shader(type, source) {\n"\
			"    var shader = gl.createShader(type);\n"\
			"    gl.shaderSource(shader, source);\n"\
			"    gl.compileShader(shader);\n"\
			"    check_shader(shader);\n"\
			"    return shader;\n"\
			"}\n"\
			"\n"\
			"function check_shader(shader) {\n"\
			"    var logResult = gl.getShaderInfoLog(shader);\n"\
			"    if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS) ) {\n"\
			"        alert(\"Shader error:\"+logResult);\n"\
			"    }\n"\
			"}\n"\
			"\n"\
			"\n"\
			"\n"\
			"function osm_notity_zoom() {\n"\
			"    try { osm_on_zoom(GLCameraX-GLHomeCameraX,GLCameraY-GLHomeCameraY,GLCameraWX,GLCameraWY); } catch(e) {}; \n"\
			"}\n"\
			"\n"\
			"function zoom ( type, pdwg_x, pdwg_y ) {\n"\
			"    var dx, dy, CameraWX = GLCameraWX, CameraWY = GLCameraWY;\n"\
			"    if (type=='+') {\n"\
			"        GLCameraWX *= 0.8;\n"\
			"        GLCameraWY *= 0.8;\n"\
			"        } else if (type=='-') {\n"\
			"        GLCameraWX /= 0.8;\n"\
			"        GLCameraWY /= 0.8;\n"\
			"        } else if (type=='') {\n"\
			"        GLCameraX = GLHomeCameraX;\n"\
			"        GLCameraY = GLHomeCameraY;\n"\
			"        GLCameraWX = GLHomeCameraWX;\n"\
			"        GLCameraWY = GLHomeCameraWY;\n"\
			"        }\n"\
			"    if (!(typeof pdwg_x === 'undefined') && !(typeof pdwg_y === 'undefined')) {\n"\
			"        dx = GLCameraWX-CameraWX;\n"\
			"        dy = GLCameraWY-CameraWY;\n"\
			"        GLCameraX -= (pdwg_x-GLCameraX)/CameraWX * dx;\n"\
			"        GLCameraY -= (pdwg_y-GLCameraY)/CameraWY * dy;\n"\
			"        }\n"\
			"   \n"\
			"   osm_notity_zoom(); \n"\
			"   \n"\
			"   draw_scene(0);\n"\
			"   }\n"\
			"\n"\
			"\n"\
			"   function reverse_color() {\n"\
			"       GLReverseColor=!GLReverseColor;\n"\
			"       draw_scene(0);\n"\
			"   }\n"\
			"\n"\
			"function canvas_print() {\n"\
			"    PrevGLReverseColor = GLReverseColor;\n"\
			"    GLReverseColor=1;\n"\
			"    GLPrint=1;\n"\
			"    try {\n"\
			"       draw_scene(0);\n"\
			"       } catch(e) {\n"\
			"       }\n"\
			"    GLReverseColor = PrevGLReverseColor;\n"\
			"    window.print();\n"\
			"    GLPrint=0;\n"\
			"    draw_scene(0);\n"\
			"    }\n"\
			"\n"\
			"\n"\
			"    function getPowerOfTwo(value, pow) {\n"\
			"    var pow = pow || 1;\n"\
			"    while(pow<value) {\n"\
			"        pow *= 2;\n"\
			"        }\n"\
			"    return pow;\n"\
			"    }\n"\
			"\n"\
			"\n"\
			"\n"\
			"    function setup_text() {\n"\
			"		CanvasText = document.getElementById(\"textcanvas\");\n"\
			"		if (CanvasText) {\n"\
			"			textCtx = CanvasText.getContext('2d');\n"\
			"			textCtx.clearRect ( 0 , 0 , CanvasText.width, CanvasText.height );\n"\
			"			CanvasText.style.position=\"relative\";\n"\
			"			CanvasText.style.marginTop=\"-\"+GLCanvasWY+\"px\";\n"\
			"			CanvasText.style.zIndex=200;\n"\
			"			CanvasText.style.pointerEvents=\"none\";\n"\
			"			CanvasText.width=GLCanvasWX;\n"\
			"			CanvasText.height=GLCanvasWY;\n"\
			"		} else {\n"\
			"			alert(\"textcanvas not found\");\n"\
			"		}\n"\
			"		var pCanvas = document.getElementById(\"glcanvas\");\n"\
			"		if (pCanvas) {\n"\
			"			pCanvas.width=GLCanvasWX;\n"\
			"			pCanvas.height=GLCanvasWY;\n"\
			"			pCanvas.style.zIndex=300;\n"\
			"		} else {\n"\
			"			alert(\"glcanvas not found\");\n"\
			"		}\n"\
			"    }\n"\
			"\n"\
			"\n"\
			"    function prepare_all_webgl_texts(pCanvasText) {\n"\
			"        if (pCanvasText) {\n"\
			"            // pCanvasText.style.display = \"none\";\n"\
			"            for (i=0; i<GLTextsData.length; i++) {\n"\
			"                textureID = prepare_webgl_text(pCanvasText, GLTextsData[i].t, 32, i);\n"\
			"                GLtextTexID.push(textureID);\n"\
			"                GLTextsData[i].tidx=GLtextTexID.length-1;\n"\
			"            }\n"\
			"        }\n"\
			"    }\n"\
			"\n"\
			"    \n"\
			"    function prepare_webgl_text(pCanvasText, text, size, txtIndex) {\n"\
			"        textCtx = pCanvasText.getContext('2d');\n"\
			"        var zoom_factor = 3.2;\n"\
			"\n"\
			"        size = getPowerOfTwo(size, 2);\n"\
			"        textCtx.font = \"normal \"+size*zoom_factor+\"px Arial\";\n"\
			"\n"\
			"        wh = getPowerOfTwo(textCtx.measureText(text).width*1.1, 2);\n"\
			"        ht = getPowerOfTwo(size*1.1*zoom_factor, 2);\n"\
			"\n"\
			"        if (GLTextsData[txtIndex].wh<=0.0) {\n"\
			"            if (txtIndex>=0) {\n"\
			"                GLTextsData[txtIndex].wh = textCtx.measureText(text).width / zoom_factor;\n"\
			"                }\n"\
			"            }\n"\
			"        \n"\
			"        pCanvasText.width = wh;\n"\
			"        pCanvasText.height = ht;\n"\
			"\n"\
			"        textCtx.font = \"normal \"+size*zoom_factor+\"px Arial\";\n"\
			"        textCtx.textAlign = \"left\";\n"\
			"        textCtx.textBaseline = \"bottom\";\n"\
			"        textCtx.fillStyle = \"#FFFFFF\";\n"\
			"\n"\
			"        //textCtx.clearRect(0, 0, wh, ht);\n"\
			"        textCtx.fillText(text, 0, ht);\n"\
			"\n"\
			"        gl.pixelStorei(gl.UNPACK_PREMULTIPLY_ALPHA_WEBGL, true);\n"\
			"\n"\
			"        // create text texture.\n"\
			"        textTexID = gl.createTexture();\n"\
			"        gl.bindTexture(gl.TEXTURE_2D, textTexID);\n"\
			"\n"\
			"        gl.pixelStorei(gl.UNPACK_PREMULTIPLY_ALPHA_WEBGL, true);\n"\
			"\n"\
			"        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);\n"\
			"        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR_MIPMAP_NEAREST);\n"\
			"        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);\n"\
			"        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);\n"\
			"\n"\
			"        gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);\n"\
			"\n"\
			"        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, pCanvasText);\n"\
			"\n"\
			"        gl.generateMipmap(gl.TEXTURE_2D);\n"\
			"        gl.bindTexture(gl.TEXTURE_2D, null);\n"\
			"\n"\
			"        return textTexID;\n"\
			"    }\n"\
			"\n"\
			"\n"\
			"\n"\
			"function startup_gl() {\n"\
			"if (gl) {\n"\
			"	GLCameraWY = (GLCameraWX/GLCanvasWX*GLCanvasWY);\n"\
			"   mvMatrix =  [ 1, 0, 0, 0   , 0, 1, 0, 0   , 0, 0, 1, 0   , 0, 0, -1, 1];\n"\
			"   pMatrix = mat4.ortho(GLCameraX, GLCameraX+GLCameraWX, GLCameraY, GLCameraY+(GLCameraWX/GLCanvasWX*GLCanvasWY), -1.00, 10.0);\n"\
			"\n"\
			"    // Program for texts\n"\
			"    textVertexShader = get_shader(gl.VERTEX_SHADER, textVtxShaderSource);\n"\
			"    textFragmentShader = get_shader(gl.FRAGMENT_SHADER, textFragShaderSource);\n"\
			"    textProgram = gl.createProgram();\n"\
			"    \n"\
			"    if (!textProgram) {\n"\
			"        alert(\"textProgram failed!\");\n"\
			"        } else {\n"\
			"        gl.attachShader(textProgram, textVertexShader);\n"\
			"        gl.attachShader(textProgram, textFragmentShader);\n"\
			"        gl.linkProgram(textProgram);\n"\
			"        gl.useProgram(textProgram);\n"\
			"\n"\
			"        textProgram.aposAttrib = gl.getAttribLocation(textProgram, \"at_position\");\n"\
			"        textProgram.colorUniform = gl.getUniformLocation(textProgram, \"ut_color\");\n"\
			"        textProgram.textureCoordAttribute = gl.getAttribLocation(textProgram, \"at_texcoord\");\n"\
			"\n"\
			"        textProgram.pMUniform = gl.getUniformLocation(textProgram, \"u_pMatrix\");\n"\
			"        gl.uniformMatrix4fv(textProgram.pMUniform, false, new Float32Array(pMatrix));\n"\
			"        }\n"\
			"    \n"\
			"    // Program for Vdertexs\n"\
			"    vertexShader = get_shader(gl.VERTEX_SHADER, vtxShaderSource);\n"\
			"    fragmentShader = get_shader(gl.FRAGMENT_SHADER, fragShaderSource);\n"\
			"    \n"\
			"    shaderProgram = gl.createProgram();\n"\
			"\n"\
			"    if (!shaderProgram) {\n"\
			"        alert(\"shaderProgram failed!\");\n"\
			"        } else {\n"\
			"        gl.attachShader(shaderProgram, vertexShader);\n"\
			"        gl.attachShader(shaderProgram, fragmentShader);\n"\
			"        gl.linkProgram(shaderProgram);\n"\
			"        gl.useProgram(shaderProgram);\n"\
			"\n"\
			"        shaderProgram.aposAttrib = gl.getAttribLocation(shaderProgram, \"a_position\");\n"\
			"        shaderProgram.colorUniform = gl.getUniformLocation(shaderProgram, \"u_color\");\n"\
			"        shaderProgram.pMUniform = gl.getUniformLocation(shaderProgram, \"u_pMatrix\");\n"\
			"        shaderProgram.mvMUniform = gl.getUniformLocation(shaderProgram, \"u_mvMatrix\");\n"\
			"        \n"\
			"        gl.uniformMatrix4fv(shaderProgram.pMUniform, false, new Float32Array(pMatrix));\n"\
			"        gl.uniformMatrix4fv(shaderProgram.mvMUniform, false, new Float32Array(mvMatrix));\n"\
			"\n"\
			"        gl.enableVertexAttribArray(shaderProgram.aposAttrib);\n"\
			"        gl.enableVertexAttribArray(shaderProgram.colorUniform);\n"\
			"\n"\
			"        }\n"\
			"    }\n"\
			"}\n"\
			"\n"\
			"\n"\
			"\n"\
			"    function canvas_start() {\n"\
			"    var canvas = document.getElementById(\"glcanvas\");\n"\
			"    if (!canvas) {\n"\
			"        alert(\"no canvas\");\n"\
			"        } else {\n"\
			"        if (GLCanvasWX == 0) GLCanvasWX = canvas.width;\n"\
			"        if (GLCanvasWY == 0) GLCanvasWY = canvas.height;\n"\
			"\n"\
			"\n"\
			"       try {\n"\
			"           // Contesto WebGL\n"\
			"           if (GLPrint) gl = canvas.getContext(\"experimental-webgl\",{preserveDrawingBuffer: true}); else gl = canvas.getContext(\"experimental-webgl\");\n"\
			"           if (!gl) { if (GLPrint) gl = canvas.getContext(\"webgl2\",{preserveDrawingBuffer: true}); else gl = canvas.getContext(\"webgl2\"); }\n"\
			"           if (!gl) { if (GLPrint) gl = canvas.getContext(\"webgl\",{preserveDrawingBuffer: true}); else gl = canvas.getContext(\"webgl\"); }\n"\
			"           if (!gl) {\n"\
			"               var url = \"https://get.webgl.org/\";\n"\
			"               if (confirm(\"Unable to initialize WebGL. Your browser may not support it. Go to get.webgl.org ?\")) {\n"\
			"                   location.href=url;\n"\
			"                   }\n"\
			"               }\n"\
			"           } catch(e) {\n"\
			"           alert (e.message);\n"\
			"           }\n"\
			"       }\n"\
			"\n"\
			"	// Istanziamento Dati globali\n"\
			"   startup_global_data();\n"\
			"\n"\
			"    if (canvas) {\n"\
			"        canvas.addEventListener(\"mouseup\", on_mouse_up, false);\n"\
			"        canvas.addEventListener(\"mousedown\", on_mouse_down, false);\n"\
			"        canvas.addEventListener(\"mousemove\", on_mouse_move, false);\n"\
			"        canvas.addEventListener(\'mousewheel\',on_mouse_wheel, false);\n"\
			"        canvas.addEventListener(\'DOMMouseScroll\',on_mouse_wheel, false);\n"\
			"        }\n"\
			"\n"\
			"    var canvasText = document.getElementById(\"textcanvas\");\n"\
			"    if (!canvasText) {\n"\
			"        alert(\"no text canvas\");\n"\
			"        } else {\n"\
			"        try {\n"\
			"            // Contesto 2D\n"\
			"            // prepare_all_webgl_texts(canvasText);\n"\
			"            } catch(e) {\n"\
			"            alert (e.message);\n"\
			"            }\n"\
			"        }\n"\
			"	\n"\
			"        // Setup offset DWG/OSM\n"\
			"        try { \n"\
			"		osm_set_zoom_offset(GLHomeCameraX, GLHomeCameraY);\n"\
			"		} catch(e) {}; \n"\
			"	\n"\
			"        // Disegno scena\n"\
			"        draw_scene(0);\n"\
			"    }\n"\
			"\n"\
			"\n"\
			"\n"\
			"function draw_scene (bTransition) {\n"\
			"\n"\
			"setup_text() \n"\
			"\n"\
			"if (gl) {\n"\
			"	canvas = document.getElementById(\"glcanvas\");\n"\
			"	if (!GLReverseColor) gl.clearColor(0.0, 0.0, 0.0, 1.0); else gl.clearColor(1.0, 1.0, 1.0, 1.0);\n"\
			"	gl.clearDepth(1.0);\n"\
			"	gl.enable(gl.DEPTH_TEST);\n"\
			"	gl.depthFunc(gl.LEQUAL);\n"\
			"	// NOT SUPPORTED : gl.disable(gl.GL_LINE_SMOOTH);\n";


			
		char *init_footer_str3_1a = "   gl.clear(gl.COLOR_BUFFER_BIT|gl.DEPTH_BUFFER_BIT);\n";
		char *init_footer_str3_1b = "   gl.clear(/*gl.COLOR_BUFFER_BIT|*/gl.DEPTH_BUFFER_BIT);\n";


		char *init_footer_str3_2 = "   gl.viewport(0, 0, canvas.width, canvas.height);\n"\
			"\n"\
			"	GLCameraWY = (GLCameraWX/GLCanvasWX*GLCanvasWY);\n"\
			"\n"\
			"	mvMatrix =  [ 1, 0, 0, 0   , 0, 1, 0, 0   , 0, 0, 1, 0   , 0, 0, -1, 1];\n"\
			"\n"\
			"	pMatrix = mat4.ortho(GLCameraX, GLCameraX+GLCameraWX, GLCameraY, GLCameraY+(GLCameraWX/GLCanvasWX*GLCanvasWY), -1.00, 10.0);\n"\
			"\n"\
			"	// Setup programma WebGL\n"\
			"	startup_gl();\n"\
			"\n"\
			"	// Controllo caricamento quadranti\n"\
			"   check_spatial_entities_loaded(bTransition);\n"\
			"\n"\
			"	// Disegno quadranti\n"\
			"   draw_spatial_quads(bTransition);\n"\
			"\n"\
			"	// Disegno entità\n"\
			"   draw_canvas_entities(canvas, bTransition);\n"\
			"\n"\
			"   if (GLPrint) {\n"\
			"	canvas = document.getElementById(\"glcanvas\");\n"\
			"       canvasText = document.getElementById(\"textcanvas\");\n"\
			"       if (canvas) { \n"\
			"           if (canvasText) {\n"\
			"			    textCtx = canvasText.getContext('2d'); \n"\
			"			    textCtx.globalCompositeOperation = \"destination-over\"; \n"\
			"			    textCtx.drawImage(canvas, 0, 0); \n"\
			"			    canvasTextImg = canvasText.toDataURL(\"image/jpg\", 0.8); }; \n"\
			"               div_obj=document.getElementById(\"test_container_div\"); \n"\
			"               if (canvasTextImg) { \n"\
			"                   if (div_obj) { \n"\
			"                       img_html = '<image src=\"'+canvasTextImg+'\" />'; \n"\
			"                       div_obj.innerHTML = img_html;\n"\
			"                       } else {\n"\
			"                       div_obj.innerHTML=\"Failed to get image from canvas\";\n"\
			"                       }\n"\
			"                   }\n"\
			"	            }\n"\
			"	        }\n"\
			"	    }\n"\
			"	}\n"\
			"\n"\
			"\n"\
			"\n"\
			"\n"\
			"///////////////////////////////////////////////\n"\
			"// Controllo caricamento quadranti...	\n"\
			"// \n"\
			"function check_spatial_entities_loaded(bTransition) {\n"\
			"        NumQuads = GLNumQuadsX*GLNumQuadsY;\n"\
			"        DebugString = \"\";\n"\
			"        for(i=0; i<NumQuads; i++) {\n"\
			"		if (GLCameraX<=GLQuads[i].X2 && GLCameraX+GLCameraWX>=GLQuads[i].X || GLCameraX<=GLQuads[i].X2 && GLCameraX>=GLQuads[i].X || GLCameraX+GLCameraWX<=GLQuads[i].X2 && GLCameraX+GLCameraWX>=GLQuads[i].X) {\n"\
			"			if (GLCameraY<=GLQuads[i].Y2 && GLCameraY+GLCameraWY>=GLQuads[i].Y || GLCameraY<=GLQuads[i].Y2 && GLCameraY>=GLQuads[i].Y || GLCameraY+GLCameraWY<=GLQuads[i].Y2 && GLCameraY+GLCameraWY>=GLQuads[i].Y) {\n"\
			"				if (!GLQuads[i].Loaded) {\n"\
			"					GLQuads[i].Loaded = 1;\n"\
			"					DebugString+=\"[Quad #\"+(i+1)+\" Loaded]\";\n"\
			"					}\n"\
			"				}\n"\
			"			}\n"\
			"		}\n"\
			"	DebugString += \"Ready...\";\n"\
			"	var debugDiv = document.getElementById(\"debugDiv\");\n"\
			"	if (debugDiv) debugDiv.innerHTML = DebugString;\n"\
			"	}\n"\
			"\n"\
			"\n"\
			"///////////////////////////////////////////////\n"\
			"// Disegno dei quadranti...	\n"\
			"// \n"\
			"function draw_spatial_quads(bTransition) {\n"\
			"        NumQuads = GLNumQuadsX*GLNumQuadsY;\n"\
			"        for(i=0; i<NumQuads; i++) {\n"\
			"            if(!GLQuads[i].verticesBuffer) {\r\n"\
			"                GLQuads[i].verticesBuffer = gl.createBuffer();\r\n"\
			"                }\r\n"\
			"       	canvas_draw_array(GLQuads[i].verticesBuffer, gl.LINE_LOOP, new Float32Array([GLQuads[i].X,GLQuads[i].Y,GLQuads[i].X2,GLQuads[i].Y,GLQuads[i].X2,GLQuads[i].Y2,GLQuads[i].X,GLQuads[i].Y2,GLQuads[i].X,GLQuads[i].Y]), 4, 1.0, 0.0, 0.0, 0.7, 2.0);\r\n"\
			"		}\n"\
			"	}\n"\
			"\n"\
			"\n"\
			"function createOrtho2D(left,right,bottom,top) {\n"\
			"    var near = -1, far = 1, rl = right-left, tb = top-bottom, fn = far-near;\n"\
			"    return [        2/rl,                0,              0,  0,\n"\
			"                       0,             2/tb,              0,  0,\n"\
			"                       0,                0,          -2/fn,  0,\n"\
			"        -(right+left)/rl, -(top+bottom)/tb, -(far+near)/fn,  1];\n"\
			"}\n"\
			"\n"\
			"\n"\
			"\n"\
			"\n"\
			"    /////////////////////////////////\n"\
			"    // Disegno entita linea...	\n"\
			"    //					\n"\
			"    function canvas_draw_array ( verticesBuffer, mode, vertices, n_vertices, r, g, b, a, w ) {\n"\
			"\n"\
			"       if (GLReverseColor) if (r>0.9&&g>0.9&&b>0.9) {r=1.0-r; b=1.0-b; g=1.0-g;}\n"\
			"\n"\
			"       gl.lineWidth(w);\n"\
			"       gl.bindBuffer(gl.ARRAY_BUFFER, verticesBuffer);\n"\
			"       gl.bufferData(gl.ARRAY_BUFFER, vertices, gl.STATIC_DRAW);\n"\
			"       gl.uniform4f(shaderProgram.colorUniform, r, g, b, a);\n"\
			"\n"\
			"       gl.vertexAttribPointer(shaderProgram.aposAttrib, 2, gl.FLOAT, false, 0, 0);\n"\
			"       gl.drawArrays(mode, 0, n_vertices);\n"\
			"    }\n"\
			"\r\n"\
			"\r\n"\
			"\r\n"\
			"    /////////////////////////////////\r\n"\
			"    // Disegno entita testo...	\r\n"\
			"    //\r\n"\
			"	function canvas_draw_text ( x, y, text, wh, ht, angle, r, g, b, a, texIndex ) {\n"\
			"		var dwg_x = (x-GLCameraX)*GLCanvasWX/GLCameraWX;\n"\
			"		var dwg_y = GLCanvasWY-(y-GLCameraY)*GLCanvasWY/GLCameraWY;\n"\
			"		var dwg_ht = parseFloat(ht) / GLCameraWY * GLCanvasWY;\n"\
			"		var dwg_wh = parseFloat(wh) / GLCameraWX * GLCanvasWX;\n"\
			"		var dwg_wh2 = ht*text.length*0.8 / GLCameraWX * GLCanvasWX;\n"\
			"		\n"\
			"       if (GLReverseColor) if (r>0.9&&g>0.9&&b>0.9) {r=1.0-r; b=1.0-b; g=1.0-g;}\n"\
			"		\n"\
			"		r = parseInt(r*255.0);\n"\
			"		g = parseInt(g*255.0);\n"\
			"		b = parseInt(b*255.0);\n"\
			"		\n"\
			"		var color = \"rgba(\"+r+\",\"+g+\",\"+b+\",\"+a+\")\";\n"\
			"		\n"\
			"		if (dwg_ht >= 4.0) {\n"\
			"			textCtx.font = \"normal \"+dwg_ht+\"px Arial\";\n"\
			"			textCtx.textAlign = \"left\";\n"\
			"			textCtx.textBaseline = \"bottom\";\n"\
			"			if (angle != 0.0) { textCtx.save(); textCtx.rotate(angle); }\n"\
			"			textCtx.fillStyle = color;\n"\
			"			\n"\
			"			textCtx.fillText(text, dwg_x, dwg_y+dwg_ht);\n"\
			"			if (angle != 0.0) { textCtx.restore(); textCtx.rotate(0.0); }\n"\
			"			if (texIndex>=0) {\n"\
			"				if (GLTextsData[texIndex].wh<=0.0) {\n"\
			"					GLTextsData[texIndex].wh = textCtx.measureText(text).width*1.1 * GLCameraWX / GLCanvasWX;\n"\
			"				}\n"\
			"			}\n"\
			"		} else {\n"\
			"		textCtx.beginPath(); textCtx.moveTo(dwg_x,dwg_y); textCtx.lineTo(dwg_x+dwg_wh2,dwg_y); textCtx.strokeStyle = color; textCtx.stroke();\n"\
			"		}\n"\
			"	}\n"\
			"   \n"\
			"   function canvas_draw_webgl_text(x, y, text, wh, ht, angle, r, g, b, a, texIndex) {\r\n"\
			"       program = textProgram;\r\n"\
			"       gl.useProgram(program);\r\n"\
			"       \r\n"\
			"       gl.enableVertexAttribArray(program.aposAttrib);\r\n"\
			"       gl.enableVertexAttribArray(program.colorUniform);\r\n"\
			"       gl.enableVertexAttribArray(program.textureCoordAttribute);\r\n"\
			"       \r\n"\
			"       width = ht * text.length;\r\n"\
			"       height = ht;\r\n"\
			"       y -= ht / 2.0;\r\n"\
			"       \r\n"\
			"       var vertices = new Float32Array([\r\n"\
			"            x+width, y,\r\n"\
			"            x, y,   \r\n"\
			"            x+width, y+height, \r\n"\
			"            \r\n"\
			"            x+width, y+height,\r\n"\
			"            x, y,\r\n"\
			"            x, y+height\r\n"\
			"            ]);\r\n"\
			"       \r\n"\
			"       gl.enable(gl.BLEND);\r\n"\
			"       gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA);\r\n"\
			"       // gl.blendFunc(gl.ONE_MINUS_SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);\r\n"\
			"       // gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);\r\n"\
			"       gl.depthMask(false);\r\n"\
			"        \r\n"\
			"       vbuffer = gl.createBuffer();\r\n"\
			"       gl.bindBuffer(gl.ARRAY_BUFFER, vbuffer);\r\n"\
			"       gl.vertexAttribPointer(program.aposAttrib, 2, gl.FLOAT, false, 0, 0);\r\n"\
			"       gl.bufferData(gl.ARRAY_BUFFER, vertices, gl.STATIC_DRAW);\r\n"\
			"       \r\n"\
			"       itemSize = 2;\r\n"\
			"       numItems = vertices.length / itemSize;\r\n"\
			"\r\n"\
			"       if (GLReverseColor) if (r==0.0&&g==0.0&&b==0.0) {r=1.0; b=1.0; g=1.0;}\n"\
			"\r\n"\
			"       gl.uniform4f(program.colorUniform, r, g, b, a);\r\n"\
			"\r\n"\
			"       tbuffer = gl.createBuffer();\r\n"\
			"       gl.bindBuffer(gl.ARRAY_BUFFER, tbuffer);\r\n"\
			"       gl.bufferData(gl.ARRAY_BUFFER, GLTextureCoords, gl.STATIC_DRAW);\r\n"\
			"       gl.vertexAttribPointer(program.textureCoordAttribute, 2, gl.FLOAT, false, 0, 0);\r\n"\
			"\r\n"\
			"       //gl.activeTexture(gl.TEXTURE0);\r\n"\
			"       var textureID = GLtextTexID[texIndex];\r\n"\
			"       gl.bindTexture(gl.TEXTURE_2D, textureID);\r\n"\
			"\r\n"\
			"       gl.drawArrays(gl.TRIANGLES, 0, numItems);\r\n"\
			"    }\r\n"\
			"\r\n"\
			"\r\n"\
			"\r\n"\
			"///////////////////////////////////////\n"\
			"// Disegno entita nel DWG\n"\
			"//\n"\
			"function draw_canvas_entities(canvas,bTransition) {\r\n"\
			"	for (i=0; i<GLLinesData.length; i++) {\r\n"\
			"            if(!GLLinesData[i].verticesBuffer) {\r\n"\
			"                GLLinesData[i].verticesBuffer = gl.createBuffer();\r\n"\
			"                }\r\n"\
			"		canvas_draw_array(GLLinesData[i].verticesBuffer, GLLinesData[i].type, GLLinesData[i].pts,GLLinesData[i].npts,GLLinesData[i].r,GLLinesData[i].g,GLLinesData[i].b,GLLinesData[i].a,GLLinesData[i].w);\r\n"\
			"		}\r\n"\
			"	if (!bTransition) {\r\n"\
			"		// Chiamata asincrona\r\n"\
			"		setTimeout('draw_canvas_texts()',1);\r\n"\
			"		}\r\n"\
			"	}\r\n"\
			"\r\n"\
			"\r\n"\
			"    // New : Chiamata asincrona \r\n"\
			"function draw_canvas_texts() {\r\n"\
			"    for (i=0; i<GLTextsData.length; i++) {\r\n"\
			"        canvas_draw_text(GLTextsData[i].x, GLTextsData[i].y, GLTextsData[i].t, GLTextsData[i].wh, GLTextsData[i].ht, GLTextsData[i].ang, GLTextsData[i].r, GLTextsData[i].g, GLTextsData[i].b, GLTextsData[i].a, GLTextsData[i].tidx);\r\n"\
			"        }\r\n"\
			"    }\r\n"\
			"\r\n"\
			"\r\n"\
			"\r\n"\
			"var x, y, dwg_x = 0.0, dwg_y = 0.0, delta_x = 0.0, delta_y = 0.0;;\n"\
			"\n"\
			"function get_mouse_coords(event) {\n"\
			"    x = event.pageX;\n"\
			"    y = event.pageY;\n"\
			"    \n"\
			"    var canvas = document.getElementById(\"glcanvas\");\n"\
			"    var rect = canvas.getBoundingClientRect();\n"\
			"\n"\
			"    x -= rect.left;\n"\
			"    y -= rect.top;\n"\
			"    \n"\
			"    dwg_x = x / GLCanvasWX * GLCameraWX + GLCameraX;\n"\
			"    dwg_y = (rect.bottom-rect.top-y) / GLCanvasWY * GLCameraWY + GLCameraY;\n"\
			"}\n"\
			"\n"\
			"\n"\
			"\n"\
			"function on_mouse_up(event) {\n"\
			"if (DWGAsDriver) {\n"\
			"	get_mouse_coords(event);\n"\
			"	if (!ldrag) {\n"\
			"		search_on_tags(dwg_x, dwg_y);\n"\
			"		} else {\n"\
			"		}\n"\
			"	draw_scene(0);\n"\
			"	ldown = false;\n"\
			"	ldrag = false;\n"\
			"	}\n"\
			"   }\n"\
			"\n"\
			"function on_mouse_down(event) {\n"\
			"if (DWGAsDriver) {\n"\
			"	get_mouse_coords(event);\n"\
			"	ldown = true;\n"\
			"	ldown_x = dwg_x;\n"\
			"	ldown_y = dwg_y;\n"\
			"	}\n"\
			"	post_event_to_map(event);\n"\
			"}\n"\
			"\n"\
			"function on_mouse_move(event) {\n"\
			"if (DWGAsDriver) {\n"\
			"	if (ldown) {\n"\
			"		GLCameraX = GLCameraSX;\n"\
			"		GLCameraY = GLCameraSY;\n"\
			"		get_mouse_coords(event);\n"\
			"		if (!ldrag) {\n"\
			"			GLCameraSX = GLCameraX;\n"\
			"			GLCameraSY = GLCameraY;\n"\
			"			}\n"\
			"		if (delta_x != 0 || delta_y != 0) ldrag = true;\n"\
			"		delta_x = dwg_x-ldown_x;\n"\
			"		delta_y = dwg_y-ldown_y;\n"\
			"		GLCameraX -= delta_x;\n"\
			"		GLCameraY -= delta_y;\n"\
			"	        \n"\
			"		osm_notity_zoom(); \n"\
			"		 \n"\
			"	        draw_scene(1);\n"\
			"		}\n"\
			"	}\n"\
			"	post_event_to_map(event);\n"\
			"}\n"\
			"\n"\
			"\n"\
			"function on_mouse_wheel (event) {\n"\
			"if (DWGAsDriver) {\n"\
			"	if (!event) event = window.event;\n"\
			"	if (event.wheelDelta) {\n"\
			"		delta = event.wheelDelta/120;\n"\
			"		} else if (event.detail) {\n"\
			"		delta = -event.detail/3;\n"\
			"		}\n"\
			"	\n"\
			"	get_mouse_coords(event);\n"\
			"	if (delta > 0) {\n"\
			"		zoom('+', dwg_x, dwg_y);\n"\
			"		} else if (delta < 0) {\n"\
			"		zoom('-', dwg_x, dwg_y);\n"\
			"		}\n"\
			"	if (event.preventDefault) event.preventDefault();\n"\
			"	event.returnValue = false;\n"\
			"	post_event_to_map(event);\n"\
			"	return false;\n"\
			"	}\n"\
			"post_event_to_map(event);\n"\
			"return true;\n"\
			"}\n"\
			"\n"\
			"\n"\
			"function post_event_to_map(e) {\n"\
			"    var myEvt = document.createEvent('MouseEvents');\n"\
			"    myEvt.initMouseEvent(e.type, e.bubbles, e.cancelable, window, e.detail,\n"\
			"        e.screenX, e.screenY, e.clientX, e.clientY, e.ctrlKey, e.altKey, e.shiftKey,\n"\
			"        e.metaKey, e.button, e.relatedTarget);\n"\
			"	mapDiv = document.getElementById('mapdiv');\n"\
			"	if (mapDiv) mapDiv.dispatchEvent(myEvt);\n"\
			"}\n"\
			"\n"\
			"\n"\
			"function search_on_tags(pdwg_x, pdwg_y) {\n"\
			"    for (i=0; i<GLTextsData.length; i++) {\n"\
			"        if (pdwg_x >= GLTextsData[i].x && pdwg_x <= GLTextsData[i].x+GLTextsData[i].wh) {\n"\
			"            if (pdwg_y <= GLTextsData[i].y && pdwg_y >= GLTextsData[i].y-GLTextsData[i].ht) {\n"\
			"                on_click_to_tag(GLTextsData[i].t);\n"\
			"                // alert(GLTextsData[i].t);\n"\
			"                return i;\n"\
			"                }\n"\
			"            }\n"\
			"        }\n"\
			"	return -1;\n"\
			"    }\n"\
			"    \n"\
			"    \n"\
			"function search_for_text(psearch) {\n"\
			"    var search = psearch.toString().toUpperCase();\n"\
			"    for (i=0; i<GLTextsData.length; i++) {\n"\
			"        if (search===GLTextsData[i].t.toUpperCase()) {\n"\
			"            return i;\n"\
			"            }\n"\
			"        }\n"\
			"    return -1;\n"\
			"    }\n"\
			"	\n"\
			"	\n"\
			"function zoom_to_tag(i) {	\n"\
			"    if (i>=0 && i<GLTextsData.length) {\n"\
			"        var ht = GLTextsData[i].ht;\n"\
			"        var wh = GLTextsData[i].t.length * ht;\n"\
			"        var tx = GLTextsData[i].x+(wh>0.0?wh/2.0:0.0);\n"\
			"        var ty = GLTextsData[i].y+(ht/2.0);\n"\
			"        var cameraWh = 0.0;\n"\
			"        if (GLTextsData[i].wh > 0.0) cameraWh = wh * 4.0; else cameraWh = ht * 12.0;\n"\
			"        if (cameraWh <= 0.0) cameraWh = 10.0;\n"\
			"        var cameraHt = (cameraWh/GLCanvasWX*GLCanvasWY);\n"\
			"        GLCameraX=tx-cameraWh / 2.0;\n"\
			"        GLCameraY=ty-cameraHt / 2.0;\n"\
			"        GLCameraWX=cameraWh;\n"\
			"        GLCameraWY=cameraHt;\n"\
			"	\n"\
			"        osm_notity_zoom(); \n"\
			"	\n"\
			"        draw_scene(0);\n"\
			"        }\n"\
			"    }\n"\
			"	\n"\
			"	\n"\
			"function on_click_to_tag(cdoggetto) { \n"\
			"    try {\n"\
			"    var jsonObj = '{\"CDOGGETTO\":\"'+cdoggetto+'\"}';\n"\
			"		zAu.send(new zk.Event(zk.Widget.$(idDivCanvas), 'onMostraDettagliOggetto', jsonObj)); \n"\
			"           } catch(e) {\n"\
			"           alert (\"oggetto:\"+cdoggetto+\"\\r\\n\"+e.message);\n"\
			"           }\n"\
			"   }\n"\
			"	\n"\
			;




	    // Timestamp
	#ifdef JNI_DLLxxx
		#elif defined JNI_SO
		#else
	{ 	char str[256];
		time_t ltime;
		ltime=time(NULL);
		sprintf(str, "/////////////////////////////////////////////////////////////////////////////////\n");
		AddStr(out_string, str, out_string_allocated);
		sprintf(str, "// Geisoft Canvas Timestamp:%s", asctime( localtime(&ltime) ));
		AddStr(out_string, str, out_string_allocated);
		AddStr(out_string, "//\n", out_string_allocated);
		sprintf(str, "// Generator : libiMuveCPP Ver.: ");
		AddStr(out_string, str, out_string_allocated);
		sprintf(str, "%0.2f", pCanvasData->Version);
		AddStr(out_string, str, out_string_allocated);
		AddStr(out_string, "\n", out_string_allocated);
		AddStr(out_string, "//\n\n", out_string_allocated);
		}
		#endif


		// Intestazione
		AddStr(out_string, init_header_str, out_string_allocated);



		//////////////////
		// Dati camera
		//
		sprintf(str, "var GLHomeCameraX=%0.2f, GLHomeCameraY=%0.2f, GLHomeCameraWX=%0.2f, GLHomeCameraWY=%0.2f;\n", pCanvasData->CameraX, pCanvasData->CameraY, pCanvasData->CameraWX, pCanvasData->CameraWY);
		AddStr(out_string, str, out_string_allocated);
		sprintf(str, "var GLCameraX=GLHomeCameraX, GLCameraY=GLHomeCameraY, GLCameraWX=GLHomeCameraWX, GLCameraWY=GLHomeCameraWY;\n");
		AddStr(out_string, str, out_string_allocated);
		sprintf(str, "var GLCanvasWX=%d, GLCanvasWY=%d;\n", width>0?width:0, height>0?height:0 );
		AddStr(out_string, str, out_string_allocated);



		if (pSpatialFilter) {
			AddStr(out_string, "\r\n", out_string_allocated);
			AddStr(out_string, "////////////////////////////////////////\r\n", out_string_allocated);
			AddStr(out_string, "// SpatialFilter Mode\r\n", out_string_allocated);
			AddStr(out_string, "//\r\n", out_string_allocated);
			sprintf(str, "var GLNumQuadsX=%d, GLNumQuadsY=%d;\n", pSpatialFilter->NumSpatialQuadsCols, pSpatialFilter->NumSpatialQuadsRecs);
			AddStr(out_string, str, out_string_allocated);
			sprintf(str, "var GLQuads = new Array ();\n");
			AddStr(out_string, str, out_string_allocated);
			AddStr(out_string, "\r\n", out_string_allocated);
			AddStr(out_string, "\r\n", out_string_allocated);

			} else {

			AddStr(out_string, "\r\n", out_string_allocated);
			AddStr(out_string, "////////////////////////////////////////\r\n", out_string_allocated);
			AddStr(out_string, "// SpatialFilter Disabled\r\n", out_string_allocated);
			AddStr(out_string, "//\r\n", out_string_allocated);
			sprintf(str, "var GLNumQuadsX=0, GLNumQuadsY=0;\n");
			AddStr(out_string, str, out_string_allocated);
			sprintf(str, "var GLQuads = null;\n");
			AddStr(out_string, str, out_string_allocated);
			AddStr(out_string, "\r\n", out_string_allocated);
			AddStr(out_string, "\r\n", out_string_allocated);
			}




		/////////////////////////////////////////////////
		// Funzione istanziamento dati globali
		//
		AddStr(out_string, init_footer_str1, out_string_allocated);

		/////////////////
		// Dati linee
		//
		if (pSpatialFilter) {

			AddStr(out_string, "// Quads GLobal Data\r\n\r\n", out_string_allocated);
			for (i=0; i<pSpatialFilter->NumSpatialQuads; i++) {
				sprintf(str, "GLQuads.push({ID:%d,X:%0.6f,Y:%0.6f,X2:%0.6f,Y2:%0.6f,verticesBuffer:0});\n", i+1, pSpatialFilter->SpatialQuads[i].X, pSpatialFilter->SpatialQuads[i].Y, pSpatialFilter->SpatialQuads[i].X2, pSpatialFilter->SpatialQuads[i].Y2);
				AddStr(out_string, str, out_string_allocated);
				}
			AddStr (out_string, str, out_string_allocated);
			sprintf(str, "\r\n");
			AddStr(out_string, str, out_string_allocated );
			sprintf(str, "\r\n");
			AddStr(out_string, str, out_string_allocated );

			// DEBUG
			sprintf(str, "\r\n");
			AddStr(out_string, str, out_string_allocated );
			sprintf(str, "// Dati globali linee (Modalità debug)\"\r\n");
			AddStr(out_string, str, out_string_allocated );
			for (i=0; i<GLSpatialFilter.NumSpatialQuads; i++) {
				AddStr( out_string, GLSpatialFilter.SpatialQuads[i].JSData, out_string_allocated );
				sprintf(str, "\r\n");
				AddStr( out_string, str, out_string_allocated );
				}

			} else {
			AddStr(out_string, "// Lines GLobal Data\r\n\r\n", out_string_allocated);
			AddStr(out_string, GLCanvasLines, out_string_allocated);
			}



		//////////////////
		// Dati testi
		//
		if (pSpatialFilter) {
			} else {
			AddStr(out_string, "// Text GLobal Data\r\n\r\n", out_string_allocated);
			AddStr(out_string, GLCanvasTexts, out_string_allocated);
			}





		///////////////////////////////////////
		// Post dischiarazioni dati globali
		//
		AddStr(out_string, init_footer_str2, out_string_allocated);


		/////////////////////
		// Corpo funzioni
		//
		AddStr(out_string, init_footer_str3_0, out_string_allocated);


		if (Options & 2) {
			// Modalità Trasparente
			AddStr(out_string, init_footer_str3_1b, out_string_allocated);
		} else {
			AddStr(out_string, init_footer_str3_1a, out_string_allocated);
		}

		AddStr(out_string, init_footer_str3_2, out_string_allocated);






	#ifdef JNI_DLL
	#elif defined JNI_SO
	    if (pSpatialFilter) {
		}
	#else
	#endif


	return 1;


	} else {
	return 0;
	}
}


