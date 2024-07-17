import org.nprog.PipelineConfig
import org.nprog.Logger

def call(Map params) {
    def config = new PipelineConfig(params, this)
    def logger = new Logger(this)

    if (params.DEBUG_MODE) {
        logger.enableDebug()
    }

    config.stages.each { stageName, stageConfig ->
        stage(stageName) {
            stageConfig.each { stepName, stepConfig ->
                if (stepConfig.command) {
                    echo "Executing command: ${stepConfig.command}"
                    sh stepConfig.command
                } else if (stepConfig.groovyScript) {
                    echo "Executing Groovy script for step: ${stepName}"
                    script {
                        evaluate(stepConfig.groovyScript)
                    }
                }
                if (stepConfig.failPipeline) {
                    error("Step ${stepName} is configured to fail the pipeline")
                }
            }
        }
    }
}