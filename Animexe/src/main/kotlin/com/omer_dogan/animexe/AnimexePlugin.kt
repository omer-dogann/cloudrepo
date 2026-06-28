package com.omer_dogan.animexe

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class AnimexePlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(Animexe())
    }
}