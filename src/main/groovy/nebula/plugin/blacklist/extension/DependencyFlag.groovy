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

class DependencyFlag {
    private final Set<DependencyCoordinates> blocked = new HashSet<DependencyCoordinates>()
    private final Set<DependencyCoordinates> warned = new HashSet<DependencyCoordinates>()
    private final DependencyCoordinatesCreator dependencyCoordinatesCreator = new DependencyCoordinatesCreatorImpl()

    void block(String coordinates) {
        blocked << dependencyCoordinatesCreator.create(coordinates)
    }

    void block(Map<String, String> coordinates) {
        blocked << dependencyCoordinatesCreator.create(coordinates)
    }

    boolean containsBlocked(DependencyCoordinates target) {
        blocked.contains(target)
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
        !blocked.isEmpty() || !warned.isEmpty()
    }
}
