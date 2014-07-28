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
package nebula.plugin.dependencyresolution.data

class DependencyMapping {
    private final Map<DependencyCoordinates, DependencyCoordinates> mappings = new HashMap<DependencyCoordinates, DependencyCoordinates>()

    void addMapping(DependencyCoordinates source, DependencyCoordinates target) {
        mappings[source] = target
    }

    boolean hasMapping(DependencyCoordinates source) {
        mappings.containsKey(source)
    }

    DependencyCoordinates getMapping(String sourceGroup) {
        mappings.keySet().find { it.group == sourceGroup }
    }

    DependencyCoordinates getMapping(DependencyCoordinates source) {
        mappings[source]
    }

    boolean hasMappings() {
        !mappings.isEmpty()
    }
}
