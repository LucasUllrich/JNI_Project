#include <jni.h>
#include <stdio.h>
#include <stdint.h>
#include "app_ButtonManager.h"

#include "pifacecad.h"

#define NUM_OF_BUTTONS (8)

JNIEXPORT jbyte JNICALL Java_app_ButtonManager_getButtonStates (JNIEnv *env, jobject thisObj) {
    uint8_t buttons;
    buttons = pifacecad_read_switches();

    // printf ("Button: %d\n", buttons);

    for (uint8_t counter = 0; counter < NUM_OF_BUTTONS; counter++) {
        if ((~(buttons) >> counter) & 0x01) {
            return (jbyte) counter;
        }
    }
    return (jbyte) 0;
}
