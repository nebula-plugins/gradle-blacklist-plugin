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
package nebula.plugin.dependencyresolution

import nebula.plugin.dependencyresolution.data.DependencyCoordinates
import nebula.plugin.dependencyresolution.exception.BlacklistedDependencyDeclarationException
import nebula.plugin.dependencyresolution.extension.DependencyResolutionExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ResolutionStrategy

class DependencyResolutionPlugin implements Plugin<Project> {
    static final String EXTENSION_NAME = 'dependencyResolution'

    @Override
    void apply(Project project) {
        DependencyResolutionExtension extension = project.extensions.create(EXTENSION_NAME, DependencyResolutionExtension)

        project.afterEvaluate {
            changeDependencyCoordinates(project, extension)
            backlistDependencies(project, extension)
            replaceBundles(project, extension)
        }
    }

    private void changeDependencyCoordinates(Project project, DependencyResolutionExtension extension) {
        if(extension.translation.changingCoordinatesMapping.hasMappings()) {
            project.configurations.all { Configuration configuration ->
                configuration.resolutionStrategy { ResolutionStrategy resolutionStrategy ->
                    resolutionStrategy.eachDependency { DependencyResolveDetails details ->
                        useTargetIfMatching(extension, details)
                    }
                }
            }
        }
    }

    private void useTargetIfMatching(DependencyResolutionExtension extension, DependencyResolveDetails details) {
        DependencyCoordinates sourceDependencyCoordinates = extension.translation.changingCoordinatesMapping.getMapping(details.requested.group)

        if(sourceDependencyCoordinates) {
            DependencyCoordinates targetDependencyCoordinates = extension.translation.changingCoordinatesMapping.getMapping(sourceDependencyCoordinates)
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

    private void backlistDependencies(Project project, DependencyResolutionExtension extension) {
        if(extension.blacklist.hasMappings()) {
            project.configurations.all { Configuration configuration ->
                def externalDependencies = configuration.dependencies.findAll { it instanceof ExternalModuleDependency }

                externalDependencies.each { dependency ->
                    DependencyCoordinates dependencyCoordinates = new DependencyCoordinates(dependency.group, dependency.name, dependency.version)

                    if (extension.blacklist.containsSuppressed(dependencyCoordinates)) {
                        throw new BlacklistedDependencyDeclarationException("Dependency '$dependencyCoordinates' is blacklisted. Please pick different coordinates.")
                    } else if (extension.blacklist.containsWarned(dependencyCoordinates)) {
                        project.logger.warn "Dependency '$dependencyCoordinates' is flagged as potential issue. It might get blacklisted in the future."
                    }
                }
            }
        }
    }

    private void replaceBundles(Project project, DependencyResolutionExtension extension) {
        if(extension.bundle.hasMappings()) {
            extension.bundle.components.each { DependencyCoordinates source, Set<DependencyCoordinates> targets ->
                project.configurations.all { Configuration configuration ->
                    def foundDependency = configuration.dependencies.find { source.group == it.group && source.name == it.name && source.version == it.version }

                    if(foundDependency) {
                        configuration.dependencies.remove(foundDependency)

                        targets.each { DependencyCoordinates target ->
                            project.dependencies.add(configuration.name, target.toString())
                        }
                    }
                }
            }
        }
    }
}
