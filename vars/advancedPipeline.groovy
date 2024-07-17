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