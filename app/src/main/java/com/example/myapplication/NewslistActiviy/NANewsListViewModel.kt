package com.example.myapplication.NewslistActiviy

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Database.NANewDatabase
import com.example.myapplication.DatasAndValues.NAConstants.GOOD_NETWORK
import com.example.myapplication.DatasAndValues.NAConstants.NOMOREPAGES
import com.example.myapplication.DatasAndValues.NANewsListData
import com.example.myapplication.DatasAndValues.NANewsListDataItem
import com.example.myapplication.Interface.NACallbackInterface
import kotlinx.coroutines.Dispatchers

class NANewsListViewModel(application: Application) : AndroidViewModel(application) {
    var pageNumber = 1
    var listSizes: Int = 0
    var newsHeadLines: String = "economy"


    var newsListLivedata: MutableLiveData<NANewsListData> = MutableLiveData<NANewsListData>()
    var offLineLivedata: MutableLiveData<ArrayList<NANewsListDataItem?>> =
        MutableLiveData<ArrayList<NANewsListDataItem?>>()
    var searchNewsLiveData: MutableLiveData<ArrayList<NANewsListDataItem?>> =
        MutableLiveData<ArrayList<NANewsListDataItem?>>()
    var networkErrorLivedata: MutableLiveData<Int> = MutableLiveData<Int>()
    var isLoadingLiveData = MutableLiveData<Boolean?>()
    var oldList = ArrayList<NANewsListDataItem?>()
    var newList = ArrayList<NANewsListDataItem?>()
    var offlineList = ArrayList<NANewsListDataItem?>()
    var searchedList = ArrayList<NANewsListDataItem?>()
    var ChangeListOrder:String="Descending"
    var currentList: Boolean? = true
    val newsListDao = NANewDatabase.getDatabase(application).dbDao()
    private val NANewsListRepository: NANewsListRepository = NANewsListRepository(newsListDao)

    fun getNewsListFromServer(newsType: String) {
        isLoadingLiveData.value = true
        NANewsListRepository.fetchNewsList(object : NACallbackInterface.NewsListCallback {
            override fun getNewsListCallback(newsList: NANewsListData) {
                newsListLivedata.value = newsList
                networkErrorLivedata.value = GOOD_NETWORK
                isLoadingLiveData.value = false
            }

            override fun getPageResult(data: Int) {
                if (data == NOMOREPAGES) isLoadingLiveData.value = false
            }

            override fun getNetworkErrorCallback(data: Int) {
                networkErrorLivedata.value = data
                isLoadingLiveData.value = false
            }
        }, pageNumber, newsType)
    }

    fun getOfflineNews(newsHeadLines: String) {
        NANewsListRepository.getOfflinNews(object : NACallbackInterface.offlineNEwsCallback {
            override fun getOfflineNews(newsList: MutableList<NANewsListDataItem?>) {
                offLineLivedata.postValue(ArrayList(newsList))
            }
        }, newsHeadLines)
    }

    fun deletNewsListIndataBase() {
        NANewsListRepository.deleteNews()
    }

    fun getSearchedListFromDatabase(newText: String?) {
        NANewsListRepository.searchedNews(object : NACallbackInterface.searchedNewsCallback {
            override fun searchedNews(newsList: MutableList<NANewsListDataItem?>) {
                Log.d("Msg123",newsList.toString())
               searchNewsLiveData.postValue(ArrayList(newsList))
            }
        }, newsHeadLines, newText)
    }
    fun getBackToNewsList() {
        NANewsListRepository.getBackToNewsList(object : NACallbackInterface.searchedNewsCallback {
            override fun searchedNews(newsList: MutableList<NANewsListDataItem?>) {
                Log.d("Msg123",newsList.toString())
                searchNewsLiveData.postValue(ArrayList(newsList))
            }
        }, newsHeadLines)
    }
}