import org.nprog.PipelineConfig
import org.nprog.Logger

def call(Map params) {
    def config = new PipelineConfig(params)
    def logger = new Logger(this)

    pipeline {
        agent any
        options {
            timestamps()
        }
        parameters {
            booleanParam(name: 'DEBUG_MODE', defaultValue: false, description: 'Enable debug logging')
        }
        stages {
            stage('Initialize') {
                steps {
                    script {
                        if (params.DEBUG_MODE) {
                            logger.enableDebug()
                        }
                        logger.info("Initializing pipeline with config: ${config}")
                    }
                }
            }
            stage('Dynamic Stages') {
                steps {
                    script {
                        config.stages.each { stageName, stageConfig ->
                            stage(stageName) {
                                parallel(generateParallelSteps(stageConfig, logger))
                            }
                        }
                    }
                }
            }
        }
        post {
            always {
                script {
                    logger.info("Pipeline completed with status: ${currentBuild.result}")
                }
            }
        }
    }
}

def generateParallelSteps(stageConfig, logger) {
    def parallelSteps = [:]
    stageConfig.each { stepName, stepConfig ->
        parallelSteps[stepName] = {
            script {
                safeExecute(stepName, stepConfig, logger)
            }
        }
    }
    return parallelSteps
}

def safeExecute(stepName, stepConfig, logger) {
    try {
        logger.debug("Executing step: ${stepName}")
        if (stepConfig.containsKey('command')) {
            echo "Executing command: ${stepConfig.command}"
        } else if (stepConfig.containsKey('groovyScript')) {
            echo "Executing Groovy script: ${stepConfig.groovyScript}"
            evaluate(stepConfig.groovyScript)
        }
        logger.info("Step ${stepName} completed successfully")
    } catch (Exception e) {
        logger.error("Step ${stepName} failed: ${e.message}")
        if (stepConfig.failPipeline) {
            error("Step ${stepName} failed and was configured to fail the pipeline")
        }
    }
}