package com.myy803.requirements.diagramgeneration;

import java.util.ArrayList;
import java.util.List;

import com.myy803.requirements.model.Actor;
import com.myy803.requirements.model.UseCase;

public class PlantUMLUseCaseDiagramGenerator extends AbstractUseCaseDiagramGenerator {

    @Override
    protected String generateHeader() {
        return "@startuml\n"
             + "left to right direction\n\n";
    }

    /**
     * Declares each unique actor.
     * Format: actor "Actor Name" as Alias
     */
    @Override
    protected String generateActors(List<Actor> uniqueActors) {
        if (uniqueActors.isEmpty()) {
            return "";
        }
        List<String> lines = new ArrayList<>();
        for (Actor actor : uniqueActors) {
            lines.add("actor \"" + actor.getName() + "\" as " + toAlias(actor.getName()));
        }
        return String.join("\n", lines) + "\n\n";
    }

    /**
     * Declares each use case inside a rectangle (system boundary).
     * Format:
     *   rectangle System {
     *     usecase "Name" as UC_0
     *     ...
     *   }
     */
    @Override
    protected String generateUseCases(List<UseCase> useCases) {
        if (useCases.isEmpty()) {
            return "";
        }
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < useCases.size(); i++) {
            lines.add("  usecase \"" + useCases.get(i).getName() + "\" as UC_" + i);
        }
        return "rectangle System {\n" + String.join("\n", lines) + "\n}\n\n";
    }

    /**
     * Creates association lines: ActorAlias --> UC_index
     *
     * We iterate each use case and for each of its actors we emit one line.
     * We use the actor alias and the UC index alias (not quoted names)
     * so the syntax is always valid.
     */
    @Override
    protected String generateAssociations(List<UseCase> useCases) {
        if (useCases.isEmpty()) {
            return "";
        }
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < useCases.size(); i++) {
            UseCase uc = useCases.get(i);
            if (uc.getActors() == null || uc.getActors().isEmpty()) continue;
            for (Actor actor : uc.getActors()) {
                lines.add(toAlias(actor.getName()) + " --> UC_" + i);
            }
        }
        return lines.isEmpty() ? "" : String.join("\n", lines) + "\n";
    }

    @Override
    protected String generateFooter() {
        return "\n@enduml\n";
    }
}
