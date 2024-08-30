#ifndef FIELDS_H
#define FIELDS_H

#pragma once
#include <jni.h>

jboolean isFieldAccessible(JNIEnv *env, jobject field);
void setFieldAccessible(JNIEnv *env, jobject field, jboolean accessible);

#endif //FIELDS_H
