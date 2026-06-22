package com.myy803.requirements.service;

/**
 * Service interface for diagram script generation.
 *
 * US15 — generateUseCaseDiagram : produces a use case diagram script
 * US16 — generateClassDiagram   : produces a class diagram script
 *
 * Both methods accept a projectId (to load the data) and a tool
 * name string (to select the generator via the factory).
 *
 * The service is the only part of the application that knows about
 * the diagram generation sub-package. The controller only calls
 * this interface — it never touches the factories or generators directly.
 */
public interface DiagramService {

    /**
     * US15 — Generates a use case diagram script.
     *
     * @param projectId the project whose use cases should be visualised
     * @param tool      "plantuml" or "nomnoml"
     * @return the generated script as a plain String
     */
    String generateUseCaseDiagram(int projectId, String tool);

    /**
     * US16 — Generates a class diagram script from CRC cards.
     *
     * @param projectId the project whose CRC cards should be visualised
     * @param tool      "plantuml" or "nomnoml"
     * @return the generated script as a plain String
     */
    String generateClassDiagram(int projectId, String tool);
}
