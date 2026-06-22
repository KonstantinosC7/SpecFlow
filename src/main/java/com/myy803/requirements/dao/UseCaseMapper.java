package com.myy803.requirements.dao;

import java.util.List;
import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import com.myy803.requirements.model.UseCase;
 
/**
 * findByProjectId:
 *   Generates -> SELECT * FROM use_cases WHERE project_id = ?
 *   Used by US9: list all use cases of a project.
 *
 * findByIdAndProjectId:
 *   Generates -> SELECT * FROM use_cases WHERE id = ? AND project_id = ?
 *   Used for safe access — a use case is only fetched if it truly
 *   belongs to the expected project (prevents URL manipulation).
 */
@Repository
public interface UseCaseMapper extends JpaRepository<UseCase, Integer> {
 
    /** US9 — returns all use cases belonging to a project */
    List<UseCase> findByProjectId(int projectId);
 
    /** US8, US10 — safely fetches a use case only if it belongs to the given project */
    Optional<UseCase> findByIdAndProjectId(int id, int projectId);
}
