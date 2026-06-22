package com.myy803.requirements.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myy803.requirements.dao.ProjectMapper;
import com.myy803.requirements.dao.UserMapper;
import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.User;

/**
 * Unit tests for ProjectServiceImpl.
 *
 * Tests cover all four service methods:
 *   getProjectsByUser  (US4)
 *   saveProject        (US5)
 *   deleteProject      (US6)
 *   getProjectByIdAndUser (used by edit/view)
 *
 * Mockito mocks ProjectMapper and UserMapper so no database is needed.
 */
@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User testUser;
    private Project testProject;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("alexdev");

        testProject = new Project();
        testProject.setId(10);
        testProject.setName("Online Banking");
        testProject.setUser(testUser);
    }

    // -------------------------------------------------------------------------
    // getProjectsByUser — US4
    // -------------------------------------------------------------------------

    /**
     * US4 — getProjectsByUser should return the list from the mapper.
     */
    @Test
    void getProjectsByUser_shouldReturnProjectList() {
        // Arrange: mapper returns a list with one project
        when(projectMapper.findByUserId(1)).thenReturn(Arrays.asList(testProject));

        // Act
        List<Project> result = projectService.getProjectsByUser(1);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Online Banking", result.get(0).getName());
    }

    // -------------------------------------------------------------------------
    // saveProject — US5
    // -------------------------------------------------------------------------

    /**
     * US5 — saveProject should load the owner User, set it on the project,
     * and then call mapper.save().
     */
    @Test
    void saveProject_shouldSetOwnerAndSave() {
        // Arrange: userMapper knows about our test user
        when(userMapper.findById(1)).thenReturn(Optional.of(testUser));

        // Act: save a NEW project (id=0 means new)
        Project newProject = new Project();
        newProject.setName("New Project");
        projectService.saveProject(newProject, 1);

        // Assert: user was set on the project
        assertEquals(testUser, newProject.getUser());
        // Assert: mapper.save was called
        verify(projectMapper).save(newProject);
    }

    /**
     * US5 — saveProject should throw RuntimeException when userId is not found.
     * This guards against phantom userId values.
     */
    @Test
    void saveProject_shouldThrow_whenUserNotFound() {
        // Arrange: userMapper finds nobody
        when(userMapper.findById(99)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class,
                () -> projectService.saveProject(testProject, 99));
    }

    // -------------------------------------------------------------------------
    // deleteProject — US6
    // -------------------------------------------------------------------------

    /**
     * US6 — deleteProject should delete the project when it belongs to the user.
     */
    @Test
    void deleteProject_shouldDelete_whenProjectBelongsToUser() {
        // Arrange: ownership check passes
        when(projectMapper.findByIdAndUserId(10, 1)).thenReturn(Optional.of(testProject));

        // Act
        projectService.deleteProject(10, 1);

        // Assert: mapper.delete was called with the correct project
        verify(projectMapper).delete(testProject);
    }

    /**
     * US6 — deleteProject should throw RuntimeException when the project
     * does not belong to the user (ownership violation).
     */
    @Test
    void deleteProject_shouldThrow_whenProjectNotOwnedByUser() {
        // Arrange: ownership check fails (different user)
        when(projectMapper.findByIdAndUserId(10, 2)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class,
                () -> projectService.deleteProject(10, 2));
    }

    // -------------------------------------------------------------------------
    // getProjectByIdAndUser
    // -------------------------------------------------------------------------

    /**
     * getProjectByIdAndUser should return the project when found.
     */
    @Test
    void getProjectByIdAndUser_shouldReturnProject_whenFound() {
        // Arrange
        when(projectMapper.findByIdAndUserId(10, 1)).thenReturn(Optional.of(testProject));

        // Act
        Project result = projectService.getProjectByIdAndUser(10, 1);

        // Assert
        assertEquals("Online Banking", result.getName());
    }

    /**
     * getProjectByIdAndUser should throw when the project is not found.
     */
    @Test
    void getProjectByIdAndUser_shouldThrow_whenNotFound() {
        // Arrange
        when(projectMapper.findByIdAndUserId(99, 1)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class,
                () -> projectService.getProjectByIdAndUser(99, 1));
    }
}
