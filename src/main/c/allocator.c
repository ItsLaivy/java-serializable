#include <jni.h>
#include "codes_laivy_serializable_Allocator.h"

// Java Native Interface methods

JNIEXPORT jobject JNICALL Java_codes_laivy_serializable_Allocator_allocate(JNIEnv *env, const jclass obj1, const jclass clazz) {
    return (*env)->AllocObject(env, clazz);
}

JNIEXPORT void JNICALL Java_codes_laivy_serializable_Allocator_setFieldValue(JNIEnv *env, const jclass clazz, jobject field, jobject instance, jobject object) {
    if (field == NULL) {
        const jclass exception = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
        (*env)->ThrowNew(env, exception, "the field parameter cannot be null!");
    }

    const jclass fieldClass = (*env)->GetObjectClass(env, field);

    if (fieldClass == NULL) {
        const jclass exception = (*env)->FindClass(env, "java/lang/ClassNotFoundException");
        (*env)->ThrowNew(env, exception, "cannot find java.lang.reflect.Field class");
    } else {
        jfieldID fieldId = (*env)->FromReflectedField(env, field);

        if (fieldId == NULL) {
            const jclass exception = (*env)->FindClass(env, "java/lang/NoSuchFieldException");
            (*env)->ThrowNew(env, exception, "cannot find this field at the runtime!");
        } else {
            // Check if the newValue is assignable with the field type
            jobject fieldType = (*env)->CallObjectMethod(env, field, (*env)->GetMethodID(env, (*env)->GetObjectClass(env, field), "getType", "()Ljava/lang/Class;"));
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
                const jclass byteClass = (*env)->FindClass(env, "java/lang/Byte");
                const jclass shortClass = (*env)->FindClass(env, "java/lang/Short");

                // Check for specific field type
                jmethodID method = (*env)->GetStaticMethodID(env, (*env)->FindClass(env, "codes/laivy/serializable/Allocator"), "isAssignableFromIncludingPrimitive", "(Ljava/lang/Class;Ljava/lang/Class;)Z");

                const jboolean isLong = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, longClass);
                const jboolean isChar = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, charClass);
                const jboolean isInt = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, intClass);
                const jboolean isFloat = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, floatClass);
                const jboolean isDouble = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, doubleClass);
                const jboolean isBoolean = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, booleanClass);
                const jboolean isByte = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, byteClass);
                const jboolean isShort = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, shortClass);

                if (instance == NULL) { // Static field
                    if (object == NULL) {
                        (*env)->SetStaticObjectField(env, clazz, fieldId, NULL);
                    } else if (isLong) {
                        const jlong value = (*env)->CallLongMethod(env, object, (*env)->GetMethodID(env, longClass, "longValue", "()J"));
                        (*env)->SetStaticLongField(env, clazz, fieldId, value);
                    } else if (isChar) {
                        const jchar value = (*env)->CallCharMethod(env, object, (*env)->GetMethodID(env, charClass, "charValue", "()C"));
                        (*env)->SetStaticCharField(env, clazz, fieldId, value);
                    } else if (isInt) {
                        const jint value = (*env)->CallIntMethod(env, object, (*env)->GetMethodID(env, intClass, "intValue", "()I"));
                        (*env)->SetStaticIntField(env, clazz, fieldId, value);
                    } else if (isFloat) {
                        const jfloat value = (*env)->CallFloatMethod(env, object, (*env)->GetMethodID(env, floatClass, "floatValue", "()F"));
                        (*env)->SetStaticFloatField(env, clazz, fieldId, value);
                    } else if (isDouble) {
                        const jdouble value = (*env)->CallDoubleMethod(env, object, (*env)->GetMethodID(env, doubleClass, "doubleValue", "()D"));
                        (*env)->SetStaticDoubleField(env, clazz, fieldId, value);
                    } else if (isBoolean) {
                        const jboolean value = (*env)->CallBooleanMethod(env, object, (*env)->GetMethodID(env, booleanClass, "booleanValue", "()Z"));
                        (*env)->SetStaticBooleanField(env, clazz, fieldId, value);
                    } else if (isByte) {
                        const jbyte value = (*env)->CallByteMethod(env, object, (*env)->GetMethodID(env, byteClass, "byteValue", "()B"));
                        (*env)->SetStaticByteField(env, clazz, fieldId, value);
                    } else if (isShort) {
                        const jshort value = (*env)->CallShortMethod(env, object, (*env)->GetMethodID(env, shortClass, "shortValue", "()S"));
                        (*env)->SetStaticShortField(env, clazz, fieldId, value);
                    } else {
                        (*env)->SetStaticObjectField(env, clazz, fieldId, object);
                    }
                } else { // Non-static field
                    if (object == NULL) {
                        (*env)->SetObjectField(env, instance, fieldId, NULL);
                    } else if (isLong) {
                        const jlong value = (*env)->CallLongMethod(env, object, (*env)->GetMethodID(env, longClass, "longValue", "()J"));
                        (*env)->SetLongField(env, instance, fieldId, value);
                    } else if (isChar) {
                        const jchar value = (*env)->CallCharMethod(env, object, (*env)->GetMethodID(env, charClass, "charValue", "()C"));
                        (*env)->SetCharField(env, instance, fieldId, value);
                    } else if (isInt) {
                        const jint value = (*env)->CallIntMethod(env, object, (*env)->GetMethodID(env, intClass, "intValue", "()I"));
                        (*env)->SetIntField(env, instance, fieldId, value);
                    } else if (isFloat) {
                        const jfloat value = (*env)->CallFloatMethod(env, object, (*env)->GetMethodID(env, floatClass, "floatValue", "()F"));
                        (*env)->SetFloatField(env, instance, fieldId, value);
                    } else if (isDouble) {
                        const jdouble value = (*env)->CallDoubleMethod(env, object, (*env)->GetMethodID(env, doubleClass, "doubleValue", "()D"));
                        (*env)->SetDoubleField(env, instance, fieldId, value);
                    } else if (isBoolean) {
                        const jboolean value = (*env)->CallBooleanMethod(env, object, (*env)->GetMethodID(env, booleanClass, "booleanValue", "()Z"));
                        (*env)->SetBooleanField(env, instance, fieldId, value);
                    } else if (isByte) {
                        const jbyte value = (*env)->CallByteMethod(env, object, (*env)->GetMethodID(env, byteClass, "byteValue", "()B"));
                        (*env)->SetByteField(env, instance, fieldId, value);
                    } else if (isShort) {
                        const jshort value = (*env)->CallShortMethod(env, object, (*env)->GetMethodID(env, shortClass, "shortValue", "()S"));
                        (*env)->SetShortField(env, instance, fieldId, value);
                    } else {
                        (*env)->SetObjectField(env, instance, fieldId, object);
                    }
                }
            }
        }
    }
}

