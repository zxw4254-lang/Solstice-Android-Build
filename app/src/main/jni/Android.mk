LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := solstice
LOCAL_SRC_FILES := \
    main.cpp \
    Arraylist.cpp \
    MathUtils.cpp \
    ColorUtils.cpp \
    Notifications.cpp \
    imgui/imgui.cpp \
    imgui/imgui_draw.cpp \
    imgui/imgui_tables.cpp \
    imgui/imgui_widgets.cpp \
    imgui/backends/imgui_impl_opengl3.cpp


LOCAL_C_INCLUDES := $(LOCAL_PATH)/imgui $(LOCAL_PATH)/imgui/backends
LOCAL_CPPFLAGS   += -std=c++20
LOCAL_CFLAGS     += -DIMGUI_IMPL_OPENGL_ES2 -DIMGUI_DISABLE_OBSOLETE_FUNCTIONS
LOCAL_LDLIBS     := -llog -lGLESv3 -lm -landroid

include $(BUILD_SHARED_LIBRARY)

