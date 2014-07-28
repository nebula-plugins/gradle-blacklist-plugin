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
package nebula.plugin.depres.extension

import nebula.plugin.depres.data.DependencyCoordinates
import nebula.plugin.depres.data.DependencyCoordinatesCreator
import nebula.plugin.depres.data.DependencyCoordinatesCreatorImpl
import nebula.plugin.depres.data.DependencyMapping

class DependencyTranslation {
    private final DependencyMapping changingCoordinatesMapping = new DependencyMapping()
    private final DependencyCoordinatesCreator dependencyCoordinatesCreator = new DependencyCoordinatesCreatorImpl()

    void map(String sourceCoordinates, String targetCoordinates) {
        DependencyCoordinates sourceDependencyCoordinates = dependencyCoordinatesCreator.create(sourceCoordinates)
        DependencyCoordinates targetDependencyCoordinates = dependencyCoordinatesCreator.create(targetCoordinates)
        changingCoordinatesMapping.addMapping(sourceDependencyCoordinates, targetDependencyCoordinates)
    }

    void map(Map<String, String> sourceCoordinates, Map<String, String> targetCoordinates) {
        DependencyCoordinates sourceDependencyCoordinates = dependencyCoordinatesCreator.create(sourceCoordinates)
        DependencyCoordinates targetDependencyCoordinates = dependencyCoordinatesCreator.create(targetCoordinates)
        changingCoordinatesMapping.addMapping(sourceDependencyCoordinates, targetDependencyCoordinates)
    }
}
