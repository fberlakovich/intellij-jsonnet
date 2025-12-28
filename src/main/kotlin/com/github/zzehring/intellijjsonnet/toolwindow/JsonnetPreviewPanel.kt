package com.github.zzehring.intellijjsonnet.toolwindow

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.messages.MessageBusConnection
import com.redhat.devtools.lsp4ij.LSPRequestConstants
import com.redhat.devtools.lsp4ij.commands.CommandExecutor
import com.redhat.devtools.lsp4ij.commands.LSPCommandContext
import org.eclipse.lsp4j.Command
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.SwingConstants
import com.intellij.openapi.editor.Document

class JsonnetPreviewPanel(private val project: Project) {

    private val log = Logger.getInstance(JsonnetPreviewPanel::class.java)
    private val panel = JBPanel<JBPanel<*>>(BorderLayout())
    private var previewEditor: EditorEx? = null
    private var connection: MessageBusConnection? = null
    private var currentFile: VirtualFile? = null

    init {
        setupUI()
        setupFileEditorListener()
        setupDocumentSaveListener()
        refreshPreview()
    }

    private fun setupUI() {
        // Create a read-only editor for displaying JSON output
        val editorFactory = EditorFactory.getInstance()
        val document = editorFactory.createDocument("")
        previewEditor = editorFactory.createEditor(document, project) as EditorEx

        // Configure editor settings
        previewEditor?.let { editor ->
            editor.settings.isLineNumbersShown = true
            editor.settings.isLineMarkerAreaShown = false
            editor.settings.isFoldingOutlineShown = true
            editor.settings.isRightMarginShown = false
            editor.isViewer = true // Read-only

            // Set JSON file type for syntax highlighting
            val virtualFile = LightVirtualFile("preview.json", "")
            (editor.document as? com.intellij.openapi.editor.impl.DocumentImpl)?.let { doc ->
                // Associate the document with JSON file type for syntax highlighting
                FileDocumentManager.getInstance()
            }
        }

        panel.add(JBScrollPane(previewEditor?.component), BorderLayout.CENTER)
    }

    private fun setupFileEditorListener() {
        // Listen for file editor changes
        connection = project.messageBus.connect()
        connection?.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {
            override fun selectionChanged(event: com.intellij.openapi.fileEditor.FileEditorManagerEvent) {
                val newFile = event.newFile
                if (newFile != null && isJsonnetFile(newFile)) {
                    currentFile = newFile
                    refreshPreview()
                }
            }
        })

        // Get currently selected file
        val selectedFiles = FileEditorManager.getInstance(project).selectedFiles
        if (selectedFiles.isNotEmpty() && isJsonnetFile(selectedFiles[0])) {
            currentFile = selectedFiles[0]
        }
    }

    private fun setupDocumentSaveListener() {
        // Listen for document saves to auto-refresh preview
        connection?.subscribe(
            com.intellij.AppTopics.FILE_DOCUMENT_SYNC,
            object : FileDocumentManagerListener {
                override fun beforeDocumentSaving(document: Document) {
                    val file = FileDocumentManager.getInstance().getFile(document)
                    if (file != null && file == currentFile && isJsonnetFile(file)) {
                        // Refresh preview after save
                        ApplicationManager.getApplication().invokeLater {
                            refreshPreview()
                        }
                    }
                }
            }
        )
    }

    private fun isJsonnetFile(file: VirtualFile): Boolean {
        val extension = file.extension?.lowercase()
        return extension == "jsonnet" || extension == "libsonnet"
    }

    fun refreshPreview() {
        val file = currentFile
        if (file == null || !isJsonnetFile(file)) {
            updatePreviewText("No Jsonnet file selected.\n\nOpen a .jsonnet or .libsonnet file to see the preview.")
            return
        }

        // Save the current document before evaluating
        ApplicationManager.getApplication().runReadAction {
            val document = FileDocumentManager.getInstance().getDocument(file)
            if (document != null) {
                FileDocumentManager.getInstance().saveDocument(document)
            }
        }

        // Execute the LSP command to evaluate the file
        try {
            val command = Command("Evaluate Jsonnet File", "jsonnet.evalFile", listOf(file.path))
            val commandContext = LSPCommandContext(command, project)
            commandContext.preferredLanguageServerId = "jsonnetLanguageServerId"

            CommandExecutor.executeCommand(commandContext)
                .response()
                .thenAccept { result ->
                    if (result != null) {
                        updatePreviewText(result.toString())
                    } else {
                        updatePreviewText("// Evaluation returned no result\n// This might indicate an error in the Jsonnet file")
                    }
                }
                .exceptionally { throwable ->
                    log.warn("Failed to evaluate Jsonnet file for preview", throwable)
                    updatePreviewText("// Error evaluating file:\n// ${throwable.message}")
                    null
                }
        } catch (e: Exception) {
            log.warn("Failed to execute evaluation command", e)
            updatePreviewText("// Error:\n// ${e.message}")
        }
    }

    private fun updatePreviewText(text: String) {
        ApplicationManager.getApplication().invokeLater {
            previewEditor?.let { editor ->
                ApplicationManager.getApplication().runWriteAction {
                    editor.document.setText(text)
                }
            }
        }
    }

    fun getContent(): JComponent {
        return panel
    }

    fun dispose() {
        connection?.disconnect()
        previewEditor?.let { EditorFactory.getInstance().releaseEditor(it) }
    }
}
