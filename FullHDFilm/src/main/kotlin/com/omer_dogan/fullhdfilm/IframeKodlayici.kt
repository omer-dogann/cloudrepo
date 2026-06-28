@file:Suppress("DEPRECATION", "PARAMETER_NAME_CHANGED_ON_OVERRIDE", "UNCHECKED_CAST", "UNUSED_VARIABLE", "DEPRECATION_ERROR")
package com.omer_dogan.fullhdfilm

import android.util.Log
import android.util.Base64
import java.util.Locale
import java.util.Arrays
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.math.min
import okhttp3.Interceptor
import okhttp3.Response
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.utils.StringUtils.decodeUri
import com.lagradost.cloudstream3.network.CloudflareKiller
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Document






// ! Ömer Doğan CloudRepo Deposu



class IframeKodlayici {
    companion object {
        fun tersCevir(metin: String): String {
            return metin.reversed()
        }

        fun base64Coz(encodedString: String): String {
            val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
            return String(decodedBytes, Charsets.UTF_8)
        }

        fun iframeParse(htmlIcerik: String): String {
            val iframePattern = """<iframe[^>]+src=["']([^"']+)["'][^>]*>""".toRegex()
            val match = iframePattern.find(htmlIcerik)
            return match?.groupValues?.get(1) ?: throw IllegalArgumentException("Iframe src bulunamadı")
        }
    }

    fun iframeCoz(veri: String): String {
        var tempVeri = veri
        if (!tempVeri.startsWith("PGltZyB3aWR0aD0iMTAwJSIgaGVpZ2")) {
            tempVeri = tersCevir("BSZtFmcmlGP") + tempVeri
        }
        val iframe = base64Coz(tempVeri)
        return iframeParse(iframe)
    }
}