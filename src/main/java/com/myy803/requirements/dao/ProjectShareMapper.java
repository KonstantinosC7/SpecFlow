package com.myy803.requirements.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myy803.requirements.model.ProjectShare;

/**
 * findByProjectId:
 *   -> SELECT * FROM project_shares WHERE project_id = ?
 *   Used to list all users who have been given access to a project.
 *
 * findBySharedWithId:
 *   -> SELECT * FROM project_shares WHERE shared_with_user_id = ?
 *   Used to find all projects shared with a given user
 *   (for the "Shared with me" section in the dashboard).
 *
 * findByProjectIdAndSharedWithId:
 *   -> SELECT * FROM project_shares WHERE project_id = ? AND shared_with_user_id = ?
 *   Used to check if a user already has access (prevent duplicates)
 *   and to find a specific share record for removal.
 */
@Repository
public interface ProjectShareMapper extends JpaRepository<ProjectShare, Integer> {

    /** Returns all share records for a project — US18 list */
    List<ProjectShare> findByProjectId(int projectId);

    /** Returns all projects shared with a given user */
    List<ProjectShare> findBySharedWithId(int userId);

    /** Checks whether a specific user already has access to a specific project */
    Optional<ProjectShare> findByProjectIdAndSharedWithId(int projectId, int sharedWithId);
}
