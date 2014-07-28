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
package nebula.plugin.depres

import org.gradle.util.ConfigureUtil

class DependencyResolutionExtension {
    DependencyTranslation translation = new DependencyTranslation()
    DependencyBlacklist blacklist = new DependencyBlacklist()
    DependencyBundle bundle = new DependencyBundle()

    void translate(Closure closure) {
        ConfigureUtil.configure(closure, translation)
    }

    void blacklist(Closure closure) {
        ConfigureUtil.configure(closure, blacklist)
    }

    void bundle(Closure closure) {
        ConfigureUtil.configure(closure, bundle)
    }
}
