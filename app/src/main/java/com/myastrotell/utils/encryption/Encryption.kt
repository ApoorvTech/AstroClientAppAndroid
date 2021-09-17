package com.myastrotell.utils.encryption

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


object Encryption {

    init {
        System.loadLibrary("native-lib")
    }


    private external fun getKeyEncryptionAlgo(): String
    private external fun getPrivateKeyEncryptionAlgo(): String
    private external fun getPrivateKey(): String
    private external fun getInitVectorKey(): String
    private external fun getCipherTransformation(): String
    private external fun getTrackierId(): String

    private val keyEncryptionAlgo = getKeyEncryptionAlgo()
    private val privateKeyEncryptionAlgo = getPrivateKeyEncryptionAlgo()
    private val privateKey = getPrivateKey()
    private val initVector = getInitVectorKey()
    private val transformation = getCipherTransformation()
    private val trackierId = getTrackierId()

    private var isEncrypt: Boolean = true


    fun getTrackierSDKId():String{
        return trackierId
    }

    private fun getKeySpec(): SecretKeySpec? {
        try {
            var keyArr = privateKey.toByteArray(charset("UTF-8"))

            // MD5
            val sha = MessageDigest.getInstance(keyEncryptionAlgo)
            keyArr = sha.digest(keyArr)

            keyArr = Arrays.copyOf(keyArr, 16)

            return SecretKeySpec(keyArr, privateKeyEncryptionAlgo)

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }


    fun encrypt(strToEncrypt: String): String? {
        try {
            val keySpec = getKeySpec()

            val cipher = Cipher.getInstance(transformation)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec)

            return Base64.encodeToString(
                cipher.doFinal(strToEncrypt.toByteArray(charset("UTF-8"))),
                Base64.NO_WRAP
            )

        } catch (e: Exception) {
            println("Error while encrypting: $e")
        }
        return null
    }


    fun rsaEncrypt(plainText:String,key: String):String?{
        try {
            val publicKey: PublicKey = KeyFactory.getInstance("RSA").generatePublic(
                X509EncodedKeySpec(
                    Base64.decode(
                        key,
                        Base64.DEFAULT
                    )
                )
            )
            val cipher =
                Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            return Base64.encodeToString(
                cipher.doFinal(plainText.toByteArray(charset("UTF-8"))),
                Base64.DEFAULT
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return null
    }


    fun decrypt(strToDecrypt: String): String? {
        try {
            val keySpec = getKeySpec()

            val cipher = Cipher.getInstance(transformation)
            cipher.init(Cipher.DECRYPT_MODE, keySpec)

            return String(cipher.doFinal(Base64.decode(strToDecrypt.toByteArray(), Base64.NO_WRAP)))

        } catch (e: java.lang.Exception) {
            println("Error while decrypting: $e")
        }
        return null
    }


//    fun encrypt(value: String): String {
//        val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
//        val mainKey: String = MD5(privateKey)
//
//        checkMainKeyNull(mainKey)
//
//        val skeySpec = SecretKeySpec(mainKey.toByteArray(charset("UTF-8")), "AES")
//
//        val cipher: Cipher = Cipher.getInstance(transformation)
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec /*secretKey*/, iv)
//
//        val encrypted: ByteArray = cipher.doFinal(value.toByteArray())
//        return Base64.encodeToString(encrypted, Base64.NO_WRAP /*| Base64.URL_SAFE*/)
//    }
//
//
//    fun decrypt(encrypted: String?): String? {
//        val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
//        val mainKey = MD5(privateKey)
//        checkMainKeyNull(mainKey)
//        val skeySpec = SecretKeySpec(mainKey.toByteArray(charset("UTF-8")), "AES")
//        val cipher = Cipher.getInstance(transformation)
//        cipher.init(Cipher.DECRYPT_MODE,  /*secretKey*/skeySpec, iv)
//        val original =
//            cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP /*| Base64.URL_SAFE*/))
//        return String(original)
//    }
//
//    fun encryptValue(value: String): String? {
//        if (isEncrypt) {
//            try {
//                val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
//                val mainKey = MD5(privateKey)
//                checkMainKeyNull(mainKey)
//                val skeySpec = SecretKeySpec(mainKey.toByteArray(charset("UTF-8")), "AES")
//                val cipher = Cipher.getInstance(transformation)
//                cipher.init(Cipher.ENCRYPT_MODE, skeySpec /*secretKey*/, iv)
//                val encrypted = cipher.doFinal(value.toByteArray())
//                return Base64.encodeToString(encrypted, Base64.NO_WRAP /*| Base64.URL_SAFE*/)
//            } catch (ex: Exception) {
//            }
//        }
//        return value
//    }
//
//    fun decryptValue(encrypted: String?): String? {
//        if (isEncrypt) {
//            try {
//                val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
//                val mainKey = MD5(privateKey)
//                checkMainKeyNull(mainKey)
//                val skeySpec = SecretKeySpec(mainKey.toByteArray(charset("UTF-8")), "AES")
//                val cipher = Cipher.getInstance(transformation)
//                cipher.init(Cipher.DECRYPT_MODE,  /*secretKey*/skeySpec, iv)
//                val original =
//                    cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP /*| Base64.URL_SAFE*/))
//                return String(original)
//            } catch (ex: Exception) {
//            }
//        }
//        return encrypted
//    }
//
//    fun decryptStatusCode(encryptedCode: Int, context: Context?): Int {
//        return encryptedCode
//    }
//
//    private fun MD5(md5: String): String {
//        val md = MessageDigest.getInstance("MD5")
//        val array = md.digest(md5.toByteArray(charset("UTF-8")))
//        val sb = StringBuffer()
//        for (i in array.indices) {
//            sb.append(Integer.toHexString(array[i].toInt() and 0xFF or 0x100).substring(1, 3))
//        }
//        return sb.toString()
//    }
//
//    private fun checkMainKeyNull(mainKey: String?) {
//        if (mainKey == null) throw NullPointerException()
//    }

}
