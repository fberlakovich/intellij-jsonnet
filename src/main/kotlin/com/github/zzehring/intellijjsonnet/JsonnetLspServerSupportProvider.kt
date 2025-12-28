package com.github.zzehring.intellijjsonnet

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider

class JsonnetLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(
        project: Project,
        file: VirtualFile,
        serverStarter: LspServerSupportProvider.LspServerStarter
    ) {
        if (file.extension == "jsonnet" || file.extension == "libsonnet") {
            serverStarter.ensureServerStarted(JsonnetLspServerDescriptor(project))
        }
    }
}
