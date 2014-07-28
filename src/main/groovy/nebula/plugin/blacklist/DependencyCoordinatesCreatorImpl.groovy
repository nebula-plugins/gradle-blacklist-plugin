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

class DependencyCoordinatesCreatorImpl implements DependencyCoordinatesCreator {
    @Override
    DependencyCoordinates create(String coordinates) {
        if(!coordinates) {
            throw new InvalidDependencyDeclarationException('Dependency coordinates cannot be empty String')
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

    @Override
    DependencyCoordinates create(Map<String, String> coordinates) {
        if(!coordinates) {
            throw new InvalidDependencyDeclarationException('Dependency coordinates cannot be null')
        }

        if(!DependencyCoordinates.Notation.allAttributes.containsAll(coordinates.keySet())) {
            throw new InvalidDependencyDeclarationException("Dependency coordinates require at least one of these attributes: 'group', 'name', 'version'")
        }

        new DependencyCoordinates(coordinates[DependencyCoordinates.Notation.GROUP.attribute],
                                  coordinates[DependencyCoordinates.Notation.NAME.attribute],
                                  coordinates[DependencyCoordinates.Notation.VERSION.attribute])
    }
}
