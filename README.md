# intellij-jsonnet 

[//]: # (![Build]&#40;https://github.com/zzehring/intellij-jsonnet/workflows/Build/badge.svg&#41;)
[![Version](https://img.shields.io/jetbrains/plugin/v/18752.svg)](https://plugins.jetbrains.com/plugin/18752)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/18752.svg)](https://plugins.jetbrains.com/plugin/18752)
![Build](https://github.com/zzehring/intellij-jsonnet/workflows/Build/badge.svg)

<!-- Plugin description -->

Provides language support for Jsonnet files (e.g. `.jsonnet`, `.libsonnet`). Language Server support is provided by [Jsonnet Language Server](https://github.com/grafana/jsonnet-language-server) and includes:

- Go-to definition (scopes include dollar, self, local, cross-files etc.)
- Error/Warning/Linting diagnostics
- Standard Library Hover and autocomplete
- And more!


Additional plugin features:

- **Live Preview Panel** - Real-time preview of evaluated Jsonnet output with auto-refresh on save
- **Local Binary Support** - Use your own language server binary (for air-gapped environments)
- Auto update for the language server binary
- Evaluate Jsonnet file (Tools menu)
- Syntax Highlighting
- Code block folding
- Brace/bracket matching

<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Jsonnet Language Server"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/zzehring/intellij-jsonnet/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
This plugin uses [lsp4ij](https://github.com/redhat-developer/lsp4ij) (Red Hat's LSP client for IntelliJ) to integrate with the [Grafana Jsonnet Language Server](https://github.com/grafana/jsonnet-language-server).

**Benefits:**
- ✅ Works in **Community Edition** (not just Ultimate)
- ✅ Actively maintained by Red Hat
- ✅ Built-in **LSP Console** for debugging language server communication
- ✅ Full LSP feature support (completion, diagnostics, navigation, formatting, etc.)

This project would not have been possible without the great work of the following projects:

- [redhat-developer/lsp4ij](https://github.com/redhat-developer/lsp4ij) - LSP client framework
- [grafana/jsonnet-language-server](https://github.com/grafana/jsonnet-language-server) - Jsonnet LSP server
- [grafana/vscode-jsonnet](https://github.com/grafana/vscode-jsonnet) - VS Code Jsonnet extension
- [databricks/intellij-jsonnet](https://github.com/databricks/intellij-jsonnet) - Original IntelliJ plugin inspiration

Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
