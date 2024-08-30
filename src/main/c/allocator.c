#include <jni.h>

#include "fields.h"
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
            const jobject fieldType = (*env)->CallObjectMethod(
                env, field, (*env)->GetMethodID(env, (*env)->GetObjectClass(env, field), "getType",
                                                "()Ljava/lang/Class;"));
            jboolean isAssignable = JNI_TRUE;

            if (object != NULL) {
                const jclass objectClass = (*env)->GetObjectClass(env, object);
                isAssignable = objectClass == NULL ? JNI_TRUE : (*env)->CallBooleanMethod(env, fieldType, (*env)->GetMethodID(env, (*env)->FindClass(env, "java/lang/Class"), "isAssignableFrom", "(Ljava/lang/Class;)Z"), objectClass);
            }

            // Finish
            if (!isAssignable) {
                jclass classCastException = (*env)->FindClass(env, "java/lang/ClassCastException");
                (*env)->ThrowNew(env, classCastException, "cannot assign object of this type to the field!");
            } else if (instance == NULL) {
                // Static field:
                (*env)->SetStaticObjectField(env, clazz, fieldId, object);
            } else {
                // Non-static field:
                (*env)->SetObjectField(env, instance, fieldId, object);
            }
        }
    }
}
