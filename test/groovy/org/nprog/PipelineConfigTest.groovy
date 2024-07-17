package org.nprog

import org.junit.Test
import static org.junit.Assert.*

class PipelineConfigTest {
    @Test
    void testConfigMerging() {
        def params = [customParam: 'value']
        def config = new PipelineConfig(params)
        
        assertNotNull(config.stages)
        assertEquals('value', config.config.customParam)
    }
}