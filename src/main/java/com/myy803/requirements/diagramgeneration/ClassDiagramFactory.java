package com.myy803.requirements.diagramgeneration;


public class ClassDiagramFactory {

    /**
     * Creates and returns the appropriate class diagram generator.
     *
     * @param tool "plantuml" or "nomnoml" (case-insensitive)
     * @return the matching ClassDiagramStrategy implementation
     * @throws IllegalArgumentException if the tool name is not recognised
     */
    public static ClassDiagramStrategy create(String tool) {
        switch (tool.toLowerCase()) {
            case "plantuml":
                return new PlantUMLClassDiagramGenerator();
            case "nomnoml":
                return new NomnomlClassDiagramGenerator();
            default:
                throw new IllegalArgumentException(
                        "Unknown UML tool for class diagrams: " + tool
                        + ". Supported: plantuml, nomnoml");
        }
    }

    private ClassDiagramFactory() {}
}
