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

class BlacklistExtension {
    DependencyMapping changingCoordinatesMapping = new DependencyMapping()

    void map(String sourceCoordinates, String targetCoordinates) {
        DependencyCoordinates sourceDependencyCoordinates = createDependencyCoordinates(sourceCoordinates)
        DependencyCoordinates targetDependencyCoordinates = createDependencyCoordinates(targetCoordinates)
        changingCoordinatesMapping.addMapping(sourceDependencyCoordinates, targetDependencyCoordinates)
    }

    void map(Map<String, String> sourceCoordinates, Map<String, String> targetCoordinates) {
        DependencyCoordinates sourceDependencyCoordinates = createDependencyCoordinates(sourceCoordinates)
        DependencyCoordinates targetDependencyCoordinates = createDependencyCoordinates(targetCoordinates)
        changingCoordinatesMapping.addMapping(sourceDependencyCoordinates, targetDependencyCoordinates)
    }

    private DependencyCoordinates createDependencyCoordinates(String coordinates) {
        if(!coordinates) {
            throw new InvalidDependencyDeclarationException('Dependency coordinates cannot be null or empty String')
        }

        String[] attributes = coordinates.split(':')

        if(attributes.size() == 1) {
            return new DependencyCoordinates(attributes[0])
        }
        else if(attributes.size() == 2) {
            return new DependencyCoordinates(attributes[0], attributes[1])
        }
        else if(attributes.size() == 3) {
            return new DependencyCoordinates(attributes[0], attributes[1], attributes[2])
        }

        throw new InvalidDependencyDeclarationException('Dependency coordinates require the following format <group>:<name>:<version>')
    }

    private DependencyCoordinates createDependencyCoordinates(Map<String, String> coordinates) {
        if(!DependencyCoordinates.Notation.allAttributes.containsAll(coordinates.keySet())) {
            throw new InvalidDependencyDeclarationException("Dependency coordinates require at least one of these attributes: 'group', 'name', 'version'")
        }

        new DependencyCoordinates(coordinates[DependencyCoordinates.Notation.GROUP.attribute],
                                  coordinates[DependencyCoordinates.Notation.NAME.attribute],
                                  coordinates[DependencyCoordinates.Notation.VERSION.attribute])
    }
}
