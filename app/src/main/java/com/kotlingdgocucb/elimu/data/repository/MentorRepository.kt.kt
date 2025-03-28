package com.kotlingdgocucb.elimu.data.repository

import androidx.lifecycle.LiveData
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Mentor

interface MentorRepository {
    fun getMentors(): LiveData<List<Mentor>>
}
