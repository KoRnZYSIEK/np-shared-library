import org.nprog.Logger

def call(Map config) {
    def logger = new Logger()

    pipeline {
        agent { label 'agent' }
        environment {
            scannerHome = tool 'SonarQube'
            PIP_BREAK_SYSTEM_PACKAGES = 1
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
                            logger.debug("Debug mode enabled")
                        }
                    }
                }
            }
            stage('Get Code') {
                steps {
                    checkout scm
                    logger.info("Code checkout completed")
                    logger.debug("Checked out branch: ${env.GIT_BRANCH}")
                }
            }
            stage('Tests and Analysis') {
                parallel {
                    stage('Unit tests') {
                        steps {
                            runTests()
                            logger.info("Unit tests completed")
                            logger.debug("Test results saved to test-results/pytest-report.xml")
                        }
                    }
                    stage('Sonarqube analysis') {
                        steps {
                            runSonarAnalysis(env.scannerHome)
                            logger.info("Sonarqube analysis completed")
                            logger.debug("Sonarqube scanner home: ${env.scannerHome}")
                        }
                    }
                }
            }
            stage('Build and Push Image') {
                steps {
                    script {
                        def dockerTag = "${env.BUILD_ID}.${env.GIT_COMMIT.take(7)}"
                        logger.debug("Docker tag: ${dockerTag}")
                        def applicationImage = buildImage(config.imageName, dockerTag)
                        pushToRegistry(applicationImage)
                        logger.info("Image built and pushed: ${config.imageName}:${dockerTag}")
                    }
                }
            }
        }
        post {
            success {
                build job: 'selenium',
                      parameters: [
                          string(name: 'frontendDockerTag', value: dockerTag)
                      ],
                      wait: false
                logger.info("Pipeline completed successfully")
            }
            failure {
                logger.error("Pipeline failed")
            }
            always {
                junit testResults: "test-results/*.xml"
                cleanWs()
            }
        }
    }
}