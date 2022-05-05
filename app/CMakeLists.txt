# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

include_directories ("/home/ubuntu/AndroidDevelop/iMuve/app/src/main/jni")
include_directories ("/Users/administrator/IntelliJProjects/MUVE/iMuve/app/src/main/jni")
include_directories ("src/main/jni")


add_library( # Sets the name of the library.
             imuvecpp

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
                src/main/jni/iMuveCPP.c
                src/main/jni/c-sources/dwg_utility.c
                src/main/jni/c-sources/ColorTable.c
                src/main/jni/c-sources/Draw.c
                src/main/jni/c-sources/matrix.c
                src/main/jni/c-sources/quat.c
                src/main/jni/c-sources/vector.c
                src/main/jni/lib-dwg-master/dwg.c
                src/main/jni/lib-dwg-master/bits.c
                src/main/jni/lib-dwg-master/common.c
                src/main/jni/lib-dwg-master/logging.c
                src/main/jni/lib-dwg-master/decode.c
                src/main/jni/lib-dwg-master/decode_r2007.c
                src/main/jni/utility/memory_allocator.c
                src/main/jni/utility/OpenGLWrapper.c
                src/main/jni/utility/triangulate.cpp
             )



target_link_libraries( # Specifies the target library.
                       imuvecpp

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                       GLESv2
                       GLESv1_CM
                       )


# target_link_libraries( GLESv2 GLESv1_CM )
# target_link_libraries(iMuveCPP GLESv2 GLESv1_CM)



# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.


# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.



#APP_CPPFLAGS += -fexceptions
#set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DANDROID_NDK -DDISABLE_IMPORTGL -fexceptions" )
