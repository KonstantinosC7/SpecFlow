package com.myy803.requirements.diagramgeneration;

import java.util.List;

import com.myy803.requirements.model.UseCase;

public interface UseCaseDiagramStrategy {

    /**
     * Generates a complete diagram script for the given list of use cases.
     *
     * @param useCases the use cases of a project (each with their actors)
     * @return the full script string ready to paste into PlantUML / Nomnoml
     */
    String generateScript(List<UseCase> useCases);
}