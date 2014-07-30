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
import spock.lang.Unroll

class DependencyBundleIntegrationTest extends DependencyResolutionIntegrationSpec {
    @Unroll
    def "Declares bundle #sourceBundle with type #type but it doesn't match any dependency"() {
        when:
        buildFile << """
ext.sourceBundle = $sourceBundle as $type

dependencyResolution {
    bundle {
        replace sourceBundle, ['my.group:awesome:3.6', 'org.company:piece1:1.2', 'com.enterprise:piece2:0.5']
    }
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0""")

        where:
        sourceBundle                                           | type
        "'my.group:awesome:3.4'"                               | 'java.lang.String'
        "[group: 'my.group', name: 'awesome', version: '3.4']" | 'java.util.Map'
    }

    @Unroll
    def "Declares bundle #sourceBundle with type #sourceType for matching dependency"() {
        when:
        buildFile << """
ext.sourceBundle = $sourceBundle as $sourceType
ext.targets = $targets as $targetType

dependencyResolution {
    bundle {
        replace sourceBundle, targets
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

        where:
        sourceBundle                                                | sourceType         | targets                                                                                                                                                                     | targetType
        "'com.company:important:1.0'"                               | 'java.lang.String' | "['my.group:awesome:3.6', 'org.company:piece1:1.2', 'com.enterprise:piece2:0.5']"                                                                                           | 'java.util.Collection'
        "[group: 'com.company', name: 'important', version: '1.0']" | 'java.util.Map'    | "[[group: 'my.group', name: 'awesome', version: '3.6'], [group: 'org.company', name: 'piece1', version: '1.2'], [group: 'com.enterprise', name: 'piece2', version: '0.5']]" | 'java.util.Collection'
    }
}
