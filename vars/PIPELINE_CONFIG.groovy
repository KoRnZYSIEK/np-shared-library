class PIPELINE_CONFIG {
    static final DEFAULT_AGENT = 'agent'
    static final SONAR_TOOL = 'SonarQube'
    static final DEBUG_MODE_DEFAULT = false
    static final DOCKER_TAG_FORMAT = { build_id, commit -> "${build_id}.${commit.take(7)}" }
    static final SELENIUM_JOB = 'selenium'
}