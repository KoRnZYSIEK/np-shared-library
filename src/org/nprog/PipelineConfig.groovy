package org.nprog

class PipelineConfig {
    Map config

    PipelineConfig(Map params, def steps) {
        def yamlConfig = loadYamlConfig(steps)
        this.config = yamlConfig + params
    }

    def getStages() {
        return config.stages
    }

    String toString() {
        return config.toString()
    }

    private loadYamlConfig(steps) {
        def yaml = new org.yaml.snakeyaml.Yaml()
        def configText = steps.libraryResource('advanced_pipeline_config.yaml')
        return yaml.load(configText)
    }
}