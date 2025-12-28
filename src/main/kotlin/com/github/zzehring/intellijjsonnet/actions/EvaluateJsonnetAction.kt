package com.github.zzehring.intellijjsonnet.actions

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.platform.lsp.api.LspServerManager
import org.eclipse.lsp4j.ExecuteCommandParams
import org.jetbrains.annotations.NotNull


class EvaluateJsonnetAction : AnAction() {
    override fun update(@NotNull event: AnActionEvent) {
        val openedFile = event.getData(PlatformDataKeys.VIRTUAL_FILE)
        event.presentation.isEnabledAndVisible = openedFile != null && openedFile.extension == "jsonnet"
    }

    override fun actionPerformed(@NotNull event: AnActionEvent) {
        val tmpDir = FileUtilRt.createTempDirectory("jsonnet-plugin-tmpdir", null)
        val tmpResultFile = FileUtilRt.createTempFile(tmpDir, "jsonnet-eval", ".json")
        val openedFile = event.getData(PlatformDataKeys.VIRTUAL_FILE)
        val project = event.project ?: return

        try {
            val lspServerManager = LspServerManager.getInstance(project)
            val servers = lspServerManager.getServersForProvider(com.github.zzehring.intellijjsonnet.JsonnetLspServerSupportProvider::class.java)

            if (servers.isEmpty()) {
                Notification(
                    "lsp",
                    "Jsonnet language server is not running",
                    NotificationType.WARNING
                ).notify(project)
                return
            }

            val params = ExecuteCommandParams("jsonnet.evalFile", listOf(openedFile!!.path))
            servers.first().sendRequestToServer { server ->
                server.workspaceService.executeCommand(params)
            }.thenAccept { result ->
                if (result != null) {
                    tmpResultFile.writeText(result.toString())
                    val vf = VfsUtil.findFileByIoFile(tmpResultFile, true)
                    FileEditorManager.getInstance(project).openFile(vf!!, true)
                }
            }
        } catch (e: Exception) {
            Notification(
                "lsp",
                "Failed to evaluate Jsonnet file: ${e.message}",
                NotificationType.ERROR
            ).notify(project)
        }
    }
}