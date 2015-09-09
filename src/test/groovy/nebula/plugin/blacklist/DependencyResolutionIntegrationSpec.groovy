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

class DependencyResolutionIntegrationSpec extends IntegrationSpec {
    def setup() {
        buildFile << """
apply plugin: 'nebula.blacklist'

configurations {
    myConf
}

dependencies {
    myConf 'com.company:important:1.0'
}
"""
    }
}
