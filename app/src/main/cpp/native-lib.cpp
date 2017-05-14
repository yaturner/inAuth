#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_das_inauth_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */,
        jstring target) {

    size_t len = env->GetStringLength(target);
    char *result = (char *) malloc(len + 1);
    const char *temp = env->GetStringUTFChars(target, 0);
    strcpy(result, temp);

    for (int index = 0; index < len; index++) {
        if (result[index] == 'a') {
            result[index] = '4';
        }
    }
    env->ReleaseStringUTFChars(target, temp);
    return env->NewStringUTF(result);
}
