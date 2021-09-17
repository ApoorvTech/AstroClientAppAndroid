package com.myastrotell.data.restapi

import com.google.gson.Gson
import com.myastrotell.BuildConfig
import com.myastrotell.data.BuildInstance
import com.myastrotell.data.DataManager
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.utils.encryption.Encryption
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


object ApiManager {

    private const val CONNECTION_TIMEOUT: Long = 30L


    val retrofitService by lazy {
        retrofit.create(ApiInterface::class.java)
    }

    val paymentRetrofitService by lazy {
        ccRetrofit.create(ApiInterface::class.java)
    }

    val notificationRetrofitService by lazy {
        notificationRetrofit.create(ApiInterface::class.java)
    }

    val chatMachineRetrofitService by lazy {
        chatMachineRetrofit.create(ApiInterface::class.java)
    }

    private val chatMachineRetrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.CHAT_MACHINE_BASE_URL)
        .client(getOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val notificationRetrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.NOTIFICATION_BASE_URL)
        .client(getOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val ccRetrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.PAYMENT_API_BASE_URL)
        .client(getOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(getOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
//        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()


    private fun getOkHttpClient(): OkHttpClient {
        val okClientBuilder = OkHttpClient.Builder()

        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        okClientBuilder.addInterceptor(getInterceptor())
        okClientBuilder.addInterceptor(httpLoggingInterceptor)

        okClientBuilder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        okClientBuilder.readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        okClientBuilder.writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        okClientBuilder.callTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)

        // applying SSL
//        when (BuildConfig.BUILD) {
//            BuildInstance.LIVESERVER -> {
//                val certs = SSLHelper.createCertifiedTrustManager(
//                    BaseApplication.instance.resources.openRawResource(R.raw.astrotell_com)
//                )
//
//                okClientBuilder.sslSocketFactory(
//                    SSLHelper.getTrustedSslSocketFactory(certs),
//                    certs[0] as X509TrustManager
//                )
//
//                okClientBuilder.hostnameVerifier { hostName, session ->
//                    hostName.equals(BuildConfig.HOST_NAME)
//                }
//            }
//
//            else -> {
//                val certs = SSLHelper.getByPassTrustManager()
//
//                okClientBuilder.sslSocketFactory(
//                    SSLHelper.getBypassSslSocketFactory(certs),
//                    certs[0] as X509TrustManager
//                )
//
//                okClientBuilder.hostnameVerifier { hostName, session -> true }
//            }
//        }

        return okClientBuilder.build()
    }


    private fun getInterceptor(): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader(
                        "accessToken",
                        DataManager.getStringFromPreference(PreferenceManager.ACCESS_TOKEN)
                    )
                    .addHeader(
                        "deviceId", DataManager.getStringFromPreference(PreferenceManager.DEVICE_ID)
                    )
                    .addHeader("msisdn", DataManager.getMsisdn())
                    .addHeader("channel", BuildConfig.CHANNEL)
                    .addHeader("channelName", BuildConfig.CHANNEL_NAME)
                    .addHeader("appName", BuildConfig.APP_NAME)
                    .addHeader("deviceType", BuildConfig.DEVICE_TYPE)
                    .addHeader("campaignId", BuildConfig.CAMPAIGN_ID)
                    .addHeader("clientId", BuildConfig.CLIENT_ID)
                    .addHeader("appLanguage", "en")

                if (!(BuildConfig.BUILD == BuildInstance.STAGING || BuildConfig.BUILD == BuildInstance.LIVESERVER)) {
                    requestBuilder.addHeader("X-FORWARDED-FOR", "127.0.0.1")
                }

                val headers = requestBuilder.build()

                return chain.proceed(headers)
            }
        }
    }


    fun createCheckSum(`object`: Any): String? {
        val gson = Gson()
        val json = gson.toJson(`object`)
        try {
            val jsonObject = JSONObject(json)
            var paramString = ""
            val iterator: Iterator<*> = jsonObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next() as String
                var value = "" + jsonObject[key]
                if (jsonObject[key] is String) {
                    value = Encryption.encrypt(jsonObject[key] as String)!!
                }
                if (!value.isEmpty()) {
                    paramString = if (paramString.isEmpty()) {
                        "$key:$value"
                    } else {
                        "$paramString#$key:$value"
                    }
                }
            }
            val date = Date()
            paramString = paramString + "#timeStamp:" + date.time
            return Encryption.encrypt(paramString)
        } catch (e: JSONException) {
        }
        return null
    }

}
