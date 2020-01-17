#include <jni.h>
#include <stdio.h>
#include "app_ButtonManager.h"

JNIEXPORT jbyte JNICALL Java_app_ButtonManager_getButtonStates (JNIEnv *env, jobject thisObj) {
    return (jbyte) pifacecad_read_switches();
}