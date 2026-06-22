package com.myy803.requirements.service;


import java.util.List;
 
import com.myy803.requirements.model.UseCase;
 
/**
 * Service interface for use case operations.
 *
 * US7  — saveUseCase      : create a new use case for a project
 * US8  — saveUseCase      : update an existing use case (same method, JPA decides INSERT or UPDATE)
 * US9  — getUseCasesByProject : list all use cases of a project
 * US10 — deleteUseCase    : delete a use case
 *
 * All methods receive projectId so the service can:
 *   1. Associate the use case with the correct project when saving.
 *   2. Verify the use case belongs to that project before editing/deleting.
 *
 * actorsInput parameter:
 *   Actors are entered in the HTML form as a comma-separated string
 *   (e.g. "Customer, Admin, Payment System").
 *   The service is responsible for parsing this string and creating
 *   or reusing Actor entities in the database.
 */
public interface UseCaseService {
 
    /** US9 — returns all use cases for a given project */
    List<UseCase> getUseCasesByProject(int projectId);
 
    /**
     * US7 / US8 — saves (create or update) a use case.
     *
     * @param useCase      the UseCase object populated from the form
     * @param projectId    the id of the project this use case belongs to
     * @param actorsInput  comma-separated actor names from the form input
     */
    void saveUseCase(UseCase useCase, int projectId, String actorsInput);
 
    /** US10 — deletes a use case, verifying it belongs to projectId first */
    void deleteUseCase(int useCaseId, int projectId);
 
    /** US8 — loads a single use case for editing, verifying project ownership */
    UseCase getUseCaseByIdAndProject(int useCaseId, int projectId);
 
    /**
     * Helper used to pre-populate the actor text field when editing a use case.
     * Converts the list of Actor objects back into a comma-separated string.
     */
    String getActorsAsString(UseCase useCase);
}