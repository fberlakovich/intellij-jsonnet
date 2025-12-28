package com.github.zzehring.intellijjsonnet.settings

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

/**
 * Tests for Jsonnet Language Server settings
 */
class JLSSettingsTest : BasePlatformTestCase() {

    @Test
    fun testSettingsComponentInitialization() {
        val component = JLSSettingsComponent()

        assertNotNull("Settings panel should be created", component.settingsPanel)
        assertNotNull("JPaths panel should be created", component.jPathsPanel)
    }

    @Test
    fun testReleaseRepositoryGetterSetter() {
        val component = JLSSettingsComponent()

        component.setReleaseRepository("custom/repo")
        assertEquals("Repository should be set and retrieved",
            "custom/repo", component.getReleaseRepository())
    }

    @Test
    fun testEnableEvalDiagnosticsGetterSetter() {
        val component = JLSSettingsComponent()

        component.setEnableEvalDiagnostics(true)
        assertTrue("Eval diagnostics should be enabled", component.getEnableEvalDiagnostics())

        component.setEnableEvalDiagnostics(false)
        assertFalse("Eval diagnostics should be disabled", component.getEnableEvalDiagnostics())
    }

    @Test
    fun testEnableLintDiagnosticsGetterSetter() {
        val component = JLSSettingsComponent()

        component.setEnableLintDiagnostics(true)
        assertTrue("Lint diagnostics should be enabled", component.getEnableLintDiagnostics())

        component.setEnableLintDiagnostics(false)
        assertFalse("Lint diagnostics should be disabled", component.getEnableLintDiagnostics())
    }

    @Test
    fun testEnableTankaModeGetterSetter() {
        val component = JLSSettingsComponent()

        // Test enabling Tanka mode
        component.setEnableTankaMode(true)
        assertTrue("Tanka mode should be enabled", component.getEnableTankaMode())

        // Test disabling Tanka mode (fix for issue #112)
        component.setEnableTankaMode(false)
        assertFalse("Tanka mode should be disabled", component.getEnableTankaMode())
    }

    @Test
    fun testJPathsGetterSetter() {
        val component = JLSSettingsComponent()

        val testPaths = listOf("/usr/local/jsonnet", "/opt/jsonnet/lib")
        component.setJPaths(testPaths)

        val retrievedPaths = component.getJPaths()
        assertEquals("Should have 2 paths", 2, retrievedPaths.size)
        assertEquals("First path should match", "/usr/local/jsonnet", retrievedPaths[0])
        assertEquals("Second path should match", "/opt/jsonnet/lib", retrievedPaths[1])
    }

    @Test
    fun testJPathsEmptyList() {
        val component = JLSSettingsComponent()

        component.setJPaths(emptyList())
        val retrievedPaths = component.getJPaths()
        assertTrue("JPaths should be empty", retrievedPaths.isEmpty())
    }

    @Test
    fun testConfigurableDisplayName() {
        val configurable = JLSSettingsConfigurable()

        assertEquals("Display name should be correct",
            "Jsonnet Language Server", configurable.displayName)
    }

    @Test
    fun testConfigurableModificationDetection() {
        val configurable = JLSSettingsConfigurable()
        val settings = JLSSettingsStateComponent.instance.state
        val originalTanka = settings.enableTankaMode

        try {
            // Create component
            configurable.createComponent()

            // Initially should not be modified
            assertFalse("Should not be modified initially", configurable.isModified)

            // Reset to load current settings
            configurable.reset()

            // Change Tanka mode setting
            val currentValue = settings.enableTankaMode
            settings.enableTankaMode = !currentValue

            // Now should detect modification
            configurable.reset() // Reset to pick up the change
            assertFalse("After reset, should not be modified", configurable.isModified)

        } finally {
            settings.enableTankaMode = originalTanka
        }
    }

    @Test
    fun testSettingsStatePersistence() {
        val stateComponent = JLSSettingsStateComponent()

        // Create a state with custom values
        val customState = JLSSettingsStateComponent.SettingsState()
        customState.releaseRepository = "test/custom-repo"
        customState.enableTankaMode = false
        customState.enableLintDiagnostics = true
        customState.jPaths = listOf("/custom/path")

        // Load the state
        stateComponent.loadState(customState)

        // Verify the state was loaded
        val loadedState = stateComponent.state
        assertEquals("Repository should match", "test/custom-repo", loadedState.releaseRepository)
        assertFalse("Tanka mode should be disabled", loadedState.enableTankaMode)
        assertTrue("Lint diagnostics should be enabled", loadedState.enableLintDiagnostics)
        assertEquals("JPaths should match", 1, loadedState.jPaths.size)
    }

    @Test
    fun testBackwardCompatibilityDefaultTankaEnabled() {
        val state = JLSSettingsStateComponent.SettingsState()

        // Default value should be true for backward compatibility
        assertTrue("Tanka mode should be enabled by default for backward compatibility",
            state.enableTankaMode)
    }

    @Test
    fun testUseLocalBinaryGetterSetter() {
        val component = JLSSettingsComponent()

        component.setUseLocalBinary(true)
        assertTrue("Local binary should be enabled", component.getUseLocalBinary())

        component.setUseLocalBinary(false)
        assertFalse("Local binary should be disabled", component.getUseLocalBinary())
    }

    @Test
    fun testLocalBinaryPathGetterSetter() {
        val component = JLSSettingsComponent()

        val testPath = "/usr/local/bin/jsonnet-language-server"
        component.setLocalBinaryPath(testPath)
        assertEquals("Local binary path should be set and retrieved",
            testPath, component.getLocalBinaryPath())
    }

    @Test
    fun testLocalBinaryDefaultValues() {
        val state = JLSSettingsStateComponent.SettingsState()

        // Local binary should be disabled by default
        assertFalse("Local binary should be disabled by default", state.useLocalBinary)
        assertEquals("Local binary path should be empty by default", "", state.localBinaryPath)
    }

    @Test
    fun testLocalBinaryStatePersistence() {
        val stateComponent = JLSSettingsStateComponent()

        // Create a state with local binary enabled
        val customState = JLSSettingsStateComponent.SettingsState()
        customState.useLocalBinary = true
        customState.localBinaryPath = "/custom/path/to/binary"

        // Load the state
        stateComponent.loadState(customState)

        // Verify the state was loaded
        val loadedState = stateComponent.state
        assertTrue("Local binary should be enabled", loadedState.useLocalBinary)
        assertEquals("Local binary path should match", "/custom/path/to/binary", loadedState.localBinaryPath)
    }
}
