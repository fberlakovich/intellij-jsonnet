package com.github.zzehring.intellijjsonnet

import com.github.zzehring.intellijjsonnet.settings.JLSSettingsStateComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test

/**
 * Tests for JsonnetLanguageServer and related LSP integration
 */
class JsonnetLanguageServerTest : BasePlatformTestCase() {

    @Test
    fun testLanguageServerFactoryCreatesConnectionProvider() {
        val factory = JsonnetLanguageServerFactory()
        val connectionProvider = factory.createConnectionProvider(project)

        assertNotNull("Connection provider should not be null", connectionProvider)
        assertInstanceOf(connectionProvider, JsonnetLanguageServer::class.java)
    }

    @Test
    fun testSettingsStateComponentAvailable() {
        val settingsComponent = ApplicationManager.getApplication()
            .getService(JLSSettingsStateComponent::class.java)

        assertNotNull("Settings component should be available", settingsComponent)
        assertNotNull("Settings state should not be null", settingsComponent.state)
    }

    @Test
    fun testDefaultSettings() {
        val settings = JLSSettingsStateComponent.instance.state

        assertEquals("Default repo should be grafana/jsonnet-language-server",
            "grafana/jsonnet-language-server", settings.releaseRepository)
        assertFalse("Lint diagnostics should be disabled by default", settings.enableLintDiagnostics)
        assertFalse("Eval diagnostics should be disabled by default", settings.enableEvalDiagnostics)
        assertTrue("JPaths should be empty by default", settings.jPaths.isEmpty())
    }

    @Test
    fun testSettingsModification() {
        val settings = JLSSettingsStateComponent.instance.state
        val originalRepo = settings.releaseRepository

        try {
            // Modify settings
            settings.releaseRepository = "test/repo"
            settings.enableLintDiagnostics = true
            settings.jPaths = listOf("/path/one", "/path/two")

            // Verify modifications
            assertEquals("Repository should be updated", "test/repo", settings.releaseRepository)
            assertTrue("Lint diagnostics should be enabled", settings.enableLintDiagnostics)
            assertEquals("JPaths should have 2 entries", 2, settings.jPaths.size)
            assertEquals("First path should match", "/path/one", settings.jPaths[0])
        } finally {
            // Restore original values
            settings.releaseRepository = originalRepo
            settings.enableLintDiagnostics = false
            settings.jPaths = emptyList()
        }
    }
}
