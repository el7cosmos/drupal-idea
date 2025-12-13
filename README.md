# Drupal Toolkit

![Build](https://github.com/el7cosmos/drupal-toolkit/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/29232-drupal-toolkit.svg)](https://plugins.jetbrains.com/plugin/29232-drupal-toolkit)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/29232-drupal-toolkit.svg)](https://plugins.jetbrains.com/plugin/29232-drupal-toolkit)

<!-- Plugin description -->

## Features

### Template

- Provide twig namespace for twig files under `MODULE_NAME/templates` or `THEME_NAME/templates` directory

### Single Directory Component (SDC) Integration

- Provide completion for SDC namespace in twig file and in render array:
    ```twig
    {{ include('<caret>') }}
    {% embed '<caret>' %}
    {% include '<caret>' %}
    ```
    ```php
    <?php
  
    $build = [
      '#type' => 'component',
      '#component' => '<caret>',
    ];
    ```

- Provide a goto symbol in twig files and in render array, allows ease of navigation by clicking the component's name.
- Provide variables for slots and props in the component's Twig file.

### Predefined variables

Declare predefined variables in the following files:

- `settings.php`: `$app_root`, `$site_path`

---

> [!NOTE]
> **Drupal is a registered trademark of Dries Buytaert.**

<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "drupal-toolkit"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/29232-drupal-toolkit) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/29232-drupal-toolkit/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/el7cosmos/drupal-toolkit/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template

[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
