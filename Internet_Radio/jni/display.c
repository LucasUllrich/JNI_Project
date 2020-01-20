#include <jni.h>
#include <stdio.h>
#include <stdint.h>
#include "app_DisplayManager.h"

#ifndef PC_BUILD
#include "pifacecad.h"
#endif

/**
 * Send a text to the previously set cursor position
 * @param text the text to be displayed on the screen
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_sendText (JNIEnv *env, jobject thisObj, jstring text) {
#ifndef PC_BUILD
    char *pText = (char *)(* env)->GetStringUTFChars(env, text, NULL);
    if (pText == NULL) {
        return;
    }
    pifacecad_lcd_write(pText);
#endif
}

/**
 * Turn the backlight on or off
 * @param state the desired power state of the backlight
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_setBacklightState (JNIEnv *env, jobject thisObj, jboolean state) {
#ifndef PC_BUILD
    if (state) {
        pifacecad_lcd_backlight_on();
    } else {
        pifacecad_lcd_backlight_off();
    }
#endif
}

/**
 * Turn the cursor visibility on or off
 * @param state the desired visbility of the cursor
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_setCursorVisibility (JNIEnv *env, jobject thisObj, jboolean state) {
#ifndef PC_BUILD
    if (state) {
        pifacecad_lcd_cursor_on();
    } else {
        pifacecad_lcd_cursor_off();
    }
#endif
}

/**
 * Clear all content from the screen
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_clearScreen (JNIEnv *env, jobject thisObj) {
#ifndef PC_BUILD
    pifacecad_lcd_clear();
#endif
}

/**
 * Turn the LCD on or off
 * @param state the desired power state of the display
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_setLcdState (JNIEnv *env, jobject thisObj, jboolean state) {
#ifndef PC_BUILD
    if (state) {
        pifacecad_lcd_display_on();
    } else {
        pifacecad_lcd_display_off();
    }
#endif
}

/**
 * Initialize the display for operation and good visibility
 */
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
    pifacecad_lcd_set_cursor(0, 0);
#endif
    return 0;
}

/**
 * Change the autoscroll functionality
 * @param state the desired state of the autoscroll functionality
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_autoscrollLcd (JNIEnv *env, jobject thisObj, jboolean state) {
#ifndef PC_BUILD
    if (state) {
        pifacecad_lcd_autoscroll_on();
    } else {
        pifacecad_lcd_autoscroll_off();
    }
#endif
}

/**
 * Change the cursor position
 * @param col the column the cursor should be set to
 * @param row the row the cursor should be set to
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_setCursourPosition (JNIEnv *env, jobject thisObj, jint col, jint row) {
#ifndef PC_BUILD
    pifacecad_lcd_set_cursor((uint8_t) col, (uint8_t) row);
#endif
}
