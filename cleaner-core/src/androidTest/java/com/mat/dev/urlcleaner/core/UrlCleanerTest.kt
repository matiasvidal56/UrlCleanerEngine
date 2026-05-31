package com.mat.dev.urlcleaner.core

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UrlCleanerTest {

    @Before
    fun setup() {
        // Initialize the engine with the real assets from the project
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        UrlCleaner.init(appContext)
    }

    @Test
    fun testGlobalTrackerRemoval() {
        val rawUrl = "https://example.com/page?utm_source=google&fbclid=123&valid=true"
        val result = UrlCleaner.clean(rawUrl)

        // Should remove utm_source and fbclid, but keep 'valid'
        assertEquals("https://example.com/page?valid=true", result.url)
    }

    @Test
    fun testInstagramCleaningAndPackage() {
        val rawUrl = "https://www.instagram.com/reels/C12345/?igsh=abcde123&utm_source=share"
        val result = UrlCleaner.clean(rawUrl)

        // Verify URL is clean
        assertEquals("https://www.instagram.com/reels/C12345/", result.url)
        // Verify it identifies the Instagram package
        assertTrue(result.preferredPackages.contains("com.instagram.android"))
    }

    @Test
    fun testYouTubeShortHost() {
        val rawUrl = "https://youtu.be/dQw4w9WgXcQ?si=unique_id&feature=shared"
        val result = UrlCleaner.clean(rawUrl)

        // Should clean the short URL
        assertEquals("https://youtu.be/dQw4w9WgXcQ", result.url)
        // Should identify YouTube package
        assertTrue(result.preferredPackages.contains("com.google.android.youtube"))
    }

    @Test
    fun testUnknownUrl() {
        val rawUrl = "https://mywebsite.com/data?id=99"
        val result = UrlCleaner.clean(rawUrl)

        // Should stay the same
        assertEquals(rawUrl, result.url)
        // Should have no preferred packages
        assertTrue(result.preferredPackages.isEmpty())
    }
}