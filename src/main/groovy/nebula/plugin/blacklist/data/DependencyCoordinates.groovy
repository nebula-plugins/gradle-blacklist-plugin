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
package nebula.plugin.blacklist.data

class DependencyCoordinates {
    final String group
    final String name
    final String version

    DependencyCoordinates(String group, String name = null, String version = null) {
        assert group != null, 'Group attribute may not be null'
        this.group = group
        this.name = name
        this.version = version
    }

    @Override
    String toString() {
        StringBuilder coordinatesString = new StringBuilder()
        coordinatesString <<= group
        coordinatesString <<= ':'

        if(name) {
            coordinatesString <<= name
        }

        coordinatesString <<= ':'

        if(version) {
            coordinatesString <<= version
        }

        coordinatesString.toString()
    }

    static enum Notation {
        GROUP('group'), NAME('name'), VERSION('version')

        private final String attribute

        private Notation(String attribute) {
            this.attribute = attribute
        }

        String getAttribute() {
            return attribute
        }

        static List<String> getAllAttributes() {
            values().collect { it.attribute }
        }
    }
}
