package com.example.myapplication.Retrofit

import com.example.myapplication.DatasAndValues.NAConstants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NARetrofitHelper {
    var okHttpClient = OkHttpClient.Builder()
var loggin=HttpLoggingInterceptor()
    fun retrofitInstance(): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
    }

    fun newListApiClient(): NAApiClass {
        return retrofitInstance()
            .client(okHttpClient.addInterceptor(loggin).build())
            .build()
            .create(NAApiClass::class.java)
    }
}