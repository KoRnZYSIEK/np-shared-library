package org.nprog

class Logger {
    static final String ANSI_RESET = "\u001B[0m"
    static final String ANSI_GREEN = "\u001B[32m"
    static final String ANSI_RED = "\u001B[31m"
    static final String ANSI_BLUE = "\u001B[34m"

    static boolean debugMode = false

    static def info(String message) {
        echo "${ANSI_GREEN}INFO: ${message}${ANSI_RESET}"
    }

    static def error(String message) {
        echo "${ANSI_RED}ERROR: ${message}${ANSI_RESET}"
    }

    static def debug(String message) {
        if (debugMode) {
            echo "${ANSI_BLUE}DEBUG: ${message}${ANSI_RESET}"
        }
    }

    static def enableDebug() {
        debugMode = true
    }
}