package com.github.zzehring.intellijjsonnet

import com.github.zzehring.intellijjsonnet.toolwindow.JsonnetPreviewPanel
import com.github.zzehring.intellijjsonnet.toolwindow.JsonnetPreviewToolWindowFactory
import com.intellij.openapi.wm.ToolWindow
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Test
import org.mockito.Mockito
import javax.swing.JComponent

/**
 * Tests for Jsonnet Preview Tool Window
 */
class JsonnetPreviewToolWindowTest : BasePlatformTestCase() {

    @Test
    fun testToolWindowFactoryCreation() {
        val factory = JsonnetPreviewToolWindowFactory()
        assertNotNull("Tool window factory should be created", factory)
    }

    @Test
    fun testPreviewPanelCreation() {
        val panel = JsonnetPreviewPanel(project)
        assertNotNull("Preview panel should be created", panel)

        val content = panel.getContent()
        assertNotNull("Preview panel should have content", content)
        assertTrue("Content should be a JComponent", content is JComponent)
    }

    @Test
    fun testPreviewPanelDisposal() {
        val panel = JsonnetPreviewPanel(project)

        // Should not throw exception
        panel.dispose()
    }

    @Test
    fun testRefreshPreviewDoesNotCrash() {
        val panel = JsonnetPreviewPanel(project)

        // Should not throw exception even with no file selected
        panel.refreshPreview()
    }
}
