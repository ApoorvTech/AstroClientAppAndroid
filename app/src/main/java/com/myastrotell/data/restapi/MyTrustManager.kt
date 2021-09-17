package com.myastrotell.data.restapi

import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class MyTrustManager(localKeyStore: KeyStore) : X509TrustManager {
    private lateinit var defaultTrustManager: X509TrustManager
    private lateinit var localTrustManager: X509TrustManager

    init {
        try {
            defaultTrustManager = createTrustManager(null);
            localTrustManager = createTrustManager(localKeyStore);
        } catch (e: NoSuchAlgorithmException) {

        } catch (e: KeyStoreException) {

        }
    }

    @Throws(NoSuchAlgorithmException::class, KeyStoreException::class)
    private fun createTrustManager(store: KeyStore?): X509TrustManager {
        val tmfAlgorithm: String = TrustManagerFactory.getDefaultAlgorithm()
        val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(store)
        val trustManagers: Array<TrustManager> = tmf.trustManagers
        return trustManagers[0] as X509TrustManager
    }

    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        try {
            defaultTrustManager.checkClientTrusted(chain, authType)
        } catch (e: CertificateException) {
            localTrustManager.checkClientTrusted(chain, authType)
        }
    }

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
        try {
            defaultTrustManager.checkServerTrusted(chain, authType)
        } catch (e: CertificateException) {
            localTrustManager.checkServerTrusted(chain, authType)
        }
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        val first =
            defaultTrustManager.acceptedIssuers
        val second =
            localTrustManager.acceptedIssuers
        val result: Array<X509Certificate> =
            Arrays.copyOf(first, first.size + second.size)
        System.arraycopy(second, 0, result, first.size, second.size)
        return result
    }
}