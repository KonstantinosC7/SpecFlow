package com.myy803.requirements.diagramgeneration;

import java.util.ArrayList;
import java.util.List;

import com.myy803.requirements.model.Actor;
import com.myy803.requirements.model.UseCase;


public class NomnomlUseCaseDiagramGenerator extends AbstractUseCaseDiagramGenerator {

    @Override
    protected String generateHeader() {
        return "#direction: right\n\n";
    }

    /**
     * Declares each unique actor.
     * Format: [<actor> ActorName]
     */
    @Override
    protected String generateActors(List<Actor> uniqueActors) {
        if (uniqueActors.isEmpty()) {
            return "";
        }
        List<String> lines = new ArrayList<>();
        for (Actor actor : uniqueActors) {
            lines.add("[<actor> " + actor.getName() + "]");
        }
        return String.join("\n", lines) + "\n\n";
    }

    /**
     * Declares each use case.
     * Format: [<usecase> Use Case Name]
     */
    @Override
    protected String generateUseCases(List<UseCase> useCases) {
        if (useCases.isEmpty()) {
            return "";
        }
        List<String> lines = new ArrayList<>();
        for (UseCase uc : useCases) {
            lines.add("[<usecase> " + uc.getName() + "]");
        }
        return String.join("\n", lines) + "\n\n";
    }

    /**
     * Creates association lines.
     * Format: [<actor> ActorName] -> [<usecase> UseCaseName]
     *
     * We repeat the full node syntax in the association line — Nomnoml
     * treats this as the same node (identified by the full label including classifier).
     */
    @Override
    protected String generateAssociations(List<UseCase> useCases) {
        if (useCases.isEmpty()) {
            return "";
        }
        List<String> lines = new ArrayList<>();
        for (UseCase uc : useCases) {
            if (uc.getActors() == null || uc.getActors().isEmpty()) continue;
            for (Actor actor : uc.getActors()) {
                lines.add("[<actor> " + actor.getName() + "] -> [<usecase> " + uc.getName() + "]");
            }
        }
        return lines.isEmpty() ? "" : String.join("\n", lines) + "\n";
    }

    @Override
    protected String generateFooter() {
        return ""; // Nomnoml has no closing tag
    }
}
