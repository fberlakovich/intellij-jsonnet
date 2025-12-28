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
    fun testSettingsStatePersistence() {
        val stateComponent = JLSSettingsStateComponent()

        // Create a state with custom values
        val customState = JLSSettingsStateComponent.SettingsState()
        customState.releaseRepository = "test/custom-repo"
        customState.enableLintDiagnostics = true
        customState.jPaths = listOf("/custom/path")

        // Load the state
        stateComponent.loadState(customState)

        // Verify the state was loaded
        val loadedState = stateComponent.state
        assertEquals("Repository should match", "test/custom-repo", loadedState.releaseRepository)
        assertTrue("Lint diagnostics should be enabled", loadedState.enableLintDiagnostics)
        assertEquals("JPaths should match", 1, loadedState.jPaths.size)
    }
}
