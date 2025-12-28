package com.github.zzehring.intellijjsonnet.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.TableUtil
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.FormBuilder
import org.jetbrains.annotations.NotNull
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog
 */
class JLSSettingsComponent {
    var settingsPanel: JPanel
    var jPathsPanel: JPanel
    private val releaseRepository = JBTextField()
    private val enableLintDiagnostics = JBCheckBox("Enable lint diagnostics on language server")
    private val enableEvalDiagnostics = JBCheckBox("Enable eval diagnostics on language")
    private val enableTankaMode = JBCheckBox("Enable Tanka mode (recommended for Tanka projects)")
    private val useLocalBinary = JBCheckBox("Use local language server binary (instead of auto-download)")
    private val localBinaryPath = TextFieldWithBrowseButton()
    private val jPathsTableModel = DefaultTableModel(arrayOf("JPath"), 0)

    init {
        // Configure local binary path file chooser
        localBinaryPath.addBrowseFolderListener(
            "Select Language Server Binary",
            "Select the jsonnet-language-server executable file",
            null,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )

        this.settingsPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Release Repo (Github Repository from which to download language server): "), releaseRepository, 1, true)
            .addComponent(enableEvalDiagnostics)
            .addTooltip("Try to evaluate files to find errors and warnings. Disable on large projects to improve performance. IDE restart required.")
            .addComponent(enableLintDiagnostics)
            .addTooltip("Enable live linting diagnostics. Disable on large projects to improve performance. IDE restart required.")
            .addComponent(enableTankaMode)
            .addTooltip("Enable Tanka mode for the language server. Disable if using custom JPaths without Tanka. IDE restart required.")
            .addComponent(useLocalBinary)
            .addTooltip("Use a locally installed language server binary instead of auto-downloading from GitHub. Useful for air-gapped environments. IDE restart required.")
            .addLabeledComponent(JBLabel("Local binary path: "), localBinaryPath, 1, true)
            .addTooltip("Path to the jsonnet-language-server executable. Only used when 'Use local binary' is enabled.")
            .panel
        val jPathsTable = JBTable(jPathsTableModel)
        val tablePanel = ToolbarDecorator.createDecorator(jPathsTable)
            .setAddAction {
                jPathsTableModel.addRow(arrayOf())
                TableUtil.editCellAt(jPathsTable, jPathsTableModel.rowCount - 1, 0)
            }
            .setRemoveAction {
                for (i in jPathsTable.selectedRows.reversed()) {
                    jPathsTableModel.removeRow(i)
                }
            }
            .createPanel()
        jPathsPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("JPaths (Additional directories to search for jsonnet library files): "), tablePanel, 1, true)
            .panel
    }

    fun getPreferredFocusedComponent(): JComponent {
        return releaseRepository
    }

    @NotNull
    fun getReleaseRepository(): String {
        return releaseRepository.text
    }

    fun setReleaseRepository(newPath: String) {
        releaseRepository.text = newPath
    }

    fun getEnableLintDiagnostics(): Boolean {
        return enableLintDiagnostics.isSelected
    }

    fun setEnableLintDiagnostics(isSelected: Boolean) {
        enableLintDiagnostics.isSelected = isSelected
    }

    fun getEnableEvalDiagnostics(): Boolean {
        return enableEvalDiagnostics.isSelected
    }

    fun setEnableEvalDiagnostics(isSelected: Boolean) {
        enableEvalDiagnostics.isSelected = isSelected
    }

    fun getEnableTankaMode(): Boolean {
        return enableTankaMode.isSelected
    }

    fun setEnableTankaMode(isSelected: Boolean) {
        enableTankaMode.isSelected = isSelected
    }

    fun getJPaths(): List<String> {
        val paths = mutableListOf<String>()
        for (i in 0 until jPathsTableModel.rowCount) {
            val aPath = jPathsTableModel.getValueAt(i, 0)
            paths.add(aPath.toString())
        }
        return paths
    }

    fun setJPaths(paths: List<String>) {
        jPathsTableModel.rowCount = 0
        for (path in paths) {
            jPathsTableModel.addRow(arrayOf(path))
        }
    }

    fun getUseLocalBinary(): Boolean {
        return useLocalBinary.isSelected
    }

    fun setUseLocalBinary(isSelected: Boolean) {
        useLocalBinary.isSelected = isSelected
    }

    fun getLocalBinaryPath(): String {
        return localBinaryPath.text
    }

    fun setLocalBinaryPath(path: String) {
        localBinaryPath.text = path
    }

}