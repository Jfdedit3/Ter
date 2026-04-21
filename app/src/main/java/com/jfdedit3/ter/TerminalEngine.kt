package com.jfdedit3.ter

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TerminalEngine {
    private val commandHistory = mutableListOf<String>()
    private val outputBuffer = mutableListOf<String>()

    init {
        printLine("Welcome to Ter")
        printLine("A lightweight Android terminal-style app inspired by Termux.")
        printLine("Type 'help' to list commands.")
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
            "help" -> {
                printLine("Available commands:")
                printLine("help      - show available commands")
                printLine("clear     - clear the terminal output")
                printLine("echo      - print text")
                printLine("date      - show current date and time")
                printLine("whoami    - show current shell user")
                printLine("uname     - show device shell info")
                printLine("pwd       - print working directory")
                printLine("ls        - list demo folders")
                printLine("history   - show entered commands")
                printLine("about     - app information")
                printLine("pkg       - placeholder package manager command")
            }

            "clear" -> {
                outputBuffer.clear()
            }

            "echo" -> printLine(args.joinToString(" "))

            "date" -> {
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                printLine(format.format(Date()))
            }

            "whoami" -> printLine("shell")

            "uname" -> printLine("Linux localhost 5.x android-ter aarch64")

            "pwd" -> printLine("/data/data/com.jfdedit3.ter/home")

            "ls" -> {
                printLine("bin")
                printLine("home")
                printLine("usr")
                printLine("tmp")
            }

            "history" -> {
                if (commandHistory.isEmpty()) {
                    printLine("No history yet.")
                } else {
                    commandHistory.forEachIndexed { index, value ->
                        printLine("${index + 1}  $value")
                    }
                }
            }

            "about" -> {
                printLine("Ter v1.0.0")
                printLine("Terminal-style Android app skeleton.")
                printLine("Ready to be extended with a real shell backend.")
            }

            "pkg" -> {
                printLine("pkg is not connected to a real package manager yet.")
                printLine("Next step: bind commands to a native shell/service layer.")
            }

            else -> {
                printLine("Command not found: $command")
                printLine("Type 'help' to list available commands.")
            }
        }

        return outputBuffer.joinToString("\n")
    }

    private fun printLine(text: String) {
        outputBuffer += text
    }
}
