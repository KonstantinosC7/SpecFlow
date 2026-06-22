package com.myy803.requirements.service;

import java.util.List;

import com.myy803.requirements.model.Project;

/**
 * Service interface for project-related business operations.
 *
 * US4 — getProjectsByUser   : list all projects for the logged-in user
 * US5 — saveProject         : create a new project
 * US6 — deleteProject       : delete a project (only if it belongs to the user)
 *
 * All methods receive userId to enforce ownership — a user can only
 * see/edit/delete their own projects.
 */
public interface ProjectService {

    /** US4 — returns all projects belonging to the given user */
    List<Project> getProjectsByUser(int userId);

    /** US5 — saves a new project, associating it with the given user */
    void saveProject(Project project, int userId);

    /** US6 — deletes a project only if it belongs to the given user */
    void deleteProject(int projectId, int userId);

    /** Used by ProjectController when loading a project for editing or viewing */
    Project getProjectByIdAndUser(int projectId, int userId);
}
