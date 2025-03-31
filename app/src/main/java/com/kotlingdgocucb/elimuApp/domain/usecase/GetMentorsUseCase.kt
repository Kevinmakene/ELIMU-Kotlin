package com.kotlingdgocucb.elimuApp.domain.usecase

import androidx.lifecycle.LiveData
import com.kotlingdgocucb.elimuApp.data.repository.MentorRepository
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.Mentor

class GetMentorsUseCase(private val repository: MentorRepository) {
    operator fun invoke(): LiveData<List<Mentor>?> = repository.getMentors() as LiveData<List<Mentor>?>
}