JNIEXPORT jobject JNICALL Java_codes_laivy_serializable_Allocator_getFieldValue(JNIEnv *env, const jclass clazz, jobject field, jobject instance) {
    if (field == NULL) {
        const jclass exception = (*env)->FindClass(env, "java/lang/IllegalArgumentException");
        (*env)->ThrowNew(env, exception, "the field parameter cannot be null!");
    }

    const jclass fieldClass = (*env)->GetObjectClass(env, field);

    if (fieldClass == NULL) {
        const jclass exception = (*env)->FindClass(env, "java/lang/ClassNotFoundException");
        (*env)->ThrowNew(env, exception, "cannot find java.lang.reflect.Field class");
        return NULL;
    } else {
        jfieldID fieldId = (*env)->FromReflectedField(env, field);

        if (fieldId == NULL) {
            const jclass exception = (*env)->FindClass(env, "java/lang/NoSuchFieldException");
            (*env)->ThrowNew(env, exception, "cannot find this field at the runtime!");
            return NULL;
        } else {
            // Check if the newValue is assignable with the field type
            jobject fieldType = (*env)->CallObjectMethod(env, field, (*env)->GetMethodID(env, (*env)->GetObjectClass(env, field), "getType", "()Ljava/lang/Class;"));

            // Handle type checks
            const jclass longClass = (*env)->FindClass(env, "java/lang/Long");
            const jclass charClass = (*env)->FindClass(env, "java/lang/Character");
            const jclass intClass = (*env)->FindClass(env, "java/lang/Integer");
            const jclass floatClass = (*env)->FindClass(env, "java/lang/Float");
            const jclass doubleClass = (*env)->FindClass(env, "java/lang/Double");
            const jclass booleanClass = (*env)->FindClass(env, "java/lang/Boolean");
            const jclass byteClass = (*env)->FindClass(env, "java/lang/Byte");
            const jclass shortClass = (*env)->FindClass(env, "java/lang/Short");

            // Check for specific field type
            jmethodID method = (*env)->GetStaticMethodID(env, (*env)->FindClass(env, "codes/laivy/serializable/Allocator"), "isAssignableFromIncludingPrimitive", "(Ljava/lang/Class;Ljava/lang/Class;)Z");

            const jboolean isLong = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, longClass);
            const jboolean isChar = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, charClass);
            const jboolean isInt = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, intClass);
            const jboolean isFloat = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, floatClass);
            const jboolean isDouble = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, doubleClass);
            const jboolean isBoolean = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, booleanClass);
            const jboolean isByte = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, byteClass);
            const jboolean isShort = (*env)->CallStaticBooleanMethod(env, fieldType, method, fieldType, shortClass);

            if (instance == NULL) { // Static field
                if (isLong) {
                    jlong value = (*env)->GetStaticLongField(env, clazz, fieldId);
                    return (*env)->NewObject(env, longClass, (*env)->GetMethodID(env, longClass, "<init>", "(J)V"), value);
                } else if (isChar) {
                    jchar value = (*env)->GetStaticCharField(env, clazz, fieldId);
                    return (*env)->NewObject(env, charClass, (*env)->GetMethodID(env, charClass, "<init>", "(C)V"), value);
                } else if (isInt) {
                    jint value = (*env)->GetStaticIntField(env, clazz, fieldId);
                    return (*env)->NewObject(env, intClass, (*env)->GetMethodID(env, intClass, "<init>", "(I)V"), value);
                } else if (isFloat) {
                    jfloat value = (*env)->GetStaticFloatField(env, clazz, fieldId);
                    return (*env)->NewObject(env, floatClass, (*env)->GetMethodID(env, floatClass, "<init>", "(F)V"), value);
                } else if (isDouble) {
                    jdouble value = (*env)->GetStaticDoubleField(env, clazz, fieldId);
                    return (*env)->NewObject(env, doubleClass, (*env)->GetMethodID(env, doubleClass, "<init>", "(D)V"), value);
                } else if (isBoolean) {
                    jboolean value = (*env)->GetStaticBooleanField(env, clazz, fieldId);
                    return (*env)->NewObject(env, booleanClass, (*env)->GetMethodID(env, booleanClass, "<init>", "(Z)V"), value);
                } else if (isByte) {
                    jbyte value = (*env)->GetStaticByteField(env, clazz, fieldId);
                    return (*env)->NewObject(env, byteClass, (*env)->GetMethodID(env, byteClass, "<init>", "(B)V"), value);
                } else if (isShort) {
                    jshort value = (*env)->GetStaticShortField(env, clazz, fieldId);
                    return (*env)->NewObject(env, shortClass, (*env)->GetMethodID(env, shortClass, "<init>", "(S)V"), value);
                } else {
                    return (*env)->GetStaticObjectField(env, clazz, fieldId);
                }
            } else { // Non-static field
                if (isLong) {
                    jlong value = (*env)->GetLongField(env, instance, fieldId);
                    return (*env)->NewObject(env, longClass, (*env)->GetMethodID(env, longClass, "<init>", "(J)V"), value);
                } else if (isChar) {
                    jchar value = (*env)->GetCharField(env, instance, fieldId);
                    return (*env)->NewObject(env, charClass, (*env)->GetMethodID(env, charClass, "<init>", "(C)V"), value);
                } else if (isInt) {
                    jint value = (*env)->GetIntField(env, instance, fieldId);
                    return (*env)->NewObject(env, intClass, (*env)->GetMethodID(env, intClass, "<init>", "(I)V"), value);
                } else if (isFloat) {
                    jfloat value = (*env)->GetFloatField(env, instance, fieldId);
                    return (*env)->NewObject(env, floatClass, (*env)->GetMethodID(env, floatClass, "<init>", "(F)V"), value);
                } else if (isDouble) {
                    jdouble value = (*env)->GetDoubleField(env, instance, fieldId);
                    return (*env)->NewObject(env, doubleClass, (*env)->GetMethodID(env, doubleClass, "<init>", "(D)V"), value);
                } else if (isBoolean) {
                    jboolean value = (*env)->GetBooleanField(env, instance, fieldId);
                    return (*env)->NewObject(env, booleanClass, (*env)->GetMethodID(env, booleanClass, "<init>", "(Z)V"), value);
                } else if (isByte) {
                    jbyte value = (*env)->GetByteField(env, instance, fieldId);
                    return (*env)->NewObject(env, byteClass, (*env)->GetMethodID(env, byteClass, "<init>", "(B)V"), value);
                } else if (isShort) {
                    jshort value = (*env)->GetShortField(env, instance, fieldId);
                    return (*env)->NewObject(env, shortClass, (*env)->GetMethodID(env, shortClass, "<init>", "(S)V"), value);
                } else {
                    return (*env)->GetObjectField(env, instance, fieldId);
                }
            }
        }
    }
}