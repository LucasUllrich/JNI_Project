/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class app_DisplayManager */

#ifndef _Included_app_DisplayManager
#define _Included_app_DisplayManager
#ifdef __cplusplus
extern "C" {
#endif
#undef app_DisplayManager_MIN_PRIORITY
#define app_DisplayManager_MIN_PRIORITY 1L
#undef app_DisplayManager_NORM_PRIORITY
#define app_DisplayManager_NORM_PRIORITY 5L
#undef app_DisplayManager_MAX_PRIORITY
#define app_DisplayManager_MAX_PRIORITY 10L
/*
 * Class:     app_DisplayManager
 * Method:    sendText
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_sendText
  (JNIEnv *, jobject, jstring);

/*
 * Class:     app_DisplayManager
 * Method:    setBacklightState
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_setBacklightState
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     app_DisplayManager
 * Method:    setCursorVisibility
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_setCursorVisibility
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     app_DisplayManager
 * Method:    clearScreen
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_clearScreen
  (JNIEnv *, jobject);

/*
 * Class:     app_DisplayManager
 * Method:    setLcdState
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_setLcdState
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     app_DisplayManager
 * Method:    initLcd
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_app_DisplayManager_initLcd
  (JNIEnv *, jobject);

/*
 * Class:     app_DisplayManager
 * Method:    autoscrollLcd
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_autoscrollLcd
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     app_DisplayManager
 * Method:    setCursourPosition
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_app_DisplayManager_setCursourPosition
  (JNIEnv *, jobject, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
