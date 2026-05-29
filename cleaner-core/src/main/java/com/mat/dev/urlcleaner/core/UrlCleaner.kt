package com.mat.dev.urlcleaner.core

import android.content.Context
import android.net.Uri
import org.json.JSONObject

object UrlCleaner {

    private var globalTrackers = mutableListOf<String>()
    private val platformRules = mutableMapOf<String, List<String>>()

    fun init(context: Context) {
        try {
            val jsonString = context.assets.open("rules.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)

            // Global rules
            val globals = root.getJSONArray("global_trackers")
            for (i in 0 until globals.length()) {
                globalTrackers.add(globals.getString(i))
            }

            // Platform rules
            val platforms = root.getJSONArray("platform_rules")
            for (i in 0 until platforms.length()) {
                val item = platforms.getJSONObject(i)
                val host = item.getString("host")
                val paramsArray = item.getJSONArray("params")
                val paramsList = mutableListOf<String>()
                for (j in 0 until paramsArray.length()) {
                    paramsList.add(paramsArray.getString(j))
                }
                platformRules[host] = paramsList
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clean(url: String): String {
        return try {
            val uri = Uri.parse(url.trim())
            val host = uri.host?.lowercase() ?: return url

            val specificTrash = platformRules.entries
                .find { host.contains(it.key) }?.value ?: emptyList()

            val allTrash = globalTrackers + specificTrash

            val builder = uri.buildUpon().clearQuery()
            for (param in uri.queryParameterNames) {
                if (param.lowercase() !in allTrash) {
                    builder.appendQueryParameter(param, uri.getQueryParameter(param))
                }
            }
            builder.build().toString()
        } catch (e: Exception) {
            url
        }
    }
}