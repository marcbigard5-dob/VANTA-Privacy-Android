package com.vanta.privacy

import java.net.URI
import java.util.Locale

/**
 * Local-only URL structure heuristics. This class deliberately performs no network requests
 * and must not be presented as a malware or reputation verdict.
 */
internal object UrlAnalyzer {
    fun analyze(input: String): String {
        val raw = input.trim()
        if (raw.isEmpty()) return "Enter a URL first."
        if (raw.any { it.isWhitespace() }) {
            return "WARNING: URL contains whitespace. Copy the address carefully and check for accidental characters."
        }
        val parsed = try {
            URI(raw)
        } catch (_: Exception) {
            return "INVALID URL FORMAT\nThe address could not be parsed. Check the spelling and include https:// or http://."
        }
        val scheme = parsed.scheme?.lowercase(Locale.ROOT)
        val host = parsed.host?.lowercase(Locale.ROOT)
        val findings = mutableListOf<String>()
        if (scheme != "https" && scheme != "http") findings += "WARNING: scheme is missing or is not HTTP/HTTPS."
        if (scheme == "http") findings += "WARNING: HTTP does not encrypt the connection to the website."
        if (host.isNullOrBlank()) findings += "WARNING: no valid hostname was detected."
        if (parsed.rawUserInfo != null) findings += "WARNING: address contains user-info before the host; this can disguise the real destination."
        if (host?.startsWith("xn--") == true || host?.split(".")?.any { it.startsWith("xn--") } == true) {
            findings += "CAUTION: hostname contains an internationalized/punycode label; verify the spelling carefully."
        }
        val shorteners = setOf("bit.ly", "tinyurl.com", "t.co", "is.gd", "cutt.ly", "rb.gy", "ow.ly")
        if (host in shorteners) findings += "CAUTION: URL uses a known link-shortening domain, so the final destination is hidden."
        if (raw.length > 200) findings += "CAUTION: unusually long URL; inspect the full address carefully."
        if (raw.contains('@')) findings += "CAUTION: contains '@'; confirm the actual hostname shown above."
        if (findings.isEmpty()) {
            findings += "No basic structural warning detected. This does NOT establish that the website is trustworthy."
        }
        return buildString {
            append("LOCAL URL REPORT\n")
            append("Scheme: ${scheme ?: "unknown"}\n")
            append("Host: ${host ?: "not detected"}\n")
            append("Length: ${raw.length} characters\n\n")
            append(findings.joinToString(separator = "\n") { "• $it" })
            append("\n\nNo website was contacted. No malware database or live reputation service was queried.")
        }
    }
}
