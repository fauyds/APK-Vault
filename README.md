<div align="center">

# 📦 APK Vault

**A clean, category-first Android APK organizer and offline package manager.**  
Designed fully in line with official **Google Material Design guidelines**.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-SDK%2026+-34A853?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Jetpack Compose](https://img.shields.io/badge/Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

<p align="center">
  <a href="#-what-is-apk-vault">Overview</a> •
  <a href="#-key-features">Features</a> •
  <a href="#-what-you-can-do-with-apks">Actions</a> •
  <a href="#-tech-stack">Tech Stack</a> •
  <a href="#-license">License</a>
</p>

</div>

---

## 🎯 What is APK Vault?

Downloading and archiving Android apps outside app stores quickly clutters storage with misplaced installers. **APK Vault** scans your device and organizes all your installer files into clean, custom categories so you can manage, install, and maintain your standalone apps from one unified hub.

---

## ⚡ Key Features

* 🗂 **Custom Category Management**  
  Group your installers into isolated categories (e.g., *Games*, *Tools*, *Mods*, *Dev Builds*). The main dashboard includes default `All APKs` and `Uncategorized` roots.

* 🌐 **Online Catalog of 2,000+ Google Material Icons**  
  Personalize every category with icons fetched directly on demand from the official Google Material Symbols library. Once loaded, icons are cached locally and work completely offline.

* 🛡 **Safe Category Cleanup**  
  Deleting a category never touches your actual files on disk; assigned packages are automatically demoted back to `Uncategorized`.

* 📊 **Storage & Count Overview**  
  Live summary header tracking total installer count, total disk space consumed, and organized groups.

---

## 🔧 What You Can Do with APKs

<details open>
<summary><b>📦 Core APK Operations (Click to collapse)</b></summary>
<br>

* 📥 **Install & Reinstall:** Launch direct installations or force clean reinstalls of any package.
* 🔄 **Update Apps:** Detect and update already installed applications with newer APK builds.
* 🔍 **Inspect APK Manifest & Info:** Read `AndroidManifest.xml` metadata on the fly (package name and version codes) without unpacking or modifying the archive.
* 🗑 **Delete APKs:** Permanently purge obsolete installer binaries directly from device storage without needing external file explorers.
* 🏷 **Quick Reassign:** Move any installer to a different category in one tap.

</details>

---

## 🛠 Tech Stack

- **UI:** Jetpack Compose (following official Google Material guidelines)
- **Language:** Kotlin & Coroutines
- **Image Pipeline:** Coil with SVG vector decoding & disk caching
- **Storage & Inspection:** Android `PackageManager` archive APIs & local storage sync

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
