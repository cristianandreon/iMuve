// jni2dll.cpp : definisce il punto di ingresso dell'applicazione DLL.
//

#include "stdafx.h"
#include "jni.h"

#ifdef _MANAGED
#pragma managed(push, off)
#endif


BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
					 ) {
switch(ul_reason_for_call) {
	case DLL_PROCESS_ATTACH:
		return TRUE;
		break;
	case DLL_THREAD_ATTACH:
		return TRUE;
		break;
	case DLL_THREAD_DETACH:
		return TRUE;
		break;
	case DLL_PROCESS_DETACH:
		return TRUE;
		break;
	case DLL_PROCESS_VERIFIER:
		return TRUE;
		break;
	}
    return TRUE;
}

#ifdef _MANAGED
#pragma managed(pop)
#endif


