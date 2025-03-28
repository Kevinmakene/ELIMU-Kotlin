package com.kotlingdgocucb.elimu.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Mentor
import com.kotlingdgocucb.elimu.domain.usecase.GetMentorsUseCase


class
MentorViewModel(private val getMentorsUseCase: GetMentorsUseCase) : ViewModel() {
    // Les mentors sont directement obtenus via le UseCase
    val mentors: LiveData<List<Mentor>?> = getMentorsUseCase()

}



