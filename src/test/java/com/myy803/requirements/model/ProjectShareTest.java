package com.myy803.requirements.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the ProjectShare domain class (US18).
 *
 * ProjectShare links a Project to a User (the collaborator).
 * Tests verify that the two FK relationships (project, sharedWith)
 * are correctly stored and retrieved.
 */
public class ProjectShareTest {

    private ProjectShare share;
    private Project project;
    private User owner;
    private User collaborator;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setUsername("alexdev");

        collaborator = new User();
        collaborator.setId(2);
        collaborator.setUsername("bobdev");

        project = new Project();
        project.setId(10);
        project.setName("Banking App");
        project.setUser(owner);

        share = ProjectShare.builder()
                .id(100)
                .project(project)
                .sharedWith(collaborator)
                .build();
    }

    // -------------------------------------------------------------------------
    // Getter tests
    // -------------------------------------------------------------------------

    @Test
    void getId_shouldReturnCorrectId() {
        assertEquals(100, share.getId());
    }

    @Test
    void getProject_shouldReturnLinkedProject() {
        assertNotNull(share.getProject());
        assertEquals("Banking App", share.getProject().getName());
    }

    @Test
    void getSharedWith_shouldReturnCollaborator() {
        assertNotNull(share.getSharedWith());
        assertEquals("bobdev", share.getSharedWith().getUsername());
    }

    // -------------------------------------------------------------------------
    // Setter tests
    // -------------------------------------------------------------------------

    @Test
    void setSharedWith_shouldUpdateCollaborator() {
        User newCollab = new User();
        newCollab.setId(3);
        newCollab.setUsername("caroldev");

        share.setSharedWith(newCollab);

        assertEquals("caroldev", share.getSharedWith().getUsername());
    }

    @Test
    void setProject_shouldUpdateProject() {
        Project newProject = new Project();
        newProject.setId(20);
        newProject.setName("E-Commerce");

        share.setProject(newProject);

        assertEquals("E-Commerce", share.getProject().getName());
    }

    // -------------------------------------------------------------------------
    // Relationship integrity test
    // -------------------------------------------------------------------------

    /**
     * The share correctly links the collaborator to the owner's project.
     * This is the core invariant of US18: the project belongs to the owner,
     * and the share grants access to a different user.
     */
    @Test
    void share_shouldLinkCollaboratorToOwnersProject() {
        assertEquals("alexdev", share.getProject().getUser().getUsername());
        assertEquals("bobdev", share.getSharedWith().getUsername());
        // Owner and collaborator must be different users
        org.junit.jupiter.api.Assertions.assertNotEquals(
                share.getProject().getUser().getId(),
                share.getSharedWith().getId()
        );
    }

    // -------------------------------------------------------------------------
    // @NoArgsConstructor test
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_shouldCreateEmptyShare() {
        ProjectShare empty = new ProjectShare();
        assertNotNull(empty);
        assertEquals(0, empty.getId());
        assertNull(empty.getProject());
        assertNull(empty.getSharedWith());
    }
}
