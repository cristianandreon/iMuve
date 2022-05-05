LOCAL_PATH := $(call my-dir)

APP_PROJECT_PATH := $PROJECT_DIR$
APP_BUILD_SCRIPT := $PROJECT_DIR$/app/src/main/jni/Android.mk
### APP_PROJECT_PATH := /home/ubuntu/AndroidDevelop
### APP_BUILD_SCRIPT := /home/ubuntu/AndroidDevelop/app/src/main/jni/Android.mk


APP_ABI := armeabi-v7a
APP_OPTIM := debug


### Non piu' supportato
### APP_STL := stlport_static
### APP_STL := gnustl_static
### GNUSTL_STATIC := true

APP_PLATFORM := android-21
### NDK_TOOLCHAIN_VERSION := 4.8

LOCAL_LDLIBS := -lGLESv2 -lGLESv1_CM
APP_CPPFLAGS += -fexceptions
LOCAL_CPP_FEATURES := exceptions
APP_CPPFLAGS := -frtti

###  APP_ALLOW_MISSING_DEPS := true