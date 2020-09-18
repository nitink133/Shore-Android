package com.theshoremedia.retrofit

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    companion object{
        private const val HTTP_CONNECT_TIMEOUT: Long = 60000
        private const val HTTP_READ_TIMEOUT = HTTP_CONNECT_TIMEOUT
        private const val HTTP_WRITE_TIMEOUT = HTTP_CONNECT_TIMEOUT
        private var instance: ApiClient? = null
        private var sService: ApiInterface? = null
        private var sAuthToken = ""
        private const val BASE_URL = "https://theshoremedia.herokuapp.com"
        fun getInstance(): ApiClient? {
            if (instance == null) {
                instance = ApiClient()
                initRetrofit()
            }
            return instance
        }

        /*
        Method return service reference
         */
        val apiService2: ApiInterface?
            get() {
                if (sService == null) {
                    if (instance == null) {
                        instance = ApiClient()
                    }
                    initRetrofit()
                }
                return sService
            }

        private fun initRetrofit() {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.apply {
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .connectTimeout(
                    HTTP_CONNECT_TIMEOUT,
                    TimeUnit.MILLISECONDS
                )
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(
                    Interceptor { chain: Interceptor.Chain ->
                        val builder = chain.request().newBuilder()
                        builder.addHeader("AuthToken", sAuthToken)
                        chain.proceed(builder.build())
                    }
                ).addInterceptor(loggingInterceptor).build()
            val sRetrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .baseUrl(BASE_URL)
                .client(client)
                .build()
            sService = sRetrofit.create(ApiInterface::class.java)
        }


    }
}