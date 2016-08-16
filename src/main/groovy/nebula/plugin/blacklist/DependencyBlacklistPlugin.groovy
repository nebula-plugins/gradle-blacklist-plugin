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

import nebula.plugin.blacklist.data.DependencyCoordinates
import nebula.plugin.blacklist.exception.BlockedDependencyDeclarationException
import nebula.plugin.blacklist.extension.DependencyResolutionExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ResolutionStrategy

class DependencyBlacklistPlugin implements Plugin<Project> {
    static final String EXTENSION_NAME = 'blacklist'

    @Override
    void apply(Project project) {
        DependencyResolutionExtension extension = project.extensions.create(EXTENSION_NAME, DependencyResolutionExtension)

        project.afterEvaluate {
            changeDependencyCoordinates(project, extension)
            flagDependencies(project, extension)
            replaceBundles(project, extension)
        }
    }

    private void changeDependencyCoordinates(Project project, DependencyResolutionExtension extension) {
        if(extension.translation.hasMappings()) {
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
        DependencyCoordinates sourceDependencyCoordinates = extension.translation.getMapping(details.requested.group, details.requested.name, details.requested.version)

        if(sourceDependencyCoordinates) {
            DependencyCoordinates targetDependencyCoordinates = extension.translation.getMapping(sourceDependencyCoordinates)
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

    private void flagDependencies(Project project, DependencyResolutionExtension extension) {
        if(extension.flag.hasMappings()) {
            project.configurations.all { Configuration configuration ->
                def externalDependencies = configuration.dependencies.findAll { it instanceof ExternalModuleDependency }

                externalDependencies.each { dependency ->
                    DependencyCoordinates dependencyCoordinates = new DependencyCoordinates(dependency.group, dependency.name, dependency.version)

                    if(extension.flag.containsBlocked(dependencyCoordinates)) {
                        throw new BlockedDependencyDeclarationException("Dependency '$dependencyCoordinates' is blocked. Please pick different coordinates.")
                    } else if (extension.flag.containsWarned(dependencyCoordinates)) {
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
                    def foundDependency = configuration.dependencies.find {
                        source.group == it.group && source.name == it.name && source.version == it.version
                    }

                    if(foundDependency) {
                        boolean success = configuration.dependencies.remove(foundDependency)

                        if(success) {
                            targets.each { DependencyCoordinates target ->
                                project.dependencies.add(configuration.name, target.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}
