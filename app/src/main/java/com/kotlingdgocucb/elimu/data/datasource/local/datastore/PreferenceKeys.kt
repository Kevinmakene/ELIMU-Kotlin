package com.kotlingdgocucb.elimu.data.datasource.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {

    val userEmail = stringPreferencesKey("user_email")
    val userName = stringPreferencesKey("user_name")
    val isLoggedIn = booleanPreferencesKey("Is_logged_in")
    val userProfilePicture = stringPreferencesKey("user_profile_image")
    val userTrack = stringPreferencesKey("user_track")
    val userMentor = stringPreferencesKey("user_mentor")
}
