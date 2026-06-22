package com.myy803.requirements.service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import com.myy803.requirements.dao.ActorMapper;
import com.myy803.requirements.dao.ProjectMapper;
import com.myy803.requirements.dao.UseCaseMapper;
import com.myy803.requirements.model.Actor;
import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.UseCase;
 
/**
 * Implementation of UseCaseService.
 *
 * === ACTOR HANDLING EXPLAINED ===
 * Actors are entered by the user as a comma-separated string in the HTML form,
 * for example: "Customer, Admin, Payment System".
 *
 * When saving a use case, we:
 *   1. Split the input string by comma.
 *   2. For each actor name, check if an Actor with that name already exists
 *      in this project (using ActorMapper.findByNameAndProjectId).
 *   3. If it exists, reuse it. If not, create a new Actor and save it.
 *   4. Build the final list of Actor objects and set it on the UseCase.
 *
 * This avoids duplicate Actor rows for the same project and reuses
 * actors that appear in multiple use cases.
 *
 * === WHY projectId IN EVERY METHOD? ===
 * Use cases belong to a project. By always checking projectId we ensure
 * a user cannot accidentally (or intentionally) edit/delete a use case
 * that belongs to a different project.
 */
@Service
public class UseCaseServiceImpl implements UseCaseService {
 
    @Autowired
    private UseCaseMapper useCaseMapper;
 
    @Autowired
    private ProjectMapper projectMapper;
 
    @Autowired
    private ActorMapper actorMapper;
 
    /**
     * US9 — Returns all use cases for the given project.
     */
    @Override
    public List<UseCase> getUseCasesByProject(int projectId) {
        return useCaseMapper.findByProjectId(projectId);
    }
 
    /**
     * US7 / US8 — Creates or updates a use case.
     *
     * Step 1: Load the Project entity from DB and set it on the UseCase.
     *         Without this, the project_id FK would be null and the INSERT would fail.
     *
     * Step 2: Parse the actorsInput string and resolve Actor entities.
     *
     * Step 3: Save the UseCase. JPA will:
     *         - INSERT a new row if useCase.id == 0 (new use case, US7)
     *         - UPDATE the existing row if useCase.id > 0 (edit, US8)
     */
    @Override
    public void saveUseCase(UseCase useCase, int projectId, String actorsInput) {
 
        // Step 1: Load and associate the Project
        Project project = projectMapper.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));
        useCase.setProject(project);
 
        // Step 2: Parse actors from the comma-separated input
        List<Actor> resolvedActors = new ArrayList<>();
 
        if (actorsInput != null && !actorsInput.trim().isEmpty()) {
            // Split by comma, trim whitespace from each name
            String[] names = actorsInput.split(",");
 
            for (String rawName : names) {
                String name = rawName.trim();
                if (name.isEmpty()) continue; // skip empty entries (e.g. trailing comma)
 
                // Check if this actor already exists in the project to avoid duplicates
                Actor actor = actorMapper.findByNameAndProjectId(name, projectId)
                        .orElseGet(() -> {
                            // Actor doesn't exist yet — create and save it
                            Actor newActor = new Actor();
                            newActor.setName(name);
                            newActor.setProject(project);
                            return actorMapper.save(newActor);
                        });
 
                resolvedActors.add(actor);
            }
        }
 
        useCase.setActors(resolvedActors);
 
        // Step 3: Save — JPA inserts or updates depending on whether id == 0
        useCaseMapper.save(useCase);
    }
 
    /**
     * US10 — Deletes a use case.
     * findByIdAndProjectId verifies the use case belongs to the given project
     * before we delete it (safety check against URL manipulation).
     */
    @Override
    public void deleteUseCase(int useCaseId, int projectId) {
        UseCase useCase = useCaseMapper.findByIdAndProjectId(useCaseId, projectId)
                .orElseThrow(() -> new RuntimeException(
                        "Use case not found or access denied: " + useCaseId));
        useCaseMapper.delete(useCase);
    }
 
    /**
     * US8 — Loads a single use case for the edit form.
     * Verifies project ownership before returning.
     */
    @Override
    public UseCase getUseCaseByIdAndProject(int useCaseId, int projectId) {
        return useCaseMapper.findByIdAndProjectId(useCaseId, projectId)
                .orElseThrow(() -> new RuntimeException(
                        "Use case not found or access denied: " + useCaseId));
    }
 
    /**
     * Converts the list of Actor objects back to a comma-separated string.
     * Used to pre-populate the actor text field when editing a use case.
     *
     * Example: [Actor("Customer"), Actor("Admin")] -> "Customer, Admin"
     *
     * Collectors.joining(", ") is a standard Java Stream operation that
     * concatenates all strings with ", " as the separator.
     */
    @Override
    public String getActorsAsString(UseCase useCase) {
        if (useCase.getActors() == null || useCase.getActors().isEmpty()) {
            return "";
        }
        return useCase.getActors()
                .stream()
                .map(Actor::getName)
                .collect(Collectors.joining(", "));
    }
}
