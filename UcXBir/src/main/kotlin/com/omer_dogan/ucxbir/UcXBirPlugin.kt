package com.omer_dogan.ucxbir

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class UcXBirPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(UcXBir())
    }
}