pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                // Checkout the repository containing the Jenkinsfile and the shared library code
                checkout scm
            }
        }

        stage('Test') {
            steps {
                script {
                    // Run the tests using Gradle
                    sh 'gradle test'
                }
            }
        }

        stage('Archive Results') {
            steps {
                script {
                    // Archive the test results
                    junit 'build/test-results/test/*.xml'
                    // Archive the HTML report
                    archiveArtifacts artifacts: 'build/reports/tests/test/index.html', allowEmptyArchive: true
                }
            }
        }
    }

    post {
        always {
            script {
                // Clean up workspace
                cleanWs()
            }
        }
    }
}
