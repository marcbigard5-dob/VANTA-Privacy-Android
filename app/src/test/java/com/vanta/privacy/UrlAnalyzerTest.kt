package com.vanta.privacy

import org.junit.Assert.assertTrue
import org.junit.Test

class UrlAnalyzerTest {
    @Test
    fun emptyInputPromptsForUrl() {
        assertTrue(UrlAnalyzer.analyze("  ").contains("Enter a URL"))
    }

    @Test
    fun httpUrlWarnsAboutUnencryptedTransport() {
        assertTrue(UrlAnalyzer.analyze("http://example.com").contains("HTTP does not encrypt"))
    }

    @Test
    fun shortenerIsFlagged() {
        assertTrue(UrlAnalyzer.analyze("https://bit.ly/abc").contains("link-shortening domain"))
    }

    @Test
    fun userInfoIsFlagged() {
        assertTrue(UrlAnalyzer.analyze("https://trusted.example@evil.example/path").contains("user-info"))
    }

    @Test
    fun malformedUrlIsRejected() {
        assertTrue(UrlAnalyzer.analyze("https://[invalid").contains("INVALID URL FORMAT"))
    }

    @Test
    fun safeLookingUrlStillHasExplicitLimitations() {
        assertTrue(UrlAnalyzer.analyze("https://example.com").contains("does NOT establish"))
        assertTrue(UrlAnalyzer.analyze("https://example.com").contains("No website was contacted"))
    }
}
