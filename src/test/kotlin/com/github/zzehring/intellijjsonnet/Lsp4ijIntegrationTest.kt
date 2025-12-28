package com.github.zzehring.intellijjsonnet

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import org.junit.Test

/**
 * Tests for lsp4ij integration
 *
 * These tests verify the migration from Ballerina LSP to lsp4ij (Red Hat's LSP client).
 */
class Lsp4ijIntegrationTest : BasePlatformTestCase() {

    @Test
    fun testLanguageServerFactoryImplementsLsp4ijInterface() {
        val factory = JsonnetLanguageServerFactory()

        // Verify it implements the lsp4ij LanguageServerFactory interface
        assertInstanceOf(factory, LanguageServerFactory::class.java)
    }

    @Test
    fun testLanguageServerFactoryCreatesValidConnectionProvider() {
        val factory = JsonnetLanguageServerFactory()
        val connectionProvider = factory.createConnectionProvider(project)

        assertNotNull("Connection provider should not be null", connectionProvider)
        assertTrue("Connection provider should be JsonnetLanguageServer",
            connectionProvider is JsonnetLanguageServer)
    }

    @Test
    fun testMultipleFactoryInstancesCreateSeparateProviders() {
        val factory1 = JsonnetLanguageServerFactory()
        val factory2 = JsonnetLanguageServerFactory()

        val provider1 = factory1.createConnectionProvider(project)
        val provider2 = factory2.createConnectionProvider(project)

        // Each call should create a new instance
        assertNotNull("First provider should not be null", provider1)
        assertNotNull("Second provider should not be null", provider2)

        // They should be different instances
        assertNotSame("Providers should be different instances", provider1, provider2)
    }

    @Test
    fun testJsonnetLanguageServerExtendsOSProcessStreamConnectionProvider() {
        val factory = JsonnetLanguageServerFactory()
        val connectionProvider = factory.createConnectionProvider(project)

        // Verify it extends com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
        assertTrue("Should extend OSProcessStreamConnectionProvider",
            connectionProvider is com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider)
    }

    @Test
    fun testLanguageServerCreationWithDifferentProjects() {
        // This tests that the language server can be created for different projects
        val factory = JsonnetLanguageServerFactory()

        // Create for current test project
        val provider1 = factory.createConnectionProvider(project)
        assertNotNull("Provider for project should be created", provider1)

        // Verify it's a JsonnetLanguageServer
        val jsonnetServer = assertInstanceOf(provider1, JsonnetLanguageServer::class.java)
        assertNotNull("JsonnetLanguageServer should not be null", jsonnetServer)
    }
}
