package org.nprog

import com.cloudbees.groovy.cps.NonCPS

class PipelineConfig implements Serializable {
    Map config

    PipelineConfig(Map params, def steps) {
        def yamlConfig = loadYamlConfig(steps)
        this.config = mergeConfigs(yamlConfig, params)
    }

    def getStages() {
        return config.stages
    }

    @NonCPS
    private Map mergeConfigs(Map yamlConfig, Map params) {
        Map result = yamlConfig.clone()
        
        if (params.containsKey('stages')) {
            if (params.appendStages) {
                result.stages = mergeMaps(result.stages ?: [:], params.stages ?: [:])
            } else {
                result.stages = params.stages
            }
        }
        
        // Merge other parameters
        params.each { key, value ->
            if (key != 'stages' && key != 'appendStages') {
                result[key] = value
            }
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
    private Map mergeMaps(Map base, Map override) {
        Map result = new HashMap(base)
        override.each { key, value ->
            if (base[key] instanceof Map && value instanceof Map) {
                result[key] = mergeMaps(base[key], value)
            } else {
                result[key] = value
            }
        }
        return result
    }
}