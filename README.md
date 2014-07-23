gradle-blacklist-plugin
==============

Gradle plugin for providing dependency resolution correction rules

## Usage

### Applying the Plugin

To include, add the following to your build.gradle

    buildscript {
      repositories { jcenter() }

      dependencies {
        classpath 'com.netflix.nebula:gradle-blacklist-plugin:1.12.+'
      }
    }

    apply plugin: 'blacklist'

### Tasks Provided

`<your tasks>`

### Extensions Provided

`<your extensions>`
