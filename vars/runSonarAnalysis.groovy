def call(String scannerHome) {
    withSonarQubeEnv('SonarQube') {
        sh "${scannerHome}/bin/sonar-scanner"
    }
}