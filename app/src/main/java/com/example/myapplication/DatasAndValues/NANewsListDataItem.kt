package com.example.myapplication.DatasAndValues

import androidx.annotation.Nullable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.myapplication.DatasAndValues.NAConstants.NEWSTYPE
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "NEWSLIST")
data class NANewsListDataItem(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val pageNo:Int,
    val newsType:String,
    @SerializedName("source")
    @Embedded
    @Nullable
    val source: Source?,

    @SerializedName("author")
    @Nullable
    val author: String?,

    @SerializedName("content")
    @Nullable
    val content: String?,

    @SerializedName("description")
    @Nullable
    val description: String?,

    @SerializedName("publishedAt")
    @Nullable
    val publishedAt: String?,

    @SerializedName("title")
    @Nullable
    val title: String?,
    @SerializedName("url")
    @Nullable
    val url: String?,
    @SerializedName("urlToImage")
    @Nullable
    val urlToImage: String?
)
