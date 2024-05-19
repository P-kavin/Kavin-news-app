package com.example.myapplication.NewslistActiviy

import android.os.AsyncTask
import com.example.myapplication.Database.NANewsListDao
import com.example.myapplication.DatasAndValues.NAConstants.BAD_NETWORK
import com.example.myapplication.DatasAndValues.NAConstants.CODE_OK
import com.example.myapplication.DatasAndValues.NAConstants.NOMOREPAGES
import com.example.myapplication.DatasAndValues.NANewsListData
import com.example.myapplication.DatasAndValues.NANewsListDataItem
import com.example.myapplication.Interface.NACallbackInterface
import com.example.myapplication.Retrofit.NARetrofitHelper
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NANewsListRepository(private val newsListDao: NANewsListDao) {
    fun fetchNewsList(
        callback: NACallbackInterface.NewsListCallback,
        pageNumber: Int,
        newsType: String
    ) {
        NARetrofitHelper.newListApiClient().getNewsList(newsType, pageNumber)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful && response.code() == CODE_OK) {
                        val foodListResponse =
                            Gson().fromJson(response.body()?.string(), NANewsListData::class.java)
                        callback.getNewsListCallback(foodListResponse)
                        saveToDB(foodListResponse, pageNumber, newsType)
                    } else if (response.code() == NOMOREPAGES) {
                        callback.getPageResult(response.code())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    callback.getNetworkErrorCallback(BAD_NETWORK)
                }

            })
    }

    private fun saveToDB(foodListResponse: NANewsListData?, pageNumber: Int, newsType: String) {
        if (pageNumber == 1) {
            Thread { newsListDao.deleteSpecificNews(newsType) }.start()
        }
        for (position in 0 until foodListResponse!!.articles.size) {
            foodListResponse.articles[position].let {
                AsyncTask.execute {
                    newsListDao?.insertNewsList(
                        NANewsListDataItem(
                            0,
                            pageNumber,
                            newsType,
                            it.source,
                            it.author,
                            it.content,
                            it.description,
                            it.publishedAt,
                            it.title,
                            it.url,
                            it.urlToImage
                        )
                    )
                }
            }
        }
    }

    fun deleteNews() {
        Thread { newsListDao.deleteAllNews() }.start()
    }

    fun getOfflinNews(param: NACallbackInterface.offlineNEwsCallback, newsHeadLines: String) {
        Thread { param.getOfflineNews(newsListDao.getNewsList(newsHeadLines)) }.start()
    }

    fun searchedNews(
        searchedNewsCallback: NACallbackInterface.searchedNewsCallback,
        newsHeadLines: String,
        newText: String?
    ) {
        Thread {
            searchedNewsCallback.searchedNews(newsListDao.searchForNews(newsHeadLines, newText))
        }.start()
    }

    fun getBackToNewsList(
        searchedNewsCallback: NACallbackInterface.searchedNewsCallback,
        newsHeadLines: String
    ) {
        Thread {
            searchedNewsCallback.searchedNews(newsListDao.getBackToNewsList(newsHeadLines))
        }.start()
    }

}