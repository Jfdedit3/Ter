package com.jfdedit3.ter

import java.text.SimpleDateFormat
import java.util.Date
import java.util.LinkedHashSet
import java.util.Locale

class TerminalEngine {
    private data class PackageInfo(
        val name: String,
        val version: String,
        val description: String,
        val executable: String? = null
    )

    private val commandHistory = mutableListOf<String>()
    private val outputBuffer = mutableListOf<String>()

    private val packageRepo = linkedMapOf(
        "python" to PackageInfo("python", "3.12.4", "Python programming language", "python"),
        "git" to PackageInfo("git", "2.45.2", "Distributed version control system", "git"),
        "curl" to PackageInfo("curl", "8.8.0", "Command line data transfer tool", "curl"),
        "nano" to PackageInfo("nano", "8.0", "Terminal text editor", "nano"),
        "clang" to PackageInfo("clang", "18.1.8", "C, C++ and Objective-C compiler", "clang"),
        "nodejs" to PackageInfo("nodejs", "22.3.0", "JavaScript runtime built on V8", "node")
    )

    private val installedPackages = LinkedHashSet<String>().apply {
        add("bash")
        add("coreutils")
    }

    init {
        printLine("Welcome to Ter")
        printLine("A lightweight Android terminal-style app inspired by Termux.")
        printLine("Type 'help' to list commands.")
        printLine("Try: pkg search python")
        printLine("")
    }

    fun bootText(): String = outputBuffer.joinToString("\n")

    fun execute(rawInput: String): String {
        val input = rawInput.trim()
        if (input.isEmpty()) {
            return outputBuffer.joinToString("\n")
        }

        commandHistory += input
        printLine("$ $input")

        val parts = input.split(" ").filter { it.isNotBlank() }
        val command = parts.firstOrNull()?.lowercase(Locale.getDefault()).orEmpty()
        val args = parts.drop(1)

        when (command) {
            "help" -> showHelp()
            "clear" -> outputBuffer.clear()
            "echo" -> printLine(args.joinToString(" "))
            "date" -> showDate()
            "whoami" -> printLine("shell")
            "uname" -> printLine("Linux localhost 5.x android-ter aarch64")
            "pwd" -> printLine("/data/data/com.jfdedit3.ter/home")
            "ls" -> showLs(args)
            "history" -> showHistory()
            "about" -> showAbout()
            "pkg" -> handlePkg(args)
            "python" -> handlePython(args)
            "git" -> handleGit(args)
            "curl" -> handleCurl(args)
            "nano" -> handleNano(args)
            "node" , "nodejs" -> handleNode(args)
            "which" -> handleWhich(args)
            else -> {
                printLine("Command not found: $command")
                printLine("Type 'help' to list available commands.")
            }
        }

        return outputBuffer.joinToString("\n")
    }

    private fun showHelp() {
        printLine("Available commands:")
        printLine("help               - show available commands")
        printLine("clear              - clear the terminal output")
        printLine("echo               - print text")
        printLine("date               - show current date and time")
        printLine("whoami             - show current shell user")
        printLine("uname              - show device shell info")
        printLine("pwd                - print working directory")
        printLine("ls                 - list demo folders")
        printLine("history            - show entered commands")
        printLine("about              - app information")
        printLine("which <command>    - show executable path if installed")
        printLine("pkg help           - package manager usage")
        printLine("pkg search <name>  - search packages")
        printLine("pkg install <pkg>  - install a package in the local demo env")
        printLine("pkg remove <pkg>   - remove a package from the local demo env")
        printLine("pkg list-installed - list installed packages")
    }

    private fun showDate() {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        printLine(format.format(Date()))
    }

    private fun showLs(args: List<String>) {
        val target = args.firstOrNull().orEmpty()
        when (target) {
            "", "." -> {
                printLine("bin")
                printLine("home")
                printLine("tmp")
                printLine("usr")
            }

            "usr/bin", "/usr/bin" -> {
                val binaries = mutableListOf("bash", "cat", "echo", "ls", "pwd")
                installedPackages.forEach { pkg ->
                    packageRepo[pkg]?.executable?.let { binaries += it }
                }
                binaries.distinct().sorted().forEach(::printLine)
            }

            else -> printLine("ls: cannot access '$target': No such file or directory")
        }
    }

    private fun showHistory() {
        if (commandHistory.isEmpty()) {
            printLine("No history yet.")
            return
        }

        commandHistory.forEachIndexed { index, value ->
            printLine("${index + 1}  $value")
        }
    }

    private fun showAbout() {
        printLine("Ter v1.1.0")
        printLine("Terminal-style Android app skeleton.")
        printLine("Includes a local demo package manager layer.")
        printLine("Not a full native Linux userspace yet.")
    }

    private fun handlePkg(args: List<String>) {
        if (args.isEmpty() || args.first() == "help") {
            printLine("pkg usage:")
            printLine("pkg search <name>")
            printLine("pkg install <package> [package...]")
            printLine("pkg remove <package> [package...]")
            printLine("pkg list-installed")
            printLine("pkg update")
            printLine("pkg upgrade")
            return
        }

        when (args.first().lowercase(Locale.getDefault())) {
            "search" -> {
                val query = args.drop(1).joinToString(" ").trim().lowercase(Locale.getDefault())
                if (query.isEmpty()) {
                    printLine("pkg search: missing query")
                    return
                }

                val results = packageRepo.values.filter {
                    it.name.contains(query) || it.description.lowercase(Locale.getDefault()).contains(query)
                }

                if (results.isEmpty()) {
                    printLine("No packages found for '$query'.")
                } else {
                    results.forEach {
                        printLine("${it.name} - ${it.description} (${it.version})")
                    }
                }
            }

            "install" -> {
                val names = args.drop(1)
                if (names.isEmpty()) {
                    printLine("pkg install: missing package name")
                    return
                }

                names.forEach { rawName ->
                    val name = rawName.lowercase(Locale.getDefault())
                    val pkg = packageRepo[name]
                    when {
                        pkg == null -> printLine("Unable to locate package $name")
                        installedPackages.contains(name) -> printLine("$name is already the newest version (${pkg.version}).")
                        else -> {
                            printLine("Reading package lists...")
                            printLine("Building dependency tree...")
                            printLine("Installing $name (${pkg.version})...")
                            installedPackages += name
                            printLine("Setting up $name (${pkg.version})")
                            printLine("Done.")
                        }
                    }
                }
            }

            "remove", "uninstall" -> {
                val names = args.drop(1)
                if (names.isEmpty()) {
                    printLine("pkg remove: missing package name")
                    return
                }

                names.forEach { rawName ->
                    val name = rawName.lowercase(Locale.getDefault())
                    when {
                        name == "bash" || name == "coreutils" -> {
                            printLine("Refusing to remove protected package: $name")
                        }
                        !installedPackages.contains(name) -> {
                            printLine("Package '$name' is not installed.")
                        }
                        else -> {
                            installedPackages.remove(name)
                            printLine("Removing $name...")
                            printLine("Done.")
                        }
                    }
                }
            }

            "list-installed" -> {
                installedPackages.sorted().forEach { name ->
                    val version = packageRepo[name]?.version ?: "system"
                    printLine("$name $version")
                }
            }

            "update" -> {
                printLine("Hit:1 https://packages.ter.local stable InRelease")
                printLine("Package lists updated.")
            }

            "upgrade" -> {
                printLine("All packages are already up to date in the local demo environment.")
            }

            else -> printLine("pkg: unknown subcommand '${args.first()}'")
        }
    }

    private fun handlePython(args: List<String>) {
        if (!installedPackages.contains("python")) {
            printLine("python: command not found")
            printLine("Install it first with: pkg install python")
            return
        }

        when {
            args.isEmpty() -> {
                printLine("Python 3.12.4")
                printLine("Interactive mode is not implemented yet in this demo build.")
            }
            args.first() == "--version" || args.first() == "-V" -> printLine("Python 3.12.4")
            else -> {
                printLine("python: script execution is not available yet in this demo build.")
            }
        }
    }

    private fun handleGit(args: List<String>) {
        if (!installedPackages.contains("git")) {
            printLine("git: command not found")
            printLine("Install it first with: pkg install git")
            return
        }

        if (args.firstOrNull() == "--version") {
            printLine("git version 2.45.2")
        } else {
            printLine("git is installed, but full repository actions are not implemented yet.")
        }
    }

    private fun handleCurl(args: List<String>) {
        if (!installedPackages.contains("curl")) {
            printLine("curl: command not found")
            printLine("Install it first with: pkg install curl")
            return
        }

        if (args.firstOrNull() == "--version") {
            printLine("curl 8.8.0 (aarch64-linux-android)")
        } else {
            printLine("curl is installed, but network transfers are not implemented yet.")
        }
    }

    private fun handleNano(args: List<String>) {
        if (!installedPackages.contains("nano")) {
            printLine("nano: command not found")
            printLine("Install it first with: pkg install nano")
            return
        }

        if (args.firstOrNull() == "--version") {
            printLine("GNU nano, version 8.0")
        } else {
            printLine("nano is installed, but editing mode is not implemented yet.")
        }
    }

    private fun handleNode(args: List<String>) {
        if (!installedPackages.contains("nodejs")) {
            printLine("node: command not found")
            printLine("Install it first with: pkg install nodejs")
            return
        }

        if (args.firstOrNull() == "--version") {
            printLine("v22.3.0")
        } else {
            printLine("node is installed, but script execution is not implemented yet.")
        }
    }

    private fun handleWhich(args: List<String>) {
        val target = args.firstOrNull()?.lowercase(Locale.getDefault())
        if (target.isNullOrEmpty()) {
            printLine("which: missing command name")
            return
        }

        when (target) {
            "bash" -> printLine("/usr/bin/bash")
            "cat" -> printLine("/usr/bin/cat")
            "echo" -> printLine("/usr/bin/echo")
            "ls" -> printLine("/usr/bin/ls")
            "pwd" -> printLine("/usr/bin/pwd")
            else -> {
                val installedPkg = packageRepo.values.firstOrNull {
                    it.executable == target && installedPackages.contains(it.name)
                }
                if (installedPkg != null) {
                    printLine("/usr/bin/$target")
                } else {
                    printLine("which: no $target in (/usr/bin)")
                }
            }
        }
    }

    private fun printLine(text: String) {
        outputBuffer += text
    }
}
