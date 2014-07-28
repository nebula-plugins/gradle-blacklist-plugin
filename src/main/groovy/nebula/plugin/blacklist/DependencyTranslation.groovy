package nebula.plugin.blacklist

class DependencyTranslation {
    DependencyMapping changingCoordinatesMapping = new DependencyMapping()

    void map(String sourceCoordinates, String targetCoordinates) {
        DependencyCoordinates sourceDependencyCoordinates = createDependencyCoordinates(sourceCoordinates)
        DependencyCoordinates targetDependencyCoordinates = createDependencyCoordinates(targetCoordinates)
        changingCoordinatesMapping.addMapping(sourceDependencyCoordinates, targetDependencyCoordinates)
    }

    void map(Map<String, String> sourceCoordinates, Map<String, String> targetCoordinates) {
        DependencyCoordinates sourceDependencyCoordinates = createDependencyCoordinates(sourceCoordinates)
        DependencyCoordinates targetDependencyCoordinates = createDependencyCoordinates(targetCoordinates)
        changingCoordinatesMapping.addMapping(sourceDependencyCoordinates, targetDependencyCoordinates)
    }

    private DependencyCoordinates createDependencyCoordinates(String coordinates) {
        if(!coordinates) {
            throw new InvalidDependencyDeclarationException('Dependency coordinates cannot be null or empty String')
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

    private DependencyCoordinates createDependencyCoordinates(Map<String, String> coordinates) {
        if(!DependencyCoordinates.Notation.allAttributes.containsAll(coordinates.keySet())) {
            throw new InvalidDependencyDeclarationException("Dependency coordinates require at least one of these attributes: 'group', 'name', 'version'")
        }

        new DependencyCoordinates(coordinates[DependencyCoordinates.Notation.GROUP.attribute],
                coordinates[DependencyCoordinates.Notation.NAME.attribute],
                coordinates[DependencyCoordinates.Notation.VERSION.attribute])
    }
}
