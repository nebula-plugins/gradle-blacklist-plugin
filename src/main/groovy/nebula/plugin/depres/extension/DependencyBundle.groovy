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

class DependencyBundle {
    private final Map<DependencyCoordinates, Set<DependencyCoordinates>> components = new HashMap<DependencyCoordinates, Set<DependencyCoordinates>>()
    private final DependencyCoordinatesCreator dependencyCoordinatesCreator = new DependencyCoordinatesCreatorImpl()

    void replace(String source, Collection componentCoordinates) {
        DependencyCoordinates sourceCoordinates = dependencyCoordinatesCreator.create(source)

        componentCoordinates.each { coordinates ->
            addComponent(sourceCoordinates, dependencyCoordinatesCreator.create(coordinates))
        }
    }

    private void addComponent(DependencyCoordinates source, DependencyCoordinates target) {
        if(!components.containsKey(source)) {
            components[source] = [target] as Set
        }

        components[source] << target
    }

    Map<DependencyCoordinates, Set<DependencyCoordinates>> getComponents() {
        components
    }

    boolean hasMappings() {
        !components.isEmpty()
    }
}
