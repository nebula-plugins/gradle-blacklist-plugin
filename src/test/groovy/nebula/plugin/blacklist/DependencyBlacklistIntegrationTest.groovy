package nebula.plugin.blacklist

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult
import org.apache.commons.lang.exception.ExceptionUtils

class DependencyBlacklistIntegrationTest extends IntegrationSpec {
    def "Declares suppressed dependency but it doesn't match any dependency"() {
        when:
        buildFile << """
apply plugin: 'blacklist'

dependencyResolution {
    blacklist {
        suppress 'my.group:awesome:3.4'
    }
}

configurations {
    myConf
}

dependencies {
    myConf 'com.company:important:1.0'
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0""")
    }

    def "Suppressed dependency matches declared dependency"() {
        when:
        buildFile << """
apply plugin: 'blacklist'

dependencyResolution {
    blacklist {
        suppress 'com.company:important:1.0'
    }
}

configurations {
    myConf
}

dependencies {
    myConf 'com.company:important:1.0'
}
"""
        ExecutionResult result = runTasksWithFailure('dependencies')

        then:
        Throwable rootCause = ExceptionUtils.getRootCause(result.failure)
        rootCause.message == "Dependency 'com.company:important:1.0' is blacklisted. Please pick different coordinates."
    }

    def "Declares future blacklisted dependency but it doesn't match any dependency"() {
        when:
        buildFile << """
apply plugin: 'blacklist'

dependencyResolution {
    blacklist {
        warn 'my.group:awesome:3.4'
    }
}

configurations {
    myConf
}

dependencies {
    myConf 'com.company:important:1.0'
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0""")
    }

    def "Future blacklisted dependency matches declared dependency"() {
        when:
        buildFile << """
apply plugin: 'blacklist'

dependencyResolution {
    blacklist {
        warn 'com.company:important:1.0'
    }
}

configurations {
    myConf
}

dependencies {
    myConf 'com.company:important:1.0'
}
"""
        ExecutionResult result = runTasksSuccessfully('dependencies')

        then:
        result.standardOutput.contains("""
myConf
\\--- com.company:important:1.0""")
        result.standardOutput.contains("Dependency 'com.company:important:1.0' is flagged as potential issue. It might get blacklisted in the future.")
    }
}
