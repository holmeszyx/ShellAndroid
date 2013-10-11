LOCAL_PATH := $(call my-dir)

# create cflg

include $(CLEAR_VARS)
MY_LOCAL_PATH := ${LOCAL_PATH}
LOCAL_MODULE    := cflag
LOCAL_SRC_FILES := main/cflag.cpp

LOCAL_LDLIBS := -llog
include $(BUILD_EXECUTABLE)
