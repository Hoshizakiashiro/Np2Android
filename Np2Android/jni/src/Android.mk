LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := main

SDL_PATH := ../SDL

CG_SUBDIRS := \
np2

NP2_COMMON		:= $(CG_SUBDIRS)/common
NP2_CODECNV		:= $(CG_SUBDIRS)/codecnv
NP2_I386C		:= $(CG_SUBDIRS)/i386c
NP2_IA32		:= $(CG_SUBDIRS)/i386c/ia32
NP2_INST		:= $(CG_SUBDIRS)/i386c/ia32/instructions
NP2_FPU			:= $(CG_SUBDIRS)/i386c/ia32/instructions/fpu
NP2_MEM			:= $(CG_SUBDIRS)/mem
NP2_IO			:= $(CG_SUBDIRS)/io
NP2_CBUS		:= $(CG_SUBDIRS)/cbus
NP2_BIOS		:= $(CG_SUBDIRS)/bios
NP2_SOUND		:= $(CG_SUBDIRS)/sound
NP2_VERMOUTH		:= $(CG_SUBDIRS)/sound/vermouth
NP2_GETSND		:= $(CG_SUBDIRS)/sound/getsnd
NP2_VRAM		:= $(CG_SUBDIRS)/vram
NP2_FDD			:= $(CG_SUBDIRS)/fdd
NP2_LIO			:= $(CG_SUBDIRS)/lio
NP2_FONT		:= $(CG_SUBDIRS)/font
NP2_GENERIC		:= $(CG_SUBDIRS)/generic
NP2_EMBED		:= $(CG_SUBDIRS)/embed
NP2_MENU		:= $(CG_SUBDIRS)/embed/menu
NP2_MENUBASE		:= $(CG_SUBDIRS)/embed/menubase
NP2_PLATFORM		:= $(CG_SUBDIRS)/android
NP2_PLATFORM2		:= $(CG_SUBDIRS)/android/android

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/$(SDL_PATH)/include	\
	$(LOCAL_PATH)/$(CG_SUBDIRS)			\
	$(LOCAL_PATH)/$(NP2_COMMON)		\
	$(LOCAL_PATH)/$(NP2_CODECNV)		\
	$(LOCAL_PATH)/$(NP2_I386C)		\
	$(LOCAL_PATH)/$(NP2_IA32)	\
	$(LOCAL_PATH)/$(NP2_INST)	\
	$(LOCAL_PATH)/$(NP2_FPU)	\
	$(LOCAL_PATH)/$(NP2_MEM)			\
	$(LOCAL_PATH)/$(NP2_IO)			\
	$(LOCAL_PATH)/$(NP2_CBUS)			\
	$(LOCAL_PATH)/$(NP2_BIOS)			\
	$(LOCAL_PATH)/$(NP2_SOUND)		\
	$(LOCAL_PATH)/$(NP2_VERMOUTH)		\
	$(LOCAL_PATH)/$(NP2_GETSND)		\
	$(LOCAL_PATH)/$(NP2_VRAM)			\
	$(LOCAL_PATH)/$(NP2_FDD)			\
	$(LOCAL_PATH)/$(NP2_LIO)			\
	$(LOCAL_PATH)/$(NP2_FONT)			\
	$(LOCAL_PATH)/$(NP2_GENERIC)		\
	$(LOCAL_PATH)/$(NP2_EMBED)		\
	$(LOCAL_PATH)/$(NP2_MENU)			\
	$(LOCAL_PATH)/$(NP2_MENUBASE)		\
	$(LOCAL_PATH)/$(NP2_PLATFORM)		\
	$(LOCAL_PATH)/$(NP2_PLATFORM2)	\


OBJSUFFIX := .o
EXT_OBJS = 


NP2_SRCS :=	$(CG_SUBDIRS)/pccore.c	\
			$(CG_SUBDIRS)/nevent.c	\
			$(CG_SUBDIRS)/calendar.c	\
			$(CG_SUBDIRS)/timing.c	\
			$(CG_SUBDIRS)/keystat.c	\
			$(CG_SUBDIRS)/statsave.c	\
			$(CG_SUBDIRS)/debugsub386.c	\
			$(NP2_COMMON)/*.c		\
			$(NP2_CODECNV)/*.c		\
			$(NP2_I386C)/*.c		\
			$(NP2_IA32)/*.c	\
			$(NP2_INST)/*.c	\
			$(NP2_FPU)/*.c	\
			$(NP2_MEM)/dmax86.c			\
			$(NP2_MEM)/memegc.c			\
			$(NP2_MEM)/memems.c			\
			$(NP2_MEM)/memepp.c			\
			$(NP2_MEM)/memtram.c			\
			$(NP2_MEM)/memvga.c			\
			$(NP2_MEM)/memvram.c			\
			$(NP2_IO)/*.c			\
			$(NP2_CBUS)/*.c			\
			$(NP2_BIOS)/*.c			\
			$(NP2_SOUND)/*.c		\
			$(NP2_VERMOUTH)/*.c		\
			$(NP2_GETSND)/*.c		\
			$(NP2_VRAM)/*.c			\
			$(NP2_FDD)/*.c			\
			$(NP2_LIO)/*.c			\
			$(NP2_FONT)/*.c			\
			$(NP2_GENERIC)/cmjasts.c	\
			$(NP2_GENERIC)/cmver.c	\
			$(NP2_EMBED)/*.c		\
			$(NP2_MENU)/*.c			\
			$(NP2_MENUBASE)/*.c		\
			$(NP2_PLATFORM)/*.c



# Add any compilation flags for your project here...
LOCAL_CFLAGS := -DSIZE_VGA -DCPUCORE_IA32

# Add your application source files here...
LOCAL_SRC_FILES := $(SDL_PATH)/src/main/android/SDL_android_main.cpp 
LOCAL_SRC_FILES += $(foreach F, $(NP2_SRCS), $(addprefix $(dir $(F)),$(notdir $(wildcard $(LOCAL_PATH)/$(F)))))

LOCAL_SHARED_LIBRARIES := SDL

LOCAL_LDLIBS := -lGLESv1_CM -llog

ifeq ($(TARGET_ARCH_ABI),armeabi-v7a)
 LOCAL_ARM_MODE := arm
 LOCAL_ARM_NEON := true
endif

include $(BUILD_SHARED_LIBRARY)
