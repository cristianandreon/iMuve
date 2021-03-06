/*****************************************************************************/
/*  LibreDWG - free implementation of the DWG file format                    */
/*                                                                           */
/*  Copyright (C) 2010 Free Software Foundation, Inc.                        */
/*                                                                           */
/*  This library is free software, licensed under the terms of the GNU       */
/*  General Public License as published by the Free Software Foundation,     */
/*  either version 3 of the License, or (at your option) any later version.  */
/*  You should have received a copy of the GNU General Public License        */
/*  along with this program.  If not, see <http://www.gnu.org/licenses/>.    */
/*****************************************************************************/

/*
 * logging.h: logging macros
 * written by Rodrigo Rodrigues da Silva
 */

//Reduce logging code through macros. In the future, this file can be used as
//an interface to use more sophisticated logging libraries such as gnu nana

#ifndef LOGGING_H
#define LOGGING_H

#include <stdio.h>
// #include <string.h>


#ifdef WINDOWS
	#else
	// #include <android/log.h>
	#endif

#ifdef __cplusplus
    #define LOGGING_KEY   extern "C"
    #else
    #define LOGGING_KEY   extern
    #endif

LOGGING_KEY char GLLogStr[];

LOGGING_KEY void my_printf (char *format,...);



/*
 * If more logging levels are necessary, put them in the right place and
 * update the numbering, keeping it a 0,1,...n sequence, where n corresponds
 * to LOGLEVEL_ALL. If LOGLEVEL is set to k, all messages with LOGLEVEL < k
 * will be displayed
 */

#define DWG_LOGLEVEL_NONE    0 //no log
#define DWG_LOGLEVEL_ERROR   1 //only error messages
#define DWG_LOGLEVEL_INFO    2 //only general info and object codes/names
#define DWG_LOGLEVEL_TRACE   3 //eg for each value parsed
#define DWG_LOGLEVEL_INSANE  4 //print all referenced objects (handles)
// #define LOGLEVEL_FOO .. //if more codes are necessary
#define DWG_LOGLEVEL_ALL     9

#ifndef DWG_LOGLEVEL
    #define DWG_LOGLEVEL DWG_LOGLEVEL_NONE //default loglevel
    #endif //ifndef LOGLEVEL



#define OUTPUT stderr




#ifdef WINDOWS
	
	#define LOG(__txt,...)		printf(__txt,##__VA_ARGS__);
	#define LOG_ERROR(__txt,...)	printf(__txt,##__VA_ARGS__);
	#define LOG_INFO(__txt,...)	printf(__txt,##__VA_ARGS__);
	#define LOG_TRACE(__txt,...)	printf(__txt,##__VA_ARGS__);
	#define LOG_ALL(__txt,...)	printf(__txt,##__VA_ARGS__);

	#else

	#define LOG(__txt,...)		my_printf(__txt,##__VA_ARGS__);
	#define LOG_ERROR(__txt,...)	my_printf(__txt,##__VA_ARGS__);
	#define LOG_INFO(__txt,...)	my_printf(__txt,##__VA_ARGS__);
	#define LOG_TRACE(__txt,...)	my_printf(__txt,##__VA_ARGS__);
	#define LOG_ALL(__txt,...)	my_printf(__txt,##__VA_ARGS__);

	#endif



// #define LOG_INFO(args...) LOG(INFO, args)
// #define LOG_TRACE(args...) LOG(TRACE, args)
// #define LOG_ALL(args...) LOG(ALL, args)


#endif //#ifndef LOGGING_H
