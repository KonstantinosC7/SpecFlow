package com.myy803.requirements.diagramgeneration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.myy803.requirements.model.Actor;
import com.myy803.requirements.model.UseCase;


public abstract class AbstractUseCaseDiagramGenerator implements UseCaseDiagramStrategy {

    /**
     * The fixed algorithm — calls the five steps in order.
     * Subclasses cannot override this method (final).
     */
    @Override
    public final String generateScript(List<UseCase> useCases) {
        // Collect unique actors across all use cases once — shared by steps 2 and 4
        List<Actor> uniqueActors = collectUniqueActors(useCases);

        return generateHeader()
             + generateActors(uniqueActors)
             + generateUseCases(useCases)
             + generateAssociations(useCases)
             + generateFooter();
    }

    // -------------------------------------------------------------------------
    // Abstract steps — each subclass must implement these
    // -------------------------------------------------------------------------

    /** Produces the opening lines of the script (e.g. "@startuml" for PlantUML) */
    protected abstract String generateHeader();

    /** Declares each unique actor in the diagram */
    protected abstract String generateActors(List<Actor> uniqueActors);

    /** Declares each use case node */
    protected abstract String generateUseCases(List<UseCase> useCases);

    /** Creates association lines between actors and the use cases they participate in */
    protected abstract String generateAssociations(List<UseCase> useCases);

    /** Produces the closing lines of the script (e.g. "@enduml" for PlantUML) */
    protected abstract String generateFooter();

    // -------------------------------------------------------------------------
    // Shared helper — available to all subclasses
    // -------------------------------------------------------------------------

    /**
     * Collects all unique actors from all use cases
     * Uses actor ID as the uniqueness key (not name) to handle
     * the case where two use cases share the same Actor entity row
     * LinkedHashSet preserves the order actors were first encountered
     */
    protected List<Actor> collectUniqueActors(List<UseCase> useCases) {
        // Use a Set of IDs to track which actors we have already added
        Set<Integer> seenIds = new LinkedHashSet<>();
        List<Actor> unique = new ArrayList<>();

        for (UseCase uc : useCases) {
            if (uc.getActors() == null) continue;
            for (Actor actor : uc.getActors()) {
                // add() on a Set returns true only if the element was not already present
                if (seenIds.add(actor.getId())) {
                    unique.add(actor);
                }
            }
        }
        return unique;
    }

    /**
     * Converts an actor name to a safe alias for PlantUML
     * (no spaces or special characters allowed in aliases)
     */
    protected String toAlias(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "_");
    }
}
