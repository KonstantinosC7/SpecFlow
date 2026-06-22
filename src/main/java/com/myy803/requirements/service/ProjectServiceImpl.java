package com.myy803.requirements.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myy803.requirements.dao.ProjectMapper;
import com.myy803.requirements.dao.UserMapper;
import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.User;


@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserMapper userRepository;

    /**
     * US4 — Returns all projects for the given user.
     */
    @Override
    public List<Project> getProjectsByUser(int userId) {
        return projectMapper.findByUserId(userId);
    }

    /**
     * US5 — Saves a new project.
     * Loads the User entity from DB and sets it on the project before saving.
     * The createdAt timestamp is set automatically by @CreationTimestamp.
     */
    @Override
    public void saveProject(Project project, int userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        project.setUser(owner);
        projectMapper.save(project);
    }

    /**
     * US6 — Deletes a project.
     * Uses findByIdAndUserId to verify ownership before deleting.
     * CascadeType.ALL on Project.useCases and Project.crcCards ensures all
     * related use cases and CRC cards are deleted automatically.
     */
    @Override
    public void deleteProject(int projectId, int userId) {
        Project project = projectMapper.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException(
                        "Project not found or access denied: " + projectId));
        projectMapper.delete(project);
    }

    /**
     * Fetches a single project by id, enforcing that it belongs to the given user.
     */
    @Override
    public Project getProjectByIdAndUser(int projectId, int userId) {
        return projectMapper.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException(
                        "Project not found or access denied: " + projectId));
    }
}
