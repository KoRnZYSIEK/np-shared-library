import org.nprog.Logger

def call(Map config) {
    def logger = new Logger(this)
    pipeline {
        agent { label PIPELINE_CONFIG.DEFAULT_AGENT }
        environment {
            scannerHome = tool PIPELINE_CONFIG.SONAR_TOOL
            PIP_BREAK_SYSTEM_PACKAGES = 1
        }
        parameters {
            booleanParam(name: 'DEBUG_MODE', defaultValue: PIPELINE_CONFIG.DEBUG_MODE_DEFAULT, description: 'Enable debug logging')
        }
        stages {
            stage('Initialize') {
                steps {
                    script {
                        if (params.DEBUG_MODE) {
                            logger.enableDebug()
                            logger.debug('Debug mode enabled')
                        }
                    }
                }
            }
            stage('Get Code') {
                steps {
                    checkout scm
                    script {
                        logger.info('Code checkout completed')
                        logger.debug("Checked out branch: ${env.GIT_BRANCH}")
                    }
                }
            }
            stage('Tests and Analysis') {
                parallel {
                    stage('Unit tests') {
                        steps {
                            runTests()
                            script {
                                logger.info('Unit tests completed')
                                logger.debug('Test results saved to test-results/pytest-report.xml')
                            }
                        }
                    }
                    stage('Sonarqube analysis') {
                        steps {
                            runSonarAnalysis(env.scannerHome)
                            script {
                                logger.info('Sonarqube analysis completed')
                                logger.debug("Sonarqube scanner home: ${env.scannerHome}")
                            }
                        }
                    }
                }
            }
            stage('Build and Push Image') {
                steps {
                    script {
                        dockerTag = PIPELINE_CONFIG.DOCKER_TAG_FORMAT(env.BUILD_ID, env.GIT_COMMIT)
                        logger.debug("Docker tag: ${dockerTag}")
                        applicationImage = buildImage(config.imageName, dockerTag)
                        pushToRegistry(applicationImage)
                        logger.info("Image built and pushed: ${config.imageName}:${dockerTag}")
                    }
                }
            }
        }
        post {
            success {
                build job: PIPELINE_CONFIG.SELENIUM_JOB,
                      parameters: [
                          string(name: 'frontendDockerTag', value: dockerTag)
                      ],
                      wait: false
                script{
                    logger.info('Pipeline completed successfully')
                }
            }
            failure {
                script {
                    logger.error('Pipeline failed')
                }
            }
            always {
                junit testResults: 'test-results/*.xml'
                cleanWs()
            }
        }
    }
}