pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare') {
            steps {
                sh '''
                    wget https://repo1.maven.org/maven2/com/lesfurets/jenkins-pipeline-unit/1.9/jenkins-pipeline-unit-1.9.jar
                    wget https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar
                    wget https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar
                    wget https://repo1.maven.org/maven2/org/codehaus/groovy/groovy-all/2.4.21/groovy-all-2.4.21.jar
                    wget https://repo1.maven.org/maven2/org/yaml/snakeyaml/1.29/snakeyaml-1.29.jar
                '''
            }
        }

        stage('Run Tests') {
            steps {
                sh '''
                    groovy -cp jenkins-pipeline-unit-1.9.jar:junit-4.13.2.jar:hamcrest-core-1.3.jar:groovy-all-2.4.21.jar:snakeyaml-1.29.jar \
                    test/AdvancedPipelineTest.groovy
                '''
            }
        }
    }

    post {
        always {
            junit 'target/test-reports/*.xml'
        }
    }
}