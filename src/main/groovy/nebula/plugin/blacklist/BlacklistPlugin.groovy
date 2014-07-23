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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.artifacts.ResolutionStrategy

class BlacklistPlugin implements Plugin<Project> {
    static final String EXTENSION_NAME = 'blacklist'

    @Override
    void apply(Project project) {
        BlacklistExtension extension = project.extensions.create(EXTENSION_NAME, BlacklistExtension)

        project.afterEvaluate {
            changeDependencyCoordinates(project, extension)
        }
    }

    private void changeDependencyCoordinates(Project project, BlacklistExtension extension) {
        project.configurations.all { Configuration configuration ->
            configuration.resolutionStrategy { ResolutionStrategy resolutionStrategy ->
                resolutionStrategy.eachDependency { DependencyResolveDetails details ->
                    useTargetIfMatching(extension, details)
                }
            }
        }
    }

    private void useTargetIfMatching(BlacklistExtension extension, DependencyResolveDetails details) {
        DependencyCoordinates sourceDependencyCoordinates = extension.changingCoordinatesMapping.getMapping(details.requested.group)

        if(sourceDependencyCoordinates) {
            DependencyCoordinates targetDependencyCoordinates = extension.changingCoordinatesMapping.getMapping(sourceDependencyCoordinates)
            Map<String, String> modifiedCoordinates = modifyTargetCoordinates(details, targetDependencyCoordinates)
            details.useTarget modifiedCoordinates
        }
    }

    private Map<String, String> modifyTargetCoordinates(DependencyResolveDetails details, DependencyCoordinates targetDependencyCoordinates) {
        Map modifiedCoordinates = [:]
        modifiedCoordinates[DependencyCoordinates.Notation.GROUP.attribute] = targetDependencyCoordinates.group
        modifiedCoordinates[DependencyCoordinates.Notation.NAME.attribute] = targetDependencyCoordinates.name ?: details.requested.name
        modifiedCoordinates[DependencyCoordinates.Notation.VERSION.attribute] = targetDependencyCoordinates.version ?: details.requested.version
        modifiedCoordinates
    }
}
