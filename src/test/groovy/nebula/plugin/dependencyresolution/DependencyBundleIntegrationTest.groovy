/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nebula.plugin.dependencyresolution

import nebula.test.functional.ExecutionResult

class DependencyBundleIntegrationTest extends DependencyResolutionIntegrationSpec {
    def "Declares bundle but it doesn't match any dependency"() {
        when:
        buildFile << """
dependencyResolution {
    bundle {
        replace 'my.group:awesome:3.4', ['my.group:awesome:3.6', 'org.company:piece1:1.2', 'com.enterprise:piece2:0.5']
    }
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0""")
    }

    def "Declares bundle replacement for matching dependency"() {
        when:
        buildFile << """
dependencyResolution {
    bundle {
        replace 'com.company:important:1.0', ['my.group:awesome:3.6', 'org.company:piece1:1.2', 'com.enterprise:piece2:0.5']
    }
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
+--- my.group:awesome:3.6 FAILED
+--- org.company:piece1:1.2 FAILED
\\--- com.enterprise:piece2:0.5 FAILED""")
        !result.standardOutput.contains('com.company:important:1.0')
    }
}
