package com.example.myapplication.DatasAndValues

import androidx.annotation.Nullable
import androidx.room.Embedded

data class Source(
    @Embedded
    @Nullable
    val id: Any?,
    @Nullable
    val name: String?
)