package com.omer_dogan.tranimaci

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class TrAnimaciPlugin : Plugin() {
    override fun load(context: Context) {
        registerMainAPI(TrAnimaci())
    }
}