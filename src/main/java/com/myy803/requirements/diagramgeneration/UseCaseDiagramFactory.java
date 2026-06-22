package com.myy803.requirements.diagramgeneration;


public class UseCaseDiagramFactory {

    /**
     * Creates and returns the appropriate use case diagram generator.
     *
     * @param tool "plantuml" or "nomnoml" (case-insensitive)
     * @return the matching UseCaseDiagramStrategy implementation
     * @throws IllegalArgumentException if the tool name is not recognised
     */
    public static UseCaseDiagramStrategy create(String tool) {
        switch (tool.toLowerCase()) {
            case "plantuml":
                return new PlantUMLUseCaseDiagramGenerator();
            case "nomnoml":
                return new NomnomlUseCaseDiagramGenerator();
            default:
                throw new IllegalArgumentException(
                        "Unknown UML tool for use case diagrams: " + tool
                        + ". Supported: plantuml, nomnoml");
        }
    }

    // Private constructor — this class should never be instantiated.
    private UseCaseDiagramFactory() {}
}