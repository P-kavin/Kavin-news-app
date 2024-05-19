package com.example.myapplication.Interface

import com.example.myapplication.DatasAndValues.NANewsListData
import com.example.myapplication.DatasAndValues.NANewsListDataItem

class NACallbackInterface {
    interface NewsListCallback{
       fun getNewsListCallback(newsList:NANewsListData)
       fun getPageResult(data:Int)
       fun getNetworkErrorCallback(data:Int)
    }
    interface offlineNEwsCallback{
        fun getOfflineNews(newsList: MutableList<NANewsListDataItem?>)
    }
    interface searchedNewsCallback{
        fun searchedNews(newsList: MutableList<NANewsListDataItem?>)
    }
    interface ascendingOrderCallback{
        fun ascendingOrder(newsList: MutableList<NANewsListDataItem?>)
    }
}