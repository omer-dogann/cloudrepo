package com.omer_dogan.codanime

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class CodAnimePlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(CodAnime())
    }
}