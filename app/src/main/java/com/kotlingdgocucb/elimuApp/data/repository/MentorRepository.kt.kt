package com.kotlingdgocucb.elimuApp.data.repository

import androidx.lifecycle.LiveData
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.Mentor

interface MentorRepository {
    fun getMentors(): LiveData<List<Mentor>>
}
