package com.github.zzehring.intellijjsonnet.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class JsonnetPreviewToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val previewPanel = JsonnetPreviewPanel(project)
        val content = ContentFactory.getInstance().createContent(previewPanel.getContent(), "", false)
        toolWindow.contentManager.addContent(content)
    }
}
