package nebula.plugin.blacklist

import org.gradle.api.GradleException

class BlacklistedDependencyDeclarationException extends GradleException {
    BlacklistedDependencyDeclarationException(String s) {
        super(s)
    }
}
