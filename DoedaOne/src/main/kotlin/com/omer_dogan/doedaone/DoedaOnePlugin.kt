package com.omer_dogan.doedaone

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class DoedaOnePlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(DoedaOne())
    }
}