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
package nebula.plugin.depres

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult
import org.apache.commons.lang.exception.ExceptionUtils

class DependencyBlacklistIntegrationTest extends IntegrationSpec {
    def setup() {
        buildFile << """
apply plugin: 'dependency-resolution'

configurations {
    myConf
}

dependencies {
    myConf 'com.company:important:1.0'
}
"""
    }

    def "Declares suppressed dependency but it doesn't match any dependency"() {
        when:
        buildFile << """
dependencyResolution {
    blacklist {
        suppress 'my.group:awesome:3.4'
    }
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0""")
    }

    def "Suppressed dependency matches declared dependency"() {
        when:
        buildFile << """
dependencyResolution {
    blacklist {
        suppress 'com.company:important:1.0'
    }
}
"""
        ExecutionResult result = runTasksWithFailure('dependencies')

        then:
        Throwable rootCause = ExceptionUtils.getRootCause(result.failure)
        rootCause.message == "Dependency 'com.company:important:1.0' is blacklisted. Please pick different coordinates."
    }

    def "Declares future blacklisted dependency but it doesn't match any dependency"() {
        when:
        buildFile << """
dependencyResolution {
    blacklist {
        warn 'my.group:awesome:3.4'
    }
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0""")
    }

    def "Future blacklisted dependency matches declared dependency"() {
        when:
        buildFile << """
dependencyResolution {
    blacklist {
        warn 'com.company:important:1.0'
    }
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0""")
        result.standardOutput.contains("Dependency 'com.company:important:1.0' is flagged as potential issue. It might get blacklisted in the future.")
    }
}
