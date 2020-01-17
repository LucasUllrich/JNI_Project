#include <jni.h>
#include <stdio.h>
#include <stdint.h>
#include "app_ButtonManager.h"

#ifndef PC_BUILD
#include "pifacecad.h"
#endif

#define NUM_OF_BUTTONS (8)

JNIEXPORT jbyte JNICALL Java_app_ButtonManager_getButtonStates (JNIEnv *env, jobject thisObj) {
#ifndef PC_BUILD
    uint8_t buttons;
    buttons = pifacecad_read_switches();

    for (uint8_t counter = 1; counter <= NUM_OF_BUTTONS; counter++) {
        if ((~(buttons) >> (counter - 1)) & 0x01) {
            return (jbyte) counter;
        }
    }
#endif
    return (jbyte) 0;
}
