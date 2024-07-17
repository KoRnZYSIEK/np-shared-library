package org.nprog

import com.cloudbees.groovy.cps.NonCPS

class PipelineConfig implements Serializable {
    Map config

    PipelineConfig(Map params, def steps) {
        def yamlConfig = loadYamlConfig(steps)
        this.config = mergeMaps(yamlConfig, params)
    }

    def getStages() {
        return config.stages
    }

    @NonCPS
    String toString() {
        return config.toString()
    }

    @NonCPS
    private Map loadYamlConfig(def steps) {
        if (steps) {
            def yaml = new org.yaml.snakeyaml.Yaml()
            def configText = steps.libraryResource('advanced_pipeline_config.yaml')
            return yaml.load(configText)
        } else {
            return [:]
        }
    }

    @NonCPS
    private Map mergeMaps(Map... maps) {
        Map result = [:]
        maps.each { map ->
            result.putAll(map)
        }
        return result
    }
}