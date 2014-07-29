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
import org.apache.commons.lang.exception.ExceptionUtils
import spock.lang.Unroll

class DependencyBlacklistIntegrationTest extends DependencyResolutionIntegrationSpec {
    @Unroll
    def "Declares suppressed dependency #dependencyNotation with type #type but it doesn't match any dependency"() {
        when:
        buildFile << """
ext.blacklistedDependency = $dependencyNotation as $type

dependencyResolution {
    blacklist {
        suppress blacklistedDependency
    }
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0""")

        where:
        dependencyNotation                                     | type
        "'my.group:awesome:3.4'"                               | 'java.lang.String'
        "[group: 'my.group', name: 'awesome', version: '3.4']" | 'java.util.Map'
    }

    @Unroll
    def "Suppressed dependency #dependencyNotation with type #type matches declared dependency"() {
        when:
        buildFile << """
ext.blacklistedDependency = $dependencyNotation as $type

dependencyResolution {
    blacklist {
        suppress blacklistedDependency
    }
}
"""
        ExecutionResult result = runTasksWithFailure('dependencies')

        then:
        Throwable rootCause = ExceptionUtils.getRootCause(result.failure)
        rootCause.message == "Dependency 'com.company:important:1.0' is blacklisted. Please pick different coordinates."

        where:
        dependencyNotation                                          | type
        "'com.company:important:1.0'"                               | 'java.lang.String'
        "[group: 'com.company', name: 'important', version: '1.0']" | 'java.util.Map'
    }

    @Unroll
    def "Declares future blacklisted dependency #dependencyNotation with type #type but it doesn't match any dependency"() {
        when:
        buildFile << """
ext.blacklistedDependency = $dependencyNotation as $type

dependencyResolution {
    blacklist {
        warn blacklistedDependency
    }
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0""")

        where:
        dependencyNotation                                     | type
        "'my.group:awesome:3.4'"                               | 'java.lang.String'
        "[group: 'my.group', name: 'awesome', version: '3.4']" | 'java.util.Map'
    }

    @Unroll
    def "Future blacklisted dependency #dependencyNotation with type #type matches declared dependency"() {
        when:
        buildFile << """
ext.blacklistedDependency = $dependencyNotation as $type

dependencyResolution {
    blacklist {
        warn blacklistedDependency
    }
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0""")
        result.standardOutput.contains("Dependency 'com.company:important:1.0' is flagged as potential issue. It might get blacklisted in the future.")

        where:
        dependencyNotation                                          | type
        "'com.company:important:1.0'"                               | 'java.lang.String'
        "[group: 'com.company', name: 'important', version: '1.0']" | 'java.util.Map'
    }
}
