//
//  NSData+Base64.h
//  base64
//
//  Created by Matt Gallagher on 2009/06/03.
//  Copyright 2009 Matt Gallagher. All rights reserved.
//
//  Permission is given to use this source code file, free of charge, in any
//  project, commercial or otherwise, entirely at your risk, with the condition
//  that any redistribution (in part or whole) of source code must retain
//  this copyright and permission notice. Attribution in compiled projects is
//  appreciated but not required.
//

#ifndef _Base64EndocdeKey

	#ifdef EXTERN
		#ifdef __cplusplus
			#define _Base64EndocdeKey extern "C"
			#else
			#define _Base64EndocdeKey extern
			#endif
		#else
		#define _Base64EndocdeKey 
		#endif







_Base64EndocdeKey void *NewBase64Decode(
					  const char *inputBuffer,
					  size_t length,
					  size_t *outputLength);

_Base64EndocdeKey char *NewBase64Encode(
					  const void *inputBuffer,
					  size_t length,
					  BOOL separateLines,
					  size_t *outputLength);

#endif
