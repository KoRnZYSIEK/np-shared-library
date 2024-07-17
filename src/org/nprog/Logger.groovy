package org.nprog

class Logger {
    static final String ANSI_RESET = "\u001B[0m"
    static final String ANSI_GREEN = "\u001B[32m"
    static final String ANSI_RED = "\u001B[31m"
    static final String ANSI_BLUE = "\u001B[34m"
    
    boolean debugMode = false
    def steps

    Logger(steps) {
        this.steps = steps
    }

    void info(String message) {
        steps.echo "${ANSI_GREEN}INFO: ${message}${ANSI_RESET}"
    }

    void error(String message) {
        steps.echo "${ANSI_RED}ERROR: ${message}${ANSI_RESET}"
    }

    void debug(String message) {
        if (debugMode) {
            steps.echo "${ANSI_BLUE}DEBUG: ${message}${ANSI_RESET}"
        }
    }

    void enableDebug() {
        debugMode = true
    }
}