#include <jni.h>
#include <stdio.h>
#include "app_DisplayManager.h"

JNIEXPORT void JNICALL Java_app_DisplayManager_sendText (JNIEnv *, jobject, jstring);

JNIEXPORT void JNICALL Java_app_DisplayManager_setBacklightState (JNIEnv *, jobject, jboolean);

JNIEXPORT void JNICALL Java_app_DisplayManager_setCursorVisibility (JNIEnv *, jobject, jboolean);

JNIEXPORT void JNICALL Java_app_DisplayManager_clearScreen (JNIEnv *, jobject);

JNIEXPORT void JNICALL Java_app_DisplayManager_setLcdState (JNIEnv *, jobject, jboolean);

JNIEXPORT void JNICALL Java_app_DisplayManager_initLcd (JNIEnv *, jobject);