[
    DEFAULT_AGENT: 'agent',
    SONAR_TOOL: 'SonarQube',
    DEBUG_MODE_DEFAULT: false,
    DOCKER_TAG_FORMAT: { build_id, commit -> "${build_id}.${commit.take(7)}" },
    SELENIUM_JOB: 'selenium'
]