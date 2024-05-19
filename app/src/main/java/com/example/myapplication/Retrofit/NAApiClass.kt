package com.example.myapplication.Retrofit

import com.example.myapplication.DatasAndValues.NAConstants.API_KEY
import com.example.myapplication.DatasAndValues.NANewsListDataItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NAApiClass {
    @GET("v2/everything ")
    @Headers("Accept:*/*")
    fun getNewsList(
        @Query("q")
        q:String="economy",
        @Query("page")
        pageNumber: Int,
        @Query("apikey")
        apikey:String= API_KEY
    ): Call<ResponseBody>

}