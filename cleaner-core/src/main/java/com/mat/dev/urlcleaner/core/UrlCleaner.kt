package com.mat.dev.urlcleaner.core

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import android.util.Log

/**
 * Data class representing the result of a URL cleaning operation.
 *
 * @property url The sanitized URL with tracking parameters removed.
 * @property preferredPackages A list of Android package names capable of handling this URL,
 * extracted from the rules database.
 */
data class CleanResult(
    val url: String,
    val preferredPackages: List<String> = emptyList()
)

/**
 * UrlCleaner is a lightweight engine designed to strip tracking parameters from URLs
 * and provide routing information for specific platforms.
 *
 * It uses a JSON-based ruleset to identify global and platform-specific trackers.
 */
object UrlCleaner {

    private const val TAG = "UrlCleaner"
    private val globalTrackers = mutableListOf<String>()
    private val platformRules = mutableListOf<PlatformRule>()

    /**
     * Internal representation of a platform-specific cleaning and routing rule.
     */
    private data class PlatformRule(
        val name: String,
        val hosts: List<String>,
        val packages: List<String>,
        val params: List<String>
    )

    /**
     * Initializes the engine by loading the rules from the assets folder.
     * This should be called once, typically in the Application class or the main Activity.
     *
     * @param context The application context to access assets.
     */
    fun init(context: Context) {
        try {
            val jsonString = context.assets.open("rules.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)

            // Parse global trackers
            val globals = root.optJSONArray("global_trackers")
            globalTrackers.clear()
            globals?.let {
                for (i in 0 until it.length()) {
                    globalTrackers.add(it.getString(i).lowercase())
                }
            }

            // Parse platform-specific rules
            val platforms = root.optJSONArray("platform_rules")
            platformRules.clear()
            platforms?.let {
                for (i in 0 until it.length()) {
                    val item = it.getJSONObject(i)
                    platformRules.add(
                        PlatformRule(
                            name = item.optString("name", "Unknown"),
                            hosts = jsonArrayToList(item.optJSONArray("hosts")),
                            packages = jsonArrayToList(item.optJSONArray("packages")),
                            params = jsonArrayToList(item.optJSONArray("params"))
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize UrlCleaner: ${e.message}")
        }
    }

    /**
     * Cleans the provided URL by removing known tracking parameters and identifies
     * the target mobile application.
     *
     * @param url The raw URL to be cleaned.
     * @return A [CleanResult] containing the sanitized URL and target package information.
     */
    fun clean(url: String): CleanResult {
        if (url.isBlank()) return CleanResult(url)

        return try {
            val uri = Uri.parse(url.trim())
            val host = uri.host?.lowercase() ?: return CleanResult(url)

            // Find a rule where any of the defined hosts match the current URL host
            val matchedRule = platformRules.find { rule ->
                rule.hosts.any { platformHost -> host.contains(platformHost) }
            }

            // Combine global trackers with platform-specific ones
            val specificTrash = matchedRule?.params ?: emptyList()
            val allTrash = globalTrackers + specificTrash

            // Rebuild the URL excluding unwanted parameters
            val builder = uri.buildUpon().clearQuery()
            var hasParams = false

            for (param in uri.queryParameterNames) {
                if (param.lowercase() !in allTrash) {
                    builder.appendQueryParameter(param, uri.getQueryParameter(param))
                    hasParams = true
                }
            }

            CleanResult(
                url = builder.build().toString(),
                preferredPackages = matchedRule?.packages ?: emptyList()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning URL: ${e.message}")
            CleanResult(url)
        }
    }

    /**
     * Utility function to convert a JSONArray into a List of Strings.
     */
    private fun jsonArrayToList(array: JSONArray?): List<String> {
        val list = mutableListOf<String>()
        if (array == null) return list
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        return list
    }
}