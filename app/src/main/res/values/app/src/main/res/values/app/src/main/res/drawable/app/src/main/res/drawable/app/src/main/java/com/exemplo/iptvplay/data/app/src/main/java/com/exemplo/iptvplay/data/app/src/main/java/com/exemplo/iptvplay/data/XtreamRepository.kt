package com.exemplo.iptvplay.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object XtreamRepository {

    private fun normalizeServer(raw: String): String {
        val t = raw.trim().trimEnd('/')
        return if (t.startsWith("http")) t else "http://$t"
    }

    private fun fetch(url: String): String {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15_000
            readTimeout = 30_000
            requestMethod = "GET"
        }
        val text = conn.inputStream.bufferedReader().use { it.readText() }
        conn.disconnect()
        return text
    }

    private fun base(cred: XtreamCredentials): String {
        val s = normalizeServer(cred.server)
        return "$s/player_api.php?username=${cred.username}&password=${cred.password}"
    }

    suspend fun validate(cred: XtreamCredentials): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject(fetch(base(cred)))
            json.optJSONObject("user_info")?.optString("auth") == "1"
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loadChannels(cred: XtreamCredentials): List<Channel> =
        withContext(Dispatchers.IO) {
            val catJson = JSONArray(fetch("${base(cred)}&action=get_live_categories"))
            val catNames = mutableMapOf<String, String>()
            for (i in 0 until catJson.length()) {
                val o = catJson.getJSONObject(i)
                catNames[o.optString("category_id")] = o.optString("category_name")
            }

            val streamJson = JSONArray(fetch("${base(cred)}&action=get_live_streams"))
            val server = normalizeServer(cred.server)
            val out = mutableListOf<Channel>()
            for (i in 0 until streamJson.length()) {
                val o = streamJson.getJSONObject(i)
                val id = o.optInt("stream_id").toString()
                val catId = o.optString("category_id")
                val ext = o.optString("container_extension").ifBlank { "m3u8" }
                out += Channel(
                    id = id,
                    name = o.optString("name", "Canal"),
                    logoUrl = o.optString("stream_icon").takeIf { it.isNotBlank() && it != "null" },
                    group = catNames[catId] ?: "Outros",
                    streamUrl = "$server/live/${cred.username}/${cred.password}/$id.$ext"
                )
            }
            out
        }
}
