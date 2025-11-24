package com.example.androidpractice.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.androidpractice.data.local.ProfileData

val Context.profileDataStore by preferencesDataStore("profile_data")

class ProfileRepository(private val context: Context) {

    private val nameKey = stringPreferencesKey("name")
    private val jobKey = stringPreferencesKey("job")
    private val avatarKey = stringPreferencesKey("avatar")
    private val resumeKey = stringPreferencesKey("resume")
    private val favoritePairTime = stringPreferencesKey("favoritePairTime")

    val profile: Flow<ProfileData> = context.profileDataStore.data.map { prefs ->
        ProfileData(
            fullName = prefs[nameKey] ?: "",
            jobTitle = prefs[jobKey] ?: "",
            avatarUri = prefs[avatarKey] ?: "",
            resumeUri = prefs[resumeKey] ?: "",
            favoritePairTime = prefs[favoritePairTime] ?: ""
        )
    }

    suspend fun saveProfile(profile: ProfileData) {
        context.profileDataStore.edit { prefs ->
            prefs[nameKey] = profile.fullName
            prefs[jobKey] = profile.jobTitle
            prefs[avatarKey] = profile.avatarUri
            prefs[resumeKey] = profile.resumeUri
            prefs[favoritePairTime] = profile.favoritePairTime
        }
    }
}