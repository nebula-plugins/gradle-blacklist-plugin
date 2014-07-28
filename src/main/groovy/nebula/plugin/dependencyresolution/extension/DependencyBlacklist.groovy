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
package nebula.plugin.dependencyresolution.extension

import nebula.plugin.dependencyresolution.data.DependencyCoordinates
import nebula.plugin.dependencyresolution.data.DependencyCoordinatesCreator
import nebula.plugin.dependencyresolution.data.DependencyCoordinatesCreatorImpl

class DependencyBlacklist {
    private final Set<DependencyCoordinates> suppressed = new HashSet<DependencyCoordinates>()
    private final Set<DependencyCoordinates> warned = new HashSet<DependencyCoordinates>()
    private final DependencyCoordinatesCreator dependencyCoordinatesCreator = new DependencyCoordinatesCreatorImpl()

    void suppress(String coordinates) {
        suppressed << dependencyCoordinatesCreator.create(coordinates)
    }

    void suppress(Map<String, String> coordinates) {
        suppressed << dependencyCoordinatesCreator.create(coordinates)
    }

    boolean containsSuppressed(DependencyCoordinates target) {
        suppressed.contains(target)
    }

    void warn(String coordinates) {
        warned << dependencyCoordinatesCreator.create(coordinates)
    }

    void warn(Map<String, String> coordinates) {
        warned << dependencyCoordinatesCreator.create(coordinates)
    }

    boolean containsWarned(DependencyCoordinates target) {
        warned.contains(target)
    }

    boolean hasMappings() {
        !suppressed.isEmpty() || !warned.isEmpty()
    }
}