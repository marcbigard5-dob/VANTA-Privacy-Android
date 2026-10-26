package com.vanta.privacy.policy

enum class Scope { NETWORK, WIFI, BLUETOOTH, IMPORTED_FILE, SHARED_CONTENT }
enum class Action { ALLOW, WARN, DENY, QUARANTINE, REQUIRE_CONSENT }

data class Policy(
    val id: String,
    val scope: Scope,
    val condition: String,
    val action: Action,
    val explanation: String
)

data class PolicyDecision(
    val policyId: String,
    val action: Action,
    val explanation: String
)
