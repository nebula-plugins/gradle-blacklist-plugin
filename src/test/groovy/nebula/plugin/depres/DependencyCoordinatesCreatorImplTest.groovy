package nebula.plugin.depres

import nebula.plugin.depres.data.DependencyCoordinates
import nebula.plugin.depres.data.DependencyCoordinatesCreator
import nebula.plugin.depres.data.DependencyCoordinatesCreatorImpl
import nebula.plugin.depres.exception.InvalidDependencyDeclarationException
import spock.lang.Specification
import spock.lang.Unroll

class DependencyCoordinatesCreatorImplTest extends Specification {
    DependencyCoordinatesCreator dependencyCoordinatesCreator = new DependencyCoordinatesCreatorImpl()

    @Unroll
    def "Throws exception for blank or empty coordinates #coordinates"() {
        when:
        dependencyCoordinatesCreator.create(coordinates)

        then:
        Throwable t = thrown(InvalidDependencyDeclarationException)
        t.message == exceptionMessage

        where:
        coordinates | exceptionMessage
        ''          | 'Dependency coordinates cannot be empty String'
        null        | 'Dependency coordinates cannot be null'
        [:]         | 'Dependency coordinates cannot be null'
    }

    @Unroll
    def "Throws exception for malformed coordinates #coordinates"() {
        when:
        dependencyCoordinatesCreator.create(coordinates)

        then:
        Throwable t = thrown(InvalidDependencyDeclarationException)
        t.message == exceptionMessage

        where:
        coordinates                                                       | exceptionMessage
        'com.company:important:1.0:something'                             | 'Dependency coordinates require the following format <group>:<name>:<version>'
        [groupId: 'com.company', artifactId: 'important', version: '1.0'] | "Dependency coordinates require at least one of these attributes: 'group', 'name', 'version'"
    }

    @Unroll
    def "Can create object representation from String coordinates '#coordinates'"() {
        expect:
        DependencyCoordinates dependencyCoordinates = dependencyCoordinatesCreator.create(coordinates)
        dependencyCoordinates.group == expectedGroup
        dependencyCoordinates.name == expectedName
        dependencyCoordinates.version == expectedVersion

        where:
        coordinates                 | expectedGroup | expectedName | expectedVersion
        'com.company'               | 'com.company' | null         | null
        'com.company:important'     | 'com.company' | 'important'  | null
        'com.company:important:1.0' | 'com.company' | 'important'  | '1.0'
    }

    @Unroll
    def "Can create object representation from Map coordinates #coordinates"() {
        expect:
        DependencyCoordinates dependencyCoordinates = dependencyCoordinatesCreator.create(coordinates)
        dependencyCoordinates.group == expectedGroup
        dependencyCoordinates.name == expectedName
        dependencyCoordinates.version == expectedVersion

        where:
        coordinates                                               | expectedGroup | expectedName | expectedVersion
        [group: 'com.company']                                    | 'com.company' | null         | null
        [group: 'com.company', name: 'important']                 | 'com.company' | 'important'  | null
        [group: 'com.company', name: 'important', version: '1.0'] | 'com.company' | 'important'  | '1.0'
    }
}
