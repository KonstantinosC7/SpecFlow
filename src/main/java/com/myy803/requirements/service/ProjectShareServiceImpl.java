package com.myy803.requirements.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myy803.requirements.dao.ProjectMapper;
import com.myy803.requirements.dao.ProjectShareMapper;
import com.myy803.requirements.dao.UserMapper;
import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.ProjectShare;
import com.myy803.requirements.model.User;

/**
 * Implementation of ProjectShareService (US18).
 *
 * === AUTHORIZATION CHECK ===
 * isUserAuthorizedForProject() first checks ownership (Project.user.id == userId),
 * then checks the project_shares table. This single method is called from
 * UseCaseController, CRCCardController, and DiagramController so that both
 * project owners and collaborators can access the project content.
 *
 * === DUPLICATE SHARE PREVENTION ===
 * Before creating a new share, we check findByProjectIdAndSharedWithId().
 * If a record already exists, we throw a RuntimeException instead of
 * creating a duplicate — the unique constraint in the DB is the final
 * safety net, but checking early gives a cleaner error message.
 *
 * === OWNER CANNOT SHARE WITH THEMSELVES ===
 * An owner sharing with themselves is prevented: we check if the target
 * user id equals the owner user id and throw if so.
 */
@Service
public class ProjectShareServiceImpl implements ProjectShareService {

    @Autowired
    private ProjectShareMapper projectShareMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * US18 — Shares a project with another user.
     */
    @Override
    public void shareProject(int projectId, int ownerUserId, String targetUsername) {

        // Load the project — verify it belongs to the owner
        Project project = projectMapper.findByIdAndUserId(projectId, ownerUserId)
                .orElseThrow(() -> new RuntimeException(
                        "Project not found or you are not the owner: " + projectId));

        // Find the target user by username
        User targetUser = userMapper.findByUsername(targetUsername)
                .orElseThrow(() -> new RuntimeException(
                        "No user found with username: " + targetUsername));

        // Owner cannot share the project with themselves
        if (targetUser.getId() == ownerUserId) {
            throw new RuntimeException("You cannot share a project with yourself.");
        }

        // Check if this share already exists (prevent duplicates)
        boolean alreadyShared = projectShareMapper
                .findByProjectIdAndSharedWithId(projectId, targetUser.getId())
                .isPresent();

        if (alreadyShared) {
            throw new RuntimeException(
                    "Project is already shared with " + targetUsername);
        }

        // Create and save the share record
        ProjectShare share = new ProjectShare();
        share.setProject(project);
        share.setSharedWith(targetUser);
        projectShareMapper.save(share);
    }

    /**
     * US18 — Removes a share record.
     * We verify the share belongs to a project owned by ownerUserId.
     */
    @Override
    public void removeShare(int shareId, int ownerUserId) {
        ProjectShare share = projectShareMapper.findById(shareId)
                .orElseThrow(() -> new RuntimeException(
                        "Share record not found: " + shareId));

        // Security check: only the project owner can remove shares
        if (share.getProject().getUser().getId() != ownerUserId) {
            throw new RuntimeException("Access denied: you are not the project owner.");
        }

        projectShareMapper.delete(share);
    }

    /**
     * Returns all share records for a project (for the share management page).
     */
    @Override
    public List<ProjectShare> getSharesByProject(int projectId) {
        return projectShareMapper.findByProjectId(projectId);
    }

    /**
     * Returns all projects that have been shared with the given user.
     * Extracts the Project from each ProjectShare record.
     */
    @Override
    public List<Project> getProjectsSharedWithUser(int userId) {
        return projectShareMapper.findBySharedWithId(userId)
                .stream()
                .map(ProjectShare::getProject)
                .collect(Collectors.toList());
    }

    /**
     * Returns true if userId is authorized to access the project
     * (either as owner OR as a collaborator via ProjectShare).
     *
     * Called by controllers before loading use cases, CRC cards, or diagrams.
     */
    @Override
    public boolean isUserAuthorizedForProject(int projectId, int userId) {
        // Check ownership first
        boolean isOwner = projectMapper.findByIdAndUserId(projectId, userId).isPresent();
        if (isOwner) {
            return true;
        }
        // Check if there is a share record for this user
        return projectShareMapper
                .findByProjectIdAndSharedWithId(projectId, userId)
                .isPresent();
    }
}
