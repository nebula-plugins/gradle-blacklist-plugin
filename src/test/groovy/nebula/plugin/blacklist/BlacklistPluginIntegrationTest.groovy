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
package nebula.plugin.blacklist

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult
import org.apache.commons.lang.exception.ExceptionUtils
import spock.lang.Unroll

class BlacklistPluginIntegrationTest extends IntegrationSpec {
    def "Declares change mapping but it doesn't match any dependency"() {
        when:
        buildFile << """
apply plugin: 'blacklist'

blacklist {
    map('my.group', 'some.other.group')
}

configurations {
    myConf
}

dependencies {
    myConf 'com.company:important:1.0'
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        !result.standardOutput.contains('some.other.group')
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0""")
    }

    def "Throws exception if change mapping uses empty String attributes"() {
        when:
        buildFile << """
apply plugin: 'blacklist'

blacklist {
    map('', '')
}

configurations {
    myConf
}

dependencies {
    myConf 'com.company:important:1.0'
}
"""
        ExecutionResult result = runTasksWithFailure('dependencies')

        then:
        Throwable rootCause = ExceptionUtils.getRootCause(result.failure)
        rootCause.message == "Dependency coordinates cannot be null or empty String"
    }

    def "Throws exception if change mapping uses invalid String attributes"() {
        when:
        buildFile << """
apply plugin: 'blacklist'

blacklist {
    map('my.group', 'some.other.group:changed:5.5:sources')
}

configurations {
    myConf
}

dependencies {
    myConf 'com.company:important:1.0'
}
"""
        ExecutionResult result = runTasksWithFailure('dependencies')

        then:
        Throwable rootCause = ExceptionUtils.getRootCause(result.failure)
        rootCause.message == "Dependency coordinates require the following format <group>:<name>:<version>"
    }

    def "Throws exception if change mapping uses incorrect Map attributes"() {
        when:
        buildFile << """
apply plugin: 'blacklist'

ext.sourceCoordinates = [unknownAttribute: '1'] as Map<String, String>
ext.targetCoordinates = [some: 'attribute'] as Map<String, String>

blacklist {
    map(sourceCoordinates, targetCoordinates)
}

configurations {
    myConf
}

dependencies {
    myConf 'com.company:important:1.0'
}
"""
        ExecutionResult result = runTasksWithFailure('dependencies')

        then:
        Throwable rootCause = ExceptionUtils.getRootCause(result.failure)
        rootCause.message == "Dependency coordinates require at least one of these attributes: 'group', 'name', 'version'"
    }

    @Unroll
    def "Declares change mapping for matching String coordinates ('#targetCoordinates')"() {
        when:
        buildFile << """
apply plugin: 'blacklist'

blacklist {
    map('$sourceCoordinates', '$targetCoordinates')
}

configurations {
    myConf
}

dependencies {
    myConf 'com.company:important:1.0'
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0 -> $modifiedCoordinates""")

        where:
        sourceCoordinates  | targetCoordinates               | modifiedCoordinates
        'com.company'      | 'some.other.group'              | 'some.other.group:important:1.0'
        'com.company'      | 'some.other.group:changed'      | 'some.other.group:changed:1.0'
        'com.company'      | 'some.other.group:changed:2.0'  | 'some.other.group:changed:2.0'
        'com.company'      | 'some.other.group::2.0'         | 'some.other.group:important:2.0'
    }

    @Unroll
    def "Declares change mapping for matching Map coordinates ('#targetCoordinates')"() {
        when:
        buildFile << """
apply plugin: 'blacklist'

ext.sourceCoordinates = [$sourceCoordinates] as Map<String, String>
ext.targetCoordinates = [$targetCoordinates] as Map<String, String>

blacklist {
    map(sourceCoordinates, targetCoordinates)
}

configurations {
    myConf
}

dependencies {
    myConf 'com.company:important:1.0'
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0 -> $modifiedCoordinates""")

        where:
        sourceCoordinates       | targetCoordinates                                             | modifiedCoordinates
        "group: 'com.company'"  | "group: 'some.other.group'"                                   | 'some.other.group:important:1.0'
        "group: 'com.company'"  | "group: 'some.other.group', name: 'changed'"                  | 'some.other.group:changed:1.0'
        "group: 'com.company'"  | "group: 'some.other.group', name: 'changed', version: '2.0'"  | 'some.other.group:changed:2.0'
        "group: 'com.company'"  | "group: 'some.other.group', version: '2.0'"                   | 'some.other.group:important:2.0'
    }
}
