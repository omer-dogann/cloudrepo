package com.omer_dogan.dramaflix

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class DramaFlixPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(DramaFlix())
    }
}
