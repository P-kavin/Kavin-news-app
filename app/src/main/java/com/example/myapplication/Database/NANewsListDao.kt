package com.example.myapplication.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.myapplication.DatasAndValues.NANewsListDataItem
import org.jetbrains.annotations.NotNull

@Dao
interface NANewsListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNewsList(data: NANewsListDataItem)

    @Query("DELETE FROM NEWSLIST")
    fun deleteAllNews()

    @Query("DELETE  FROM NEWSLIST WHERE newsType=:newsHeadLines")
    fun deleteSpecificNews(newsHeadLines: String)

    @Transaction
    @NotNull
    @Query("SELECT * FROM NEWSLIST WHERE newsType=:newsHeadLines")
    fun getNewsList(newsHeadLines: String): MutableList<NANewsListDataItem?>

    @Transaction
    @Query("SELECT * FROM NEWSLIST WHERE title LIKE '%' || :newText || '%' AND newsType=:newsHeadLines")
    fun searchForNews(newsHeadLines: String, newText: String?): MutableList<NANewsListDataItem?>

    @Transaction
    @Query("SELECT * FROM NEWSLIST WHERE newsType=:newsHeadLines ORDER BY pageNo Asc,title Asc")
    fun getBackToNewsList(newsHeadLines: String): MutableList<NANewsListDataItem?>
}