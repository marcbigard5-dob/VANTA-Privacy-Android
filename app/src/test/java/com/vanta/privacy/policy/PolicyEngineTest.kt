package com.vanta.privacy.policy

import org.junit.Assert.assertEquals
import org.junit.Test

class PolicyEngineTest {

    @Test
    fun matchingFactProducesExpectedWarning() {
        val engine = PolicyEngine(PolicyEngine.defaultPolicies())
        val result = engine.evaluate(Scope.WIFI, setOf("network_open"))

        assertEquals(1, result.size)
        assertEquals(Action.WARN, result.first().action)
        assertEquals("wifi.open-network", result.first().policyId)
    }

    @Test
    fun unrelatedFactDoesNotTriggerPolicy() {
        val engine = PolicyEngine(PolicyEngine.defaultPolicies())
        val result = engine.evaluate(Scope.WIFI, setOf("wifi_connected"))

        assertEquals(0, result.size)
    }

    @Test
    fun scopeMustMatch() {
        val engine = PolicyEngine(PolicyEngine.defaultPolicies())
        val result = engine.evaluate(Scope.BLUETOOTH, setOf("network_open"))

        assertEquals(0, result.size)
    }
}
