package org.nprog

import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test

class AdvancedPipelineTest extends BasePipelineTest {
    
    @Override
    @Before
    void setUp() throws Exception {
        super.setUp()
        
        // Register the libraryResource step to return the YAML configuration
        helper.registerAllowedMethod('libraryResource', [String.class], { String resource ->
            return """
stages:
  Build:
    compile:
      command: "echo 'Compiling the project'"
    staticAnalysis:
      command: "echo 'Running static analysis'"
  Test:
    unitTests:
      command: "echo 'Running unit tests'"
    integrationTests:
      command: "echo 'Running integration tests'"
  Deploy:
    deployToStaging:
      groovyScript: |
        def version = "1.0.0" // Simulating version retrieval
        echo "Deploying version \${version} to staging"
    smokeTest:
      command: "echo 'Running smoke tests on staging environment'"
            """
        })
        
        // Mock the sh step
        helper.registerAllowedMethod('sh', [String.class], { String command ->
            println "Simulated command: ${command}"
        })
        
        // Mock the echo step
        helper.registerAllowedMethod('echo', [String.class], { String message ->
            println message
        })
        
        // Mock the error step
        helper.registerAllowedMethod('error', [String.class], { String message ->
            throw new Exception(message)
        })

        // Mock pipeline steps
        helper.registerAllowedMethod('stage', [String.class, Closure.class], null)
        helper.registerAllowedMethod('script', [Closure.class], null)
    }

    @Test
    void testAdvancedPipeline() {
        def script = loadScript('vars/advancedPipeline.groovy')
        def params = [
            DEBUG_MODE: true,
            stages: [
                Build: [
                    compile: [command: 'echo "Compiling the project"', failPipeline: false],
                    staticAnalysis: [command: 'echo "Running static analysis"', failPipeline: false]
                ],
                Test: [
                    unitTests: [command: 'echo "Running unit tests"', failPipeline: false],
                    integrationTests: [command: 'echo "Running integration tests"', failPipeline: false]
                ],
                Deploy: [
                    deployToStaging: [groovyScript: 'def version = "1.0.0"; echo "Deploying version ${version} to staging"', failPipeline: false],
                    smokeTest: [command: 'echo "Running smoke tests on staging environment"', failPipeline: false]
                ]
            ]
        ]
       
        script.call(params)
        
        // Validate the outputs, errors, and debug messages if necessary
        printCallStack()
        
        // Assert that all stages were executed
        assertJobStatusSuccess()
    }
}