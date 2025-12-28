package com.github.zzehring.intellijjsonnet

import com.github.zzehring.intellijjsonnet.settings.JLSSettingsStateComponent
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for local binary support feature
 */
class LocalBinarySupportTest : BasePlatformTestCase() {

    @Test
    fun testLocalBinarySettingsDefault() {
        val settings = JLSSettingsStateComponent().state

        // By default, local binary should be disabled
        assertFalse("Local binary should be disabled by default", settings.useLocalBinary)
        assertEquals("Local binary path should be empty by default", "", settings.localBinaryPath)
    }

    @Test
    fun testLocalBinarySettingsCanBeEnabled() {
        val settingsComponent = JLSSettingsStateComponent()
        val settings = settingsComponent.state

        // Enable local binary and set path
        settings.useLocalBinary = true
        settings.localBinaryPath = "/usr/local/bin/jsonnet-language-server"

        assertTrue("Local binary should be enabled", settings.useLocalBinary)
        assertEquals(
            "Local binary path should be set",
            "/usr/local/bin/jsonnet-language-server",
            settings.localBinaryPath
        )
    }

    @Test
    fun testLocalBinarySettingsPersistence() {
        val settingsComponent = JLSSettingsStateComponent()

        // Set custom values
        val state1 = settingsComponent.state
        state1.useLocalBinary = true
        state1.localBinaryPath = "/custom/path/to/binary"

        // Simulate persistence by loading state
        val savedState = settingsComponent.getState()
        assertNotNull("State should be saveable", savedState)

        // Create new component and load state
        val newComponent = JLSSettingsStateComponent()
        newComponent.loadState(savedState!!)

        // Verify state was persisted
        val state2 = newComponent.state
        assertTrue("useLocalBinary should persist", state2.useLocalBinary)
        assertEquals("localBinaryPath should persist", "/custom/path/to/binary", state2.localBinaryPath)
    }

    @Test
    fun testBackwardCompatibility() {
        // Test that existing settings without local binary fields still work
        val settingsComponent = JLSSettingsStateComponent()
        val settings = settingsComponent.state

        // Simulate old settings (before local binary feature)
        settings.releaseRepository = "grafana/jsonnet-language-server"
        settings.enableLintDiagnostics = true
        settings.enableEvalDiagnostics = false
        settings.enableTankaMode = true

        // New fields should have default values
        assertFalse("useLocalBinary should default to false for backward compatibility", settings.useLocalBinary)
        assertEquals("localBinaryPath should default to empty string", "", settings.localBinaryPath)

        // Old fields should still work
        assertEquals("grafana/jsonnet-language-server", settings.releaseRepository)
        assertTrue(settings.enableLintDiagnostics)
        assertFalse(settings.enableEvalDiagnostics)
        assertTrue(settings.enableTankaMode)
    }
}
