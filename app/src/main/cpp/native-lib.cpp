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
    strcpy(result, env->GetStringUTFChars(target, 0));

    for (int index = 0; index < len; index++) {
        if (result[index] == 'a') {
            result[index] = '4';
        }
    }

    return env->NewStringUTF(result);
}
