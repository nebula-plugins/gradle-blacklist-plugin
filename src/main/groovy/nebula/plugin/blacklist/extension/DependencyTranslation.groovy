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
package nebula.plugin.blacklist.extension

import nebula.plugin.blacklist.data.DependencyCoordinates
import nebula.plugin.blacklist.data.DependencyCoordinatesCreator
import nebula.plugin.blacklist.data.DependencyCoordinatesCreatorImpl

class DependencyTranslation {
    private final Map<DependencyCoordinates, DependencyCoordinates> changingCoordinatesMapping = new HashMap<DependencyCoordinates, DependencyCoordinates>()
    private final DependencyCoordinatesCreator dependencyCoordinatesCreator = new DependencyCoordinatesCreatorImpl()

    void map(String sourceCoordinates, String targetCoordinates) {
        DependencyCoordinates sourceDependencyCoordinates = dependencyCoordinatesCreator.create(sourceCoordinates)
        DependencyCoordinates targetDependencyCoordinates = dependencyCoordinatesCreator.create(targetCoordinates)
        addMapping(sourceDependencyCoordinates, targetDependencyCoordinates)
    }

    void map(Map<String, String> sourceCoordinates, Map<String, String> targetCoordinates) {
        DependencyCoordinates sourceDependencyCoordinates = dependencyCoordinatesCreator.create(sourceCoordinates)
        DependencyCoordinates targetDependencyCoordinates = dependencyCoordinatesCreator.create(targetCoordinates)
        addMapping(sourceDependencyCoordinates, targetDependencyCoordinates)
    }

    private void addMapping(DependencyCoordinates source, DependencyCoordinates target) {
        changingCoordinatesMapping[source] = target
    }

    DependencyCoordinates getMapping(String sourceGroup, String sourceName, String sourceVersion) {
        changingCoordinatesMapping.keySet().find { it.group == sourceGroup && (it.name == null || it.name == sourceName) && (it.version == null || it.version == sourceVersion)}
    }

    DependencyCoordinates getMapping(DependencyCoordinates source) {
        changingCoordinatesMapping[source]
    }

    boolean hasMappings() {
        !changingCoordinatesMapping.isEmpty()
    }
}
