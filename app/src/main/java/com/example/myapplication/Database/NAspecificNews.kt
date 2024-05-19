package com.example.myapplication.Database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.DatasAndValues.NANewsListDataItem

class NAspecificNews{
    @Entity(tableName ="Economy")
    data class economy(
        @PrimaryKey(autoGenerate = true)
        val id:Int=1,
        @Embedded
        val economy:ArrayList<NANewsListDataItem>
    )
}
