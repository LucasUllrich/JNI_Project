#include <jni.h>
#include <stdio.h>
#include "app_DisplayManager.h"

#ifndef PC_BUILD
#include "pifacecad.h"
#endif

JNIEXPORT void JNICALL Java_app_DisplayManager_sendText (JNIEnv *env, jobject thisObj, jstring text) {
#ifndef PC_BUILD
    char *pText = (char *)(* env)->GetStringUTFChars(env, text, NULL);
    if (pText == NULL) {
        return;
    }
    pifacecad_lcd_write(pText);
#endif
}

JNIEXPORT void JNICALL Java_app_DisplayManager_setBacklightState (JNIEnv *env, jobject thisObj, jboolean state) {
#ifndef PC_BUILD
    if (state) {
        pifacecad_lcd_backlight_on();
    } else {
        pifacecad_lcd_backlight_off();
    }
#endif
}

JNIEXPORT void JNICALL Java_app_DisplayManager_setCursorVisibility (JNIEnv *env, jobject thisObj, jboolean state) {
#ifndef PC_BUILD
    if (state) {
        pifacecad_lcd_cursor_on();
    } else {
        pifacecad_lcd_cursor_off();
    }
#endif
}

JNIEXPORT void JNICALL Java_app_DisplayManager_clearScreen (JNIEnv *env, jobject thisObj) {
#ifndef PC_BUILD
    pifacecad_lcd_clear();
#endif
}

JNIEXPORT void JNICALL Java_app_DisplayManager_setLcdState (JNIEnv *env, jobject thisObj, jboolean state) {
#ifndef PC_BUILD
    if (state) {
        pifacecad_lcd_display_on();
    } else {
        pifacecad_lcd_display_off();
    }
#endif
}

JNIEXPORT jbyte JNICALL Java_app_DisplayManager_initLcd (JNIEnv *env, jobject thisObj) {
#ifndef PC_BUILD
    if (pifacecad_open() == -1) {
        return 1;
    }
    pifacecad_lcd_backlight_on();
    pifacecad_lcd_clear();
    pifacecad_lcd_cursor_off();
    pifacecad_lcd_blink_off();
    pifacecad_lcd_display_on();
    pifacecad_lcd_autoscroll_on();
#endif
    return 0;
}

JNIEXPORT void JNICALL Java_app_DisplayManager_autoscrollLcd (JNIEnv *env, jobject thisObj, jboolean state) {
#ifndef PC_BUILD
    if (state) {
        pifacecad_lcd_autoscroll_on();
    } else {
        pifacecad_lcd_autoscroll_off();
    }
#endif
}