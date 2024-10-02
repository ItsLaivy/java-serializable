#include <jni.h>
#include "codes_laivy_serializable_Allocator.h"

// Java Native Interface methods

JNIEXPORT jobject JNICALL Java_codes_laivy_serializable_Allocator_allocate(JNIEnv *env, const jclass obj1, const jclass clazz) {
    return (*env)->AllocObject(env, clazz);
}

JNIEXPORT void JNICALL Java_codes_laivy_serializable_Allocator_setFieldValue(JNIEnv *env, const jclass clazz, const jobject field, const jobject instance, const jobject object) {
    const jclass fieldClass = (*env)->GetObjectClass(env, field);

    if (fieldClass == NULL) {
        const jclass exception = (*env)->FindClass(env, "java/lang/ClassNotFoundException");
        (*env)->ThrowNew(env, exception, "cannot find java.lang.reflect.Field class");
    } else {
        const jfieldID fieldId = (*env)->FromReflectedField(env, field);

        if (field == NULL) {
            const jclass exception = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
            (*env)->ThrowNew(env, exception, "the field parameter cannot be null!");
        } else if (fieldId == NULL) {
            const jclass exception = (*env)->FindClass(env, "java/lang/NoSuchFieldException");
            (*env)->ThrowNew(env, exception, "cannot find this field at the runtime!");
        } else {
            // Check if the newValue is assignable with the field type
            const jobject fieldType = (*env)->CallObjectMethod(env, field, (*env)->GetMethodID(env, (*env)->GetObjectClass(env, field), "getType", "()Ljava/lang/Class;"));
            jboolean isAssignable = JNI_TRUE;

            if (object != NULL) {
                const jclass objectClass = (*env)->GetObjectClass(env, object);
                isAssignable = objectClass == NULL ? JNI_TRUE : (*env)->CallStaticBooleanMethod(env, fieldType, (*env)->GetStaticMethodID(env, (*env)->FindClass(env, "codes/laivy/serializable/Allocator"), "isAssignableFromIncludingPrimitive", "(Ljava/lang/Class;Ljava/lang/Class;)Z"), fieldType, objectClass);
            }

            // Finish
            if (!isAssignable) {
                const jclass classCastException = (*env)->FindClass(env, "java/lang/ClassCastException");
                (*env)->ThrowNew(env, classCastException, "cannot assign object of this type to the field!");
            } else {
                // Handle type checks
                const jclass longClass = (*env)->FindClass(env, "java/lang/Long");
                const jclass charClass = (*env)->FindClass(env, "java/lang/Character");
                const jclass intClass = (*env)->FindClass(env, "java/lang/Integer");
                const jclass floatClass = (*env)->FindClass(env, "java/lang/Float");
                const jclass doubleClass = (*env)->FindClass(env, "java/lang/Double");
                const jclass booleanClass = (*env)->FindClass(env, "java/lang/Boolean");

                // Check for specific field type
                jboolean isLong = (*env)->CallStaticBooleanMethod(env, fieldType, (*env)->GetStaticMethodID(env, (*env)->FindClass(env, "codes/laivy/serializable/Allocator"), "isAssignableFromIncludingPrimitive", "(Ljava/lang/Class;Ljava/lang/Class;)Z"), fieldType, longClass);
                jboolean isChar = (*env)->IsInstanceOf(env, fieldType, charClass);
                jboolean isInt = (*env)->IsInstanceOf(env, fieldType, intClass);
                jboolean isFloat = (*env)->IsInstanceOf(env, fieldType, floatClass);
                jboolean isDouble = (*env)->IsInstanceOf(env, fieldType, doubleClass);
                jboolean isBoolean = (*env)->IsInstanceOf(env, fieldType, booleanClass);

                if (instance == NULL) { // Static field
                    if (isLong) {
                        jlong value = (*env)->CallLongMethod(env, object, (*env)->GetMethodID(env, longClass, "longValue", "()J"));
                        (*env)->SetStaticLongField(env, clazz, fieldId, value);
                    } else if (isChar) {
                        jchar value = (*env)->CallCharMethod(env, object, (*env)->GetMethodID(env, charClass, "charValue", "()C"));
                        (*env)->SetStaticCharField(env, clazz, fieldId, value);
                    } else if (isInt) {
                        jint value = (*env)->CallIntMethod(env, object, (*env)->GetMethodID(env, intClass, "intValue", "()I"));
                        (*env)->SetStaticIntField(env, clazz, fieldId, value);
                    } else if (isFloat) {
                        jfloat value = (*env)->CallFloatMethod(env, object, (*env)->GetMethodID(env, floatClass, "floatValue", "()F"));
                        (*env)->SetStaticFloatField(env, clazz, fieldId, value);
                    } else if (isDouble) {
                        jdouble value = (*env)->CallDoubleMethod(env, object, (*env)->GetMethodID(env, doubleClass, "doubleValue", "()D"));
                        (*env)->SetStaticDoubleField(env, clazz, fieldId, value);
                    } else if (isBoolean) {
                        jboolean value = (*env)->CallBooleanMethod(env, object, (*env)->GetMethodID(env, booleanClass, "booleanValue", "()Z"));
                        (*env)->SetStaticBooleanField(env, clazz, fieldId, value);
                    } else {
                        (*env)->SetStaticObjectField(env, clazz, fieldId, object);
                    }
                } else { // Non-static field
                    if (isLong) {
                        jlong value = (*env)->CallLongMethod(env, object, (*env)->GetMethodID(env, longClass, "longValue", "()J"));
                        (*env)->SetLongField(env, instance, fieldId, value);
                    } else if (isChar) {
                        jchar value = (*env)->CallCharMethod(env, object, (*env)->GetMethodID(env, charClass, "charValue", "()C"));
                        (*env)->SetCharField(env, instance, fieldId, value);
                    } else if (isInt) {
                        jint value = (*env)->CallIntMethod(env, object, (*env)->GetMethodID(env, intClass, "intValue", "()I"));
                        (*env)->SetIntField(env, instance, fieldId, value);
                    } else if (isFloat) {
                        jfloat value = (*env)->CallFloatMethod(env, object, (*env)->GetMethodID(env, floatClass, "floatValue", "()F"));
                        (*env)->SetFloatField(env, instance, fieldId, value);
                    } else if (isDouble) {
                        jdouble value = (*env)->CallDoubleMethod(env, object, (*env)->GetMethodID(env, doubleClass, "doubleValue", "()D"));
                        (*env)->SetDoubleField(env, instance, fieldId, value);
                    } else if (isBoolean) {
                        jboolean value = (*env)->CallBooleanMethod(env, object, (*env)->GetMethodID(env, booleanClass, "booleanValue", "()Z"));
                        (*env)->SetBooleanField(env, instance, fieldId, value);
                    } else {
                        (*env)->SetObjectField(env, instance, fieldId, object);
                    }
                }
            }
        }
    }
}