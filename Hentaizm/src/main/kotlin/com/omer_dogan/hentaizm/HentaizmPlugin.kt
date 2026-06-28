package com.omer_dogan.hentaizm

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class HentaizmPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(Hentaizm())
    }
}