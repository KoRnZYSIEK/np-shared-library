package org.nprog

class PipelineConfig {
    Map config

    PipelineConfig(Map params, def steps = null) {
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
        if (steps) {
            def yaml = new org.yaml.snakeyaml.Yaml()
            def configText = steps.libraryResource('advanced_pipeline_config.yaml')
            return yaml.load(configText)
        } else {
            // Return an empty map if steps is not provided (for testing purposes)
            return [:]
        }
    }
}