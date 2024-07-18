class PIPELINE_CONFIG {
    static DEFAULT_AGENT = 'agent'
    static SONAR_TOOL = 'SonarQube'
    static DEBUG_MODE_DEFAULT = false
    static DOCKER_TAG_FORMAT = { build_id, commit -> "${build_id}.${commit.take(7)}" }
    static SELENIUM_JOB = 'selenium'
}