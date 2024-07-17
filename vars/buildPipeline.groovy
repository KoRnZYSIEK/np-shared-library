import org.nprog.Logger

def call(Map config) {
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
                            Logger.enableDebug()
                            Logger.debug("Debug mode enabled")
                        }
                    }
                }
            }
            stage('Get Code') {
                steps {
                    checkout scm
                    Logger.info("Code checkout completed")
                    Logger.debug("Checked out branch: ${env.GIT_BRANCH}")
                }
            }
            stage('Tests and Analysis') {
                parallel {
                    stage('Unit tests') {
                        steps {
                            runTests()
                            Logger.info("Unit tests completed")
                            Logger.debug("Test results saved to test-results/pytest-report.xml")
                        }
                    }
                    stage('Sonarqube analysis') {
                        steps {
                            runSonarAnalysis(env.scannerHome)
                            Logger.info("Sonarqube analysis completed")
                            Logger.debug("Sonarqube scanner home: ${env.scannerHome}")
                        }
                    }
                }
            }
            stage('Build and Push Image') {
                steps {
                    script {
                        def dockerTag = "${env.BUILD_ID}.${env.GIT_COMMIT.take(7)}"
                        Logger.debug("Docker tag: ${dockerTag}")
                        def applicationImage = buildImage(config.imageName, dockerTag)
                        pushToRegistry(applicationImage)
                        Logger.info("Image built and pushed: ${config.imageName}:${dockerTag}")
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
                Logger.info("Pipeline completed successfully")
            }
            failure {
                Logger.error("Pipeline failed")
            }
            always {
                junit testResults: "test-results/*.xml"
                cleanWs()
            }
        }
    }
}