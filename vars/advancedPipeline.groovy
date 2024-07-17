import org.nprog.PipelineConfig
import org.nprog.Logger

def call(Map params) {
    def config = new PipelineConfig(params, this)
    def logger = new Logger(this)

    pipeline {
        agent any
        stages {
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