#include <jni.h>
#include <stdio.h>
#include "app_DisplayManager.h"

#include "pifacecad.h"

JNIEXPORT void JNICALL Java_app_DisplayManager_sendText (JNIEnv *env, jobject thisObj, jstring text){
    char *pText = (char *)(* env)->GetStringUTFChars(env, text, NULL);
    if (pText == NULL) {
        return;
    }
    pifacecad_lcd_write(pText);
}

JNIEXPORT void JNICALL Java_app_DisplayManager_setBacklightState (JNIEnv *env, jobject thisObj, jboolean state){
    if (state) {
        pifacecad_lcd_backlight_on();
    } else {
        pifacecad_lcd_backlight_off();
    }
}

JNIEXPORT void JNICALL Java_app_DisplayManager_setCursorVisibility (JNIEnv *env, jobject thisObj, jboolean state){
    if (state) {
        pifacecad_lcd_cursor_on();
    } else {
        pifacecad_lcd_cursor_off();
    }
}

JNIEXPORT void JNICALL Java_app_DisplayManager_clearScreen (JNIEnv *env, jobject thisObj){
    pifacecad_lcd_clear();
}

JNIEXPORT void JNICALL Java_app_DisplayManager_setLcdState (JNIEnv *env, jobject thisObj, jboolean state){
    if (state) {
        pifacecad_lcd_display_on();
    } else {
        pifacecad_lcd_display_off();
    }
}

JNIEXPORT jbyte JNICALL Java_app_DisplayManager_initLcd (JNIEnv *env, jobject thisObj){
    if (pifacecad_open() == -1) {
        return 1;
    }
    pifacecad_lcd_backlight_on();
    pifacecad_lcd_clear();
    pifacecad_lcd_cursor_off();
    pifacecad_lcd_blink_off();
    pifacecad_lcd_display_on();

}