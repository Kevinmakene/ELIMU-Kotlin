package com.kotlingdgocucb.elimuApp.data.datasource.local.room.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kotlingdgocucb.elimuApp.domain.model.ProgressResponse


class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromProgressResponseList(value: List<ProgressResponse>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toProgressResponseList(value: String?): List<ProgressResponse>? {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<ProgressResponse>>() {}.type
        return gson.fromJson(value, listType)
    }

    // Si vous avez aussi besoin de convertir d'autres listes, par exemple une liste de ReviewResponse,
    // vous pouvez ajouter d'autres méthodes de conversion ici.
}
