package org.nprog

import org.yaml.snakeyaml.Yaml

class PipelineConfig {
    Map config

    PipelineConfig(Map params) {
        def configText = libraryResource 'advanced_pipeline_config.yaml'
        def yamlConfig = new Yaml().load(configText)
        this.config = yamlConfig + params
    }

    def getStages() {
        return config.stages
    }

    String toString() {
        return config.toString()
    }
}