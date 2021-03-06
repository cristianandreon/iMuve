/*****************************************************************************/
/*  LibreDWG - free implementation of the DWG file format                    */
/*                                                                           */
/*  Copyright (C) 2009 Free Software Foundation, Inc.                        */
/*                                                                           */
/*  This library is free software, licensed under the terms of the GNU       */
/*  General Public License as published by the Free Software Foundation,     */
/*  either version 3 of the License, or (at your option) any later version.  */
/*  You should have received a copy of the GNU General Public License        */
/*  along with this program.  If not, see <http://www.gnu.org/licenses/>.    */
/*****************************************************************************/

/*
 * common.c: common data arrays
 * written by Felipe Castro
 * modified by Felipe Corrêa da Silva Sances
 * modified by Rodrigo Rodrigues da Silva
 */

#define _STLP_HAS_INCLUDE_NEXT  1
#define _STLP_USE_MALLOC   1
#define _STLP_USE_NO_IOSTREAMS  1


// #include "config.h"
#include "common.h"
#include "stdio.h"

unsigned char *
dwg_sentinel(Dwg_Sentinel s)
{
  static unsigned char sentinels[9][16] =
    {
      { 0x95, 0xA0, 0x4E, 0x28, 0x99, 0x82, 0x1A, 0xE5, 0x5E, 0x41, 0xE0, 0x5F,
          0x9D, 0x3A, 0x4D, 0x00 },
      { 0x1F, 0x25, 0x6D, 0x07, 0xD4, 0x36, 0x28, 0x28, 0x9D, 0x57, 0xCA, 0x3F,
          0x9D, 0x44, 0x10, 0x2B },
      { 0xE0, 0xDA, 0x92, 0xF8, 0x2B, 0xc9, 0xD7, 0xD7, 0x62, 0xA8, 0x35, 0xC0,
          0x62, 0xBB, 0xEF, 0xD4 },
      { 0xCF, 0x7B, 0x1F, 0x23, 0xFD, 0xDE, 0x38, 0xA9, 0x5F, 0x7C, 0x68, 0xB8,
          0x4E, 0x6D, 0x33, 0x5F },
      { 0x30, 0x84, 0xE0, 0xDC, 0x02, 0x21, 0xC7, 0x56, 0xA0, 0x83, 0x97, 0x47,
          0xB1, 0x92, 0xCC, 0xA0 },
      { 0x8D, 0xA1, 0xC4, 0xB8, 0xC4, 0xA9, 0xF8, 0xC5, 0xC0, 0xDC, 0xF4, 0x5F,
          0xE7, 0xCF, 0xB6, 0x8A },
      { 0x72, 0x5E, 0x3B, 0x47, 0x3B, 0x56, 0x07, 0x3A, 0x3F, 0x23, 0x0B, 0xA0,
          0x18, 0x30, 0x49, 0x75 },
      { 0xD4, 0x7B, 0x21, 0xCE, 0x28, 0x93, 0x9F, 0xBF, 0x53, 0x24, 0x40, 0x09,
          0x12, 0x3C, 0xAA, 0x01 },
      { 0x2B, 0x84, 0xDE, 0x31, 0xD7, 0x6C, 0x60, 0x40, 0xAC, 0xDB, 0xBF, 0xF6,
          0xED, 0xC3, 0x55, 0xFE } };

  return (sentinels[s]);
}

/* TODO: update with new codes */
char version_codes[8][7] =
  { "------", "AC1012", "AC1014", "AC1015", "AC1018", "AC1021", "AC1024", "------" };

