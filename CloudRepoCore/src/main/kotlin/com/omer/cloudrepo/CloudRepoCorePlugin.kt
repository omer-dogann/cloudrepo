package com.omer.cloudrepo

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context
import com.omer.cloudrepo.extractors.VidmolyExtractor
import com.omer.cloudrepo.extractors.FilemoonExtractor

@CloudstreamPlugin
class CloudRepoCorePlugin : Plugin() {
    override fun load(context: Context) {
        registerExtractorAPI(VidmolyExtractor())
        registerExtractorAPI(FilemoonExtractor())
    }
}
