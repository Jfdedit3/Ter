# Ter

Ter is a lightweight Android terminal-style application skeleton inspired by Termux.

## Included

- Android app project in Kotlin
- Dark terminal-style UI
- Simple built-in shell-like commands
- GitHub Actions workflow that builds a debug APK

## Current commands

- `help`
- `clear`
- `echo`
- `date`
- `whoami`
- `uname`
- `pwd`
- `ls`
- `history`
- `about`
- `pkg`

## Build on GitHub Actions

1. Push to `main` or run the workflow manually.
2. Open the `Android APK` workflow.
3. Download the `ter-debug-apk` artifact.

## Important

This is a buildable Android terminal-style base, not a full Termux replacement yet.
To become a real shell app, the next step is adding a native process backend or terminal emulator layer.
