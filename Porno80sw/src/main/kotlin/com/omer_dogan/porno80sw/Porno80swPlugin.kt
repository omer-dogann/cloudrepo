package com.omer_dogan.porno80sw

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class Porno80swPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(Porno80sw())
    }
}