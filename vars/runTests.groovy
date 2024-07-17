def call() {
    sh "pip3 install -r requirements.txt"
    sh "python3 -m pytest --cov=. --cov-report xml:test-results/coverage.xml --junitxml=test-results/pytest-report.xml"
}