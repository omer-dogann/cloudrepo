package com.omer_dogan.doeda

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class DoedaPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(Doeda())
    }
}