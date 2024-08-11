#include <jni.h>
#include "codes_laivy_serializable_Allocator.h"

JNIEXPORT jobject JNICALL Java_codes_laivy_serializable_Allocator_allocate(JNIEnv *env, jclass obj1, jclass clazz) {
    return (*env)->AllocObject(env, clazz);
}