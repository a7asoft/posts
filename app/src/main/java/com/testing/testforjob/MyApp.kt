package com.testing.testforjob

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

    }
}