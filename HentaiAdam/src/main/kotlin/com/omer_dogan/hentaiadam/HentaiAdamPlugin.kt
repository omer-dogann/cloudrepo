package com.omer_dogan.hentaiadam

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class HentaiAdamPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(HentaiAdam())
    }
}