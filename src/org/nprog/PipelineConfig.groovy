package org.nprog

import com.cloudbees.groovy.cps.NonCPS

class PipelineConfig implements Serializable {
    Map config

    PipelineConfig(Map params, def steps) {
        def yamlConfig = loadYamlConfig(steps)
        this.config = mergeMaps(yamlConfig, params.appendStages ? params : overrideStages(params))
    }

    def getStages() {
        return config.stages
    }

    @NonCPS
    private Map overrideStages(Map params) {
        def result = params.clone()
        if (params.containsKey('stages')) {
            result.stages = params.stages
        }
        return result
    }

    @NonCPS
    private Map loadYamlConfig(def steps) {
        def yaml = new org.yaml.snakeyaml.Yaml()
        def configText = steps.libraryResource('advanced_pipeline_config.yaml')
        return yaml.load(configText)
    }

    @NonCPS
    private Map mergeMaps(Map... maps) {
        Map result = [:]
        maps.each { map ->
            result = mergeMapRecursive(result, map)
        }
        return result
    }

    @NonCPS
    private Map mergeMapRecursive(Map base, Map override) {
        Map result = new HashMap(base)
        override.each { key, value ->
            if (base[key] instanceof Map && value instanceof Map) {
                result[key] = mergeMapRecursive(base[key], value)
            } else {
                result[key] = value
            }
        }
        return result
    }
}