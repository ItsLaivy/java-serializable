/* DO NOT EDIT THIS FILE - it is machine generated */
#pragma once
#include <jni.h>
/* Header for class codes_laivy_serializable_Allocator */

#ifndef _Included_codes_laivy_serializable_Allocator
#define _Included_codes_laivy_serializable_Allocator
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     codes_laivy_serializable_Allocator
 * Method:    allocate
 * Signature: (Ljava/lang/Class;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_codes_laivy_serializable_Allocator_allocate
  (JNIEnv *, jclass, jclass);

/*
 * Class:     codes_laivy_serializable_Allocator
 * Method:    setFieldValue
 * Signature: (Ljava/lang/reflect/Field;Ljava/lang/Object;Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_codes_laivy_serializable_Allocator_setFieldValue
  (JNIEnv *, jclass, jobject, jobject, jobject);

/*
 * Class:     codes_laivy_serializable_Allocator
 * Method:    getFieldValue
 * Signature: (Ljava/lang/reflect/Field;Ljava/lang/Object;)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_codes_laivy_serializable_Allocator_getFieldValue
  (JNIEnv *, jclass, jobject, jobject);

#ifdef __cplusplus
}
#endif
#endif
