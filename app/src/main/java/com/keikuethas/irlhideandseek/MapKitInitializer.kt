package com.keikuethas.irlhideandseek

import android.content.Context
import com.yandex.mapkit.MapKitFactory

//NOTE: вайбкод
object MapKitInitializer {
    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return

        synchronized(this) {
            if (isInitialized) return

            MapKitFactory.setApiKey(
                context.getString(R.string.MAPKIT_API_KEY)
            )
            MapKitFactory.initialize(context.applicationContext)
            isInitialized = true
        }
    }
}