import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test
import static com.lesfurets.jenkins.unit.MethodCall.callArgsToString
import static org.junit.Assert.assertTrue

class AdvancedPipelineTest extends BasePipelineTest {
    def advancedPipeline

    @Override
    @Before
    void setUp() throws Exception {
        super.setUp()
        // Mock libraryResource
        helper.registerAllowedMethod('libraryResource', [String.class], { fileName ->
            return '''
stages:
  Build:
    compile:
      command: "echo 'Compiling'"
  Test:
    unitTest:
      command: "echo 'Running unit tests'"
'''
        })
        // Load the script
        advancedPipeline = loadScript("vars/advancedPipeline.groovy")
        
        // Mock pipeline steps
        helper.registerAllowedMethod('pipeline', [Closure.class], null)
        helper.registerAllowedMethod('agent', [Closure.class], null)
        helper.registerAllowedMethod('stages', [Closure.class], null)
        helper.registerAllowedMethod('steps', [Closure.class], null)
        helper.registerAllowedMethod('script', [Closure.class], { c -> c() })
        helper.registerAllowedMethod('stage', [String.class, Closure.class], { s, c -> c() })
        helper.registerAllowedMethod('echo', [String.class], { str -> println str })
    }

    @Test
    void testAdvancedPipeline() {
        def config = [
            customParam: 'customValue'
        ]

        // When
        advancedPipeline(config)

        // Then
        printCallStack()
        assertTrue(helper.callStack.findAll { call ->
            call.methodName == "echo"
        }.any { call ->
            callArgsToString(call).contains("Executing compile: echo 'Compiling'")
        })
        assertTrue(helper.callStack.findAll { call ->
            call.methodName == "echo"
        }.any { call ->
            callArgsToString(call).contains("Executing unitTest: echo 'Running unit tests'")
        })
    }
}