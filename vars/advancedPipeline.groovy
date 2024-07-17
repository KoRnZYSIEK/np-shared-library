import org.nprog.PipelineConfig
import org.nprog.Logger

def call(Map params) {
    def config
    def logger

    pipeline {
        agent any

        stages {
            stage('Initialize') {
                steps {
                    script {
                        config = new PipelineConfig(params, this)
                        logger = new Logger(this)
                    }
                }
            }

            stage('Dynamic Stages') {
                steps {
                    script {
                        config.stages.each { stageName, stageConfig ->
                            stage(stageName) {
                                stageConfig.each { stepName, stepConfig ->
                                    echo "Executing ${stepName}: ${stepConfig.command}"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}