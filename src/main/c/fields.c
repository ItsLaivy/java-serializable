#include "fields.h"

jboolean isFieldAccessible(JNIEnv *env, const jobject field) {
    // Find AccessibleObject class
    const jclass accessibleObjectClass = (*env)->FindClass(env, "java/lang/reflect/AccessibleObject");
    if (accessibleObjectClass == NULL) {
        const jclass exception = (*env)->FindClass(env, "java/lang/ClassNotFoundException");
        (*env)->ThrowNew(env, exception, "cannot find java.lang.reflect.AccessibleObject class");
    }

    // Find #isAccessible method from AccessibleObject class
    const jmethodID isAccessibleMethod = (*env)->GetMethodID(env, accessibleObjectClass, "isAccessible", "()Z");
    if (isAccessibleMethod == NULL) {
        const jclass exception = (*env)->FindClass(env, "java/lang/NoSuchMethodException");
        (*env)->ThrowNew(env, exception, "cannot find java.lang.reflect.AccessibleObject#isAccessible method");
    }

    // Return accessibility from field
    const jboolean accessible = (*env)->CallBooleanMethod(env, field, isAccessibleMethod);
    return accessible;
}

void setFieldAccessible(JNIEnv *env, const jobject field, const jboolean accessible) {
    // Find AccessibleObject class
    const jclass accessibleObjectClass = (*env)->FindClass(env, "java/lang/reflect/AccessibleObject");
    if (accessibleObjectClass == NULL) {
        const jclass exception = (*env)->FindClass(env, "java/lang/ClassNotFoundException");
        (*env)->ThrowNew(env, exception, "cannot find java.lang.reflect.AccessibleObject class");
    }

    // Find #setAccessible method from AccessibleObject class
    const jmethodID isAccessibleMethod = (*env)->GetMethodID(env, accessibleObjectClass, "setAccessible", "(Z)V");
    if (isAccessibleMethod == NULL) {
        const jclass exception = (*env)->FindClass(env, "java/lang/NoSuchMethodException");
        (*env)->ThrowNew(env, exception, "cannot find java.lang.reflect.AccessibleObject#setAccessible(boolean) method");
    }

    (*env)->CallBooleanMethod(env, field, isAccessibleMethod, accessible);
}