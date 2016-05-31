Gradle Blacklist Plugin
==============
![Support Status](https://img.shields.io/badge/nebula-inactive-red.svg)
[![Build Status](https://travis-ci.org/nebula-plugins/gradle-blacklist-plugin.svg?branch=master)](https://travis-ci.org/nebula-plugins/gradle-blacklist-plugin)
[![Coverage Status](https://coveralls.io/repos/nebula-plugins/gradle-blacklist-plugin/badge.svg?branch=master&service=github)](https://coveralls.io/github/nebula-plugins/gradle-blacklist-plugin?branch=master)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/nebula-plugins/gradle-blacklist-plugin?utm_source=badgeutm_medium=badgeutm_campaign=pr-badge)
[![Apache 2.0](https://img.shields.io/github/license/nebula-plugins/gradle-blacklist-plugin.svg)](http://www.apache.org/licenses/LICENSE-2.0)


Gradle plugin for providing dependency resolution correction rules.

## Usage

### Applying the Plugin

To include, add the following to your build.gradle

    buildscript {
        repositories { 
            jcenter() 
        }

        dependencies {
            classpath 'com.netflix.nebula:gradle-blacklist-plugin:3.0.0'
        }
    }

    apply plugin: 'nebula.blacklist'

### Dependency corrections

Dependencies for a Gradle project are defined per build script. It's the user's responsibility to declare and manage the 
list of dependencies. On an organizational level, you might want to enforce certain governance, for example:

* Map declared dependency coordinates to different coordinates.
* Prevent users from declaring dependencies with specific coordinates.
* Replace dependency bundles that combine multiple artifacts as Uber-JAR and define replacement dependencies for it.

Having to deal with these dependency corrections across hundreds or even thousands of projects is almost impossible if
 done manually. Instead you can define dependency corrections centrally for all projects using this plugin. The easiest
 way to achieve this goal is to write your own enterprise-wide plugin that defines the desired dependency corrections. 
 Every Gradle project in your organization would have to apply this plugin. You might also want to force the use of the
  plugin with the help of an init script or a custom Gradle distribution.
  
  Dependency correction rules are not limited to a specific configuration. They apply to all configurations available in
  the project.

#### Changing coordinates

Over time the coordinates of a dependency might change. A typical example is when the organization name was changed and
the artifact is published with the new coordinates. Artifact consumers will either have to directly change the coordinates
 in the build script. A more convenient alternative is to declare the mapping of changed coordinates and let Gradle do the
 heavy lifting.

##### API

Dependency coordinates can be mapped to a different set of coordinates as part of the `translate` closure. Coordinates
can be defined as `String` or `Map`. At the very least the declared dependency notation needs to provide the `group`
attribute. The attributes `name` and `version` are optional. The API exposes the following methods:

    void map(String source, String target)
    void map(Map<String, String> source, Map<String, String> target)

##### Example

    blacklist {
        translate {
            map 'nebula', 'com.netflix.nebula'
            map 'commons-lang:commons-lang:2.6', 'org.apache.commons:commons-lang3:3.3.2'
        }
    }

#### Bad coordinates

Sometime you want to prevent to use of dependencies with specific coordinates. They might have a certain defect, for 
 example transitive dependencies in their metadata do not resolve correctly. With the help of the plugin you can either 
 suppress the use of dependencies or warn the user about them. If Gradle encounters a suppressed dependency, it will 
 automatically throw an exception. If a dependency is flagged as potentially "bad", the user is warned by a log message.

##### API

Coordinates can be defined as `String` or `Map`. The API exposes the following methods:

    void block(String target)
    void block(Map<String, String> target)
    void warn(String target)
    void warn(Map<String, String> target)

##### Example

    blacklist {
        flag {
            block 'org.foo:bar:3.4'
            warn group: 'com.company', name: 'baz', version: '1.2'
        }
    }

#### Replacing bundles

Dependency bundles combine classes of multiple JAR files into a single Uber-JAR. While this practice is acceptable for
classes from several modules with the same version, it's considered an anti-pattern if the artifact combines classes 
from other artifacts produced by other projects. Consuming such a bundle strips you of the capability to successfully
apply any dependency conflict resolution as the notion of their coordinates is lost. This plugin let's you replace a 
dependency bundle with a set of well-defined constituent artifacts.

##### API

Coordinates can be defined as `String` or `Map`. The API exposes the following methods:

    void replace(String sourceBundle, Collection targets)
    void replace(Map<String, String> sourceBundle, Collection targets)

##### Example

    blacklist {
        bundle {
            replace 'com.eclipsesource.jaxrs:jersey-all:2.10.1', ['org.glassfish.jersey:jersey-common:2.10.1', 
                                                                  'org.glassfish.jersey:jersey-client:2.10.1', 
                                                                  'org.glassfish.jersey:jersey-server:2.10.1']
        }
    }
