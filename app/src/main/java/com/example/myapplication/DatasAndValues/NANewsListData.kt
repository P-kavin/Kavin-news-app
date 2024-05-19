package com.example.myapplication.DatasAndValues

import com.google.gson.annotations.SerializedName


data class NANewsListData(
//    val status: String,
//    val totalResults: Int,
    @SerializedName("articles")
    val articles: List<NANewsListDataItem>
)