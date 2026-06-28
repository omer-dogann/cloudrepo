package com.omer_dogan.animpow

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class AnimpowPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(Animpow())
    }
}