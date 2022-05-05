#define _STLP_HAS_INCLUDE_NEXT  1
#define _STLP_USE_MALLOC   1
#define _STLP_USE_NO_IOSTREAMS  1



#include "logging.h"
#include <jni.h>

extern JNIEnv *GLJNIenv;
extern jobject GLJNIjobj;



extern int GLdrawOptions;



#ifdef JNI_DLL
	
	void my_printf (char *format,...) {
		char dest[1024 * 16];

		va_list argptr;
		va_start(argptr, format);
		vsprintf(dest, format, argptr);
		va_end(argptr);
		printf(dest);

		#ifdef _DEBUG
			// OutputDebugString(dest);
		#endif
	}

	#else
	
	void my_printf (char *format,...) {

	// return;
	if (GLdrawOptions & 1) return;

        return;


	#ifdef JNI_SO
		// return;
	#endif


	if (GLJNIenv) {

		char dest[1024 * 16];

		va_list argptr;
		va_start(argptr, format);
		vsprintf(dest, format, argptr);
		va_end(argptr);

/*
		if (GLNumErrorsAllocated < 64*1024) {
		    if (GLNumErrors) {
			AddStr(&GLErrors, "\r\n", &GLNumErrorsAllocated);
			}
		    AddStr(&GLErrors, dest, &GLNumErrorsAllocated);
		    GLNumErrors++;
		    }
*/

        {

            jstring jstr = (*GLJNIenv)->NewStringUTF(GLJNIenv, dest);
			jstring gjstr = (*GLJNIenv)->NewGlobalRef(GLJNIenv, jstr);
			jclass jcl = (*GLJNIenv)->FindClass(GLJNIenv, "com/imuve/cristian/imuve/MainActivity");
			jclass gjcl = (*GLJNIenv)->NewGlobalRef(GLJNIenv, jcl);
			if (gjcl) {
			    jmethodID messageMe = (*GLJNIenv)->GetMethodID(GLJNIenv, gjcl, "messageMe", "(Ljava/lang/String;)V");
			    /* jobject result = */
			    if (messageMe) {
				(*GLJNIenv)->CallVoidMethod(GLJNIenv, GLJNIjobj, messageMe, gjstr);
				/// (*GLJNIenv)->DeleteLocalRef(GLJNIenv, messageMe);
				} else {
				#ifdef JNI_DLL
					printf("[C] DEBUG", "No messageMe");
					#else
					// __android_log_print(ANDROID_LOG_ERROR, "[C] DEBUG", "No messageMe");
					#endif
				}
			    } else {
				#ifdef JNI_DLL
					printf("[C] DEBUG", "No jclass");
					#else
					// __android_log_print(ANDROID_LOG_ERROR, "[C] DEBUG", "No jclass");
					#endif
			    }

			(*GLJNIenv)->DeleteLocalRef(GLJNIenv, jstr);
			(*GLJNIenv)->DeleteLocalRef(GLJNIenv, jcl);
			(*GLJNIenv)->DeleteGlobalRef(GLJNIenv, gjstr);
			(*GLJNIenv)->DeleteGlobalRef(GLJNIenv, gjcl);
			}
		}
	    }
	#endif

