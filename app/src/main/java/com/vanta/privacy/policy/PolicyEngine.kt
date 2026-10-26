package com.vanta.privacy.policy

/**
 * Minimal deterministic policy engine foundation.
 * This version evaluates explicitly supplied facts only; it does not inspect OS traffic.
 */
class PolicyEngine(private val policies: List<Policy>) {
    fun policies(): List<Policy> = policies.toList()

    fun evaluate(scope: Scope, facts: Set<String>): List<PolicyDecision> =
        policies.asSequence()
            .filter { it.scope == scope && facts.contains(it.condition) }
            .map { PolicyDecision(it.id, it.action, it.explanation) }
            .toList()

    companion object {
        fun defaultPolicies() = listOf(
            Policy(
                id = "wifi.open-network",
                scope = Scope.WIFI,
                condition = "network_open",
                action = Action.WARN,
                explanation = "The active Wi-Fi network is reported as open; use caution."
            ),
            Policy(
                id = "content.unscanned-file",
                scope = Scope.IMPORTED_FILE,
                condition = "file_unscanned",
                action = Action.WARN,
                explanation = "This file has not been inspected by VANTA."
            )
        )
    }
}
