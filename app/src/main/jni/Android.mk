NDK_PROJECT_PATH := $PROJECT_DIR$/app
### NDK_PROJECT_PATH := /home/ubuntu/AndroidDevelop/app

LOCAL_PATH := $(call my-dir)

# APP_ALLOW_MISSING_DEPS := true

include $(CLEAR_VARS)


LOCAL_MODULE := imuvecpp

LOCAL_SRC_FILES := iMuveCPP.c

LOCAL_SRC_FILES += c-sources/dwg_utility.c
LOCAL_SRC_FILES += c-sources/ColorTable.c
LOCAL_SRC_FILES += c-sources/Draw.c
LOCAL_SRC_FILES += c-sources/matrix.c
LOCAL_SRC_FILES += c-sources/quat.c
LOCAL_SRC_FILES += c-sources/vector.c

LOCAL_SRC_FILES += lib-dwg-master/dwg.c
LOCAL_SRC_FILES += lib-dwg-master/bits.c
LOCAL_SRC_FILES += lib-dwg-master/common.c
LOCAL_SRC_FILES += lib-dwg-master/logging.c
LOCAL_SRC_FILES += lib-dwg-master/decode.c
LOCAL_SRC_FILES += lib-dwg-master/decode_r2007.c

LOCAL_SRC_FILES += utility/memory_allocator.c
LOCAL_SRC_FILES += utility/OpenGLWrapper.c
LOCAL_SRC_FILES += utility/triangulate.cpp


#################################################################
### Libreria GLText2
### LOCAL_SRC_FILES += gltext/AbstractRenderer.cpp
### LOCAL_SRC_FILES += gltext/FTFont.cpp
### LOCAL_SRC_FILES += gltext/FTGlyph.cpp
### LOCAL_SRC_FILES += gltext/GLPixelGlyph.cpp
### LOCAL_SRC_FILES += gltext/gltext.cpp
### LOCAL_SRC_FILES += gltext/GLTextureGlyph.cpp
###
### Includes Libreria GLText2
### LOCAL_C_INCLUDES += $(LOCAL_PATH)/freetype2/include
### LOCAL_C_INCLUDES += $(LOCAL_PATH)/glues/include/glues
#################################################################





### $(warning $(LOCAL_PATH))

# LOCAL_C_INCLUDES += c-source
# LOCAL_C_INCLUDES += lib-dwg-master
# LOCAL_C_INCLUDES += utility

### Libreria GLText
LOCAL_C_INCLUDES += $(LOCAL_PATH)/gltext



### $(warning $(LOCAL_C_INCLUDES))




LOCAL_CFLAGS := -w -DWI -fexceptions -frtti
LOCAL_CPP_FEATURES := exceptions -rtti



LOCAL_LDLIBS := -L$(SYSROOT)/usr/libimuve64 -ldl -lGLESv2 -lGLESv1_CM
LOCAL_STATIC_LIBRARIES := libm libc libstdc++ libGLESv2 libGLESv1_CM

include $(BUILD_SHARED_LIBRARY)


TARGET_PLATFORM := android-21
TARGET_ARCH_ABI := armeabi-v7a
TARGET_ABI := android-21-armeabi-v7a

