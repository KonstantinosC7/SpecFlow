package com.myy803.requirements.service;

import java.util.List;

import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.ProjectShare;

/**
 * Service interface for project sharing operations (US18).
 *
 * === METHODS ===
 *
 * shareProject:
 *   The project owner enters a username. This method:
 *     1. Verifies the project belongs to the owner.
 *     2. Looks up the target user by username.
 *     3. Checks they are not already a collaborator.
 *     4. Creates and saves the ProjectShare record.
 *
 * removeShare:
 *   Removes a specific share record, so the user loses access.
 *   Only the project owner can do this.
 *
 * getSharesByProject:
 *   Returns all ProjectShare records for a project
 *   (used to show the list of collaborators on the share page).
 *
 * getProjectsSharedWithUser:
 *   Returns all projects that have been shared with a given user
 *   (used to show "Shared with me" section on the dashboard).
 *
 * isUserAuthorizedForProject:
 *   Returns true if the user is the owner OR a collaborator.
 *   Used by controllers to allow both types of user to access
 *   use cases, CRC cards, and diagram generation.
 */
public interface ProjectShareService {

    /**
     * US18 — Shares a project with a user identified by their username.
     *
     * @param projectId     the project to share
     * @param ownerUserId   must be the owner of the project
     * @param targetUsername the username of the user to grant access to
     * @throws RuntimeException if project not found, user not found,
     *                          or share already exists
     */
    void shareProject(int projectId, int ownerUserId, String targetUsername);

    /**
     * US18 — Removes a share record.
     *
     * @param shareId     the id of the ProjectShare to remove
     * @param ownerUserId must be the project owner (authorization check)
     */
    void removeShare(int shareId, int ownerUserId);

    /** Returns all share records for a project */
    List<ProjectShare> getSharesByProject(int projectId);

    /** Returns all projects shared with the given user */
    List<Project> getProjectsSharedWithUser(int userId);

    /**
     * Returns true if userId is either the project owner or a collaborator.
     * Used to gate access to project content (use cases, CRC cards, diagrams).
     */
    boolean isUserAuthorizedForProject(int projectId, int userId);
}
