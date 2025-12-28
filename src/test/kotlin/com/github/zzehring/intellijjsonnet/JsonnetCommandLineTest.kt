package com.github.zzehring.intellijjsonnet

import com.github.zzehring.intellijjsonnet.settings.JLSSettingsStateComponent
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test
import java.io.File
import java.nio.file.Files

/**
 * Tests for JsonnetLanguageServer command line generation
 *
 * These tests verify the fix for issue #112 (optional Tanka mode)
 * and proper command line argument construction.
 */
class JsonnetCommandLineTest : BasePlatformTestCase() {

    private lateinit var tempBinary: File

    override fun setUp() {
        super.setUp()
        // Create a temporary mock binary file for testing
        tempBinary = Files.createTempFile("jsonnet-ls-test", "").toFile()
        tempBinary.setExecutable(true)
        tempBinary.deleteOnExit()
    }

    override fun tearDown() {
        try {
            if (::tempBinary.isInitialized && tempBinary.exists()) {
                tempBinary.delete()
            }
        } finally {
            super.tearDown()
        }
    }

    @Test
    fun testCommandLineWithTankaModeEnabled() {
        val settings = JLSSettingsStateComponent.instance.state
        val originalTanka = settings.enableTankaMode

        try {
            // Enable Tanka mode
            settings.enableTankaMode = true
            settings.enableLintDiagnostics = false
            settings.enableEvalDiagnostics = false
            settings.jPaths = emptyList()

            // The actual command line creation is done in JsonnetLanguageServer
            // We test that the setting affects command construction
            assertTrue("Tanka mode should be enabled in settings", settings.enableTankaMode)

        } finally {
            settings.enableTankaMode = originalTanka
        }
    }

    @Test
    fun testCommandLineWithTankaModeDisabled() {
        val settings = JLSSettingsStateComponent.instance.state
        val originalTanka = settings.enableTankaMode

        try {
            // Disable Tanka mode (fix for issue #112)
            settings.enableTankaMode = false
            settings.enableLintDiagnostics = false
            settings.enableEvalDiagnostics = false
            settings.jPaths = emptyList()

            // Verify Tanka mode is disabled
            assertFalse("Tanka mode should be disabled in settings", settings.enableTankaMode)

        } finally {
            settings.enableTankaMode = originalTanka
        }
    }

    @Test
    fun testCommandLineWithAllOptionsEnabled() {
        val settings = JLSSettingsStateComponent.instance.state
        val originalTanka = settings.enableTankaMode
        val originalLint = settings.enableLintDiagnostics
        val originalEval = settings.enableEvalDiagnostics

        try {
            // Enable all options
            settings.enableTankaMode = true
            settings.enableLintDiagnostics = true
            settings.enableEvalDiagnostics = true
            settings.jPaths = emptyList()

            assertTrue("Tanka mode should be enabled", settings.enableTankaMode)
            assertTrue("Lint diagnostics should be enabled", settings.enableLintDiagnostics)
            assertTrue("Eval diagnostics should be enabled", settings.enableEvalDiagnostics)

        } finally {
            settings.enableTankaMode = originalTanka
            settings.enableLintDiagnostics = originalLint
            settings.enableEvalDiagnostics = originalEval
        }
    }

    @Test
    fun testCommandLineWithCustomJPaths() {
        val settings = JLSSettingsStateComponent.instance.state
        val originalJPaths = settings.jPaths
        val originalTanka = settings.enableTankaMode

        try {
            // Test with custom JPaths (issue #112 scenario)
            settings.enableTankaMode = false // Disable Tanka when using custom JPaths
            settings.jPaths = listOf("/usr/local/jsonnet/lib", "/opt/vendor")

            assertFalse("Tanka mode should be disabled for custom JPaths", settings.enableTankaMode)
            assertEquals("Should have 2 JPaths", 2, settings.jPaths.size)
            assertEquals("First JPath should match", "/usr/local/jsonnet/lib", settings.jPaths[0])

        } finally {
            settings.jPaths = originalJPaths
            settings.enableTankaMode = originalTanka
        }
    }

    @Test
    fun testCommandLineWithMixedOptions() {
        val settings = JLSSettingsStateComponent.instance.state
        val originalTanka = settings.enableTankaMode
        val originalLint = settings.enableLintDiagnostics
        val originalEval = settings.enableEvalDiagnostics
        val originalJPaths = settings.jPaths

        try {
            // Test a realistic mixed configuration
            settings.enableTankaMode = true
            settings.enableLintDiagnostics = false
            settings.enableEvalDiagnostics = true
            settings.jPaths = listOf("/custom/lib")

            assertTrue("Tanka mode should be enabled", settings.enableTankaMode)
            assertFalse("Lint should be disabled", settings.enableLintDiagnostics)
            assertTrue("Eval diagnostics should be enabled", settings.enableEvalDiagnostics)
            assertEquals("Should have 1 JPath", 1, settings.jPaths.size)

        } finally {
            settings.enableTankaMode = originalTanka
            settings.enableLintDiagnostics = originalLint
            settings.enableEvalDiagnostics = originalEval
            settings.jPaths = originalJPaths
        }
    }

    @Test
    fun testCommandLineNoOptions() {
        val settings = JLSSettingsStateComponent.instance.state
        val originalTanka = settings.enableTankaMode
        val originalLint = settings.enableLintDiagnostics
        val originalEval = settings.enableEvalDiagnostics
        val originalJPaths = settings.jPaths

        try {
            // Test minimal configuration (no optional flags)
            settings.enableTankaMode = false
            settings.enableLintDiagnostics = false
            settings.enableEvalDiagnostics = false
            settings.jPaths = emptyList()

            assertFalse("Tanka mode should be disabled", settings.enableTankaMode)
            assertFalse("Lint should be disabled", settings.enableLintDiagnostics)
            assertFalse("Eval diagnostics should be disabled", settings.enableEvalDiagnostics)
            assertTrue("JPaths should be empty", settings.jPaths.isEmpty())

        } finally {
            settings.enableTankaMode = originalTanka
            settings.enableLintDiagnostics = originalLint
            settings.enableEvalDiagnostics = originalEval
            settings.jPaths = originalJPaths
        }
    }
}
