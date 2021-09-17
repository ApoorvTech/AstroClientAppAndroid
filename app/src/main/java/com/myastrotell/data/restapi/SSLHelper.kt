package com.myastrotell.data.restapi

import android.annotation.SuppressLint
import java.io.InputStream
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*


object SSLHelper {

    private var trustManagers: Array<TrustManager>? = null
//    var myTrustManager: MyTrustManager? = null


    // Create trust manager that validates certificate chains
    fun createCertifiedTrustManager(certificateIS: InputStream): Array<TrustManager> {
        if (trustManagers == null) {
            val cf = CertificateFactory.getInstance("X.509")
            val ca: Certificate?
            try {
                ca = cf.generateCertificate(certificateIS)
            } finally {
                certificateIS.close()
            }

            // creating a KeyStore containing our trusted CAs
            val keyStoreType: String = KeyStore.getDefaultType()
            val keyStore: KeyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            keyStore.setCertificateEntry("ca", ca)

            // creating a TrustManager that trusts the CAs in our KeyStore
            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)

            trustManagers = tmf.trustManagers

//            myTrustManager = MyTrustManager(keyStore)
//            trustManagers = arrayOf<TrustManager>(myTrustManager!!)

        }
        return trustManagers!!
    }


    // creating an SSLSocketFactory that uses our TrustManager
    fun getTrustedSslSocketFactory(trustManagers: Array<TrustManager>): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustManagers, null)
        return sslContext.socketFactory
    }


    // Create a trust manager that does not validate certificate chains
    fun getByPassTrustManager(): Array<TrustManager> {
        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(object :
            X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(
                chain: Array<X509Certificate?>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return emptyArray()
            }
        })

        return trustAllCerts
    }


    // Create an ssl socket factory with all-trusting manager
    fun getBypassSslSocketFactory(trustAllCerts: Array<TrustManager>): SSLSocketFactory {
        val sslContext: SSLContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        return sslContext.socketFactory
    }


}