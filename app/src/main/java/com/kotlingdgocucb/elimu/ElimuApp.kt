package com.kotlingdgocucb.elimu

import android.app.Application
import com.google.firebase.FirebaseApp
import com.kotlingdgocucb.elimu.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ElimuApp : Application() {

    override fun onCreate() {
        FirebaseApp.initializeApp(this)

        super.onCreate()
        startKoin {


            androidContext(this@ElimuApp)
            androidLogger()
            modules(appModule)
        }
    }
}