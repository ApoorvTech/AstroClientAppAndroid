//
// Created by Navjot Singh
// on 21/09/20.
//

#include <jni.h>
#include <string>


extern "C" JNIEXPORT jstring JNICALL
Java_com_myastrotell_utils_encryption_Encryption_getPrivateKeyEncryptionAlgo(JNIEnv *env, jobject obj) {
    return env->NewStringUTF("AES");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_myastrotell_utils_encryption_Encryption_getKeyEncryptionAlgo(JNIEnv *env, jobject obj) {
    return env->NewStringUTF("MD5");
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_myastrotell_utils_encryption_Encryption_getPrivateKey(JNIEnv *env, jobject obj) {
    return env->NewStringUTF("LOYALTYPLATF0%MOne97@LOyalty%#!m"); // for development
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_myastrotell_utils_encryption_Encryption_getInitVectorKey(JNIEnv *env, jobject obj) {
    return env->NewStringUTF("BSonK88FyMYQGSAi"); // for development
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_myastrotell_utils_encryption_Encryption_getCipherTransformation(JNIEnv *env,jobject obj) {
    return env->NewStringUTF("AES/ECB/PKCS5Padding"); // for development
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_myastrotell_utils_encryption_Encryption_getTrackierId(JNIEnv *env,jobject obj) {
    return env->NewStringUTF("d2b7fa85-a59f-46d8-b5db-0adf351917c6"); // for development
}
