package com.myy803.requirements.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import com.myy803.requirements.dao.ActorMapper;
import com.myy803.requirements.dao.ProjectMapper;
import com.myy803.requirements.dao.UseCaseMapper;
import com.myy803.requirements.model.Actor;
import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.UseCase;

/**
 * Unit tests for UseCaseServiceImpl.
 *
 * Focus areas:
 *   getUseCasesByProject  (US9)
 *   saveUseCase           (US7 / US8) — including actor parsing logic
 *   deleteUseCase         (US10)
 *   getActorsAsString     (helper used by the edit form)
 */
@ExtendWith(MockitoExtension.class)
public class UseCaseServiceImplTest {

    @Mock
    private UseCaseMapper useCaseMapper;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private ActorMapper actorMapper;

    @InjectMocks
    private UseCaseServiceImpl useCaseService;

    private Project testProject;
    private UseCase testUseCase;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(1);
        testProject.setName("Banking App");

        testUseCase = new UseCase();
        testUseCase.setId(5);
        testUseCase.setName("User Login");
        testUseCase.setProject(testProject);
    }

    // -------------------------------------------------------------------------
    // getUseCasesByProject — US9
    // -------------------------------------------------------------------------

    @Test
    void getUseCasesByProject_shouldReturnList() {
        // Arrange
        when(useCaseMapper.findByProjectId(1))
                .thenReturn(Arrays.asList(testUseCase));

        // Act
        List<UseCase> result = useCaseService.getUseCasesByProject(1);

        // Assert
        assertEquals(1, result.size());
        assertEquals("User Login", result.get(0).getName());
    }

    // -------------------------------------------------------------------------
    // saveUseCase — US7 (new) and US8 (update)
    // -------------------------------------------------------------------------

    /**
     * US7 — saveUseCase should set the project, resolve actors, and call save().
     *
     * For the actor "Customer": we simulate that it does NOT yet exist
     * in the DB (findByNameAndProjectId returns empty) — so a new one
     * should be created and saved.
     */
    @Test
    void saveUseCase_shouldSetProjectAndCreateNewActors() {
        // Arrange
        when(projectMapper.findById(1)).thenReturn(Optional.of(testProject));

        // "Customer" does not exist yet -> should be created
        when(actorMapper.findByNameAndProjectId("Customer", 1))
                .thenReturn(Optional.empty());

        Actor savedActor = new Actor();
        savedActor.setId(10);
        savedActor.setName("Customer");
        savedActor.setProject(testProject);

        // When a new Actor is saved, return the savedActor with an id
        when(actorMapper.save(any(Actor.class))).thenReturn(savedActor);

        UseCase newUseCase = new UseCase();
        newUseCase.setName("Place Order");

        // Act
        useCaseService.saveUseCase(newUseCase, 1, "Customer");

        // Assert — project was set
        assertEquals(testProject, newUseCase.getProject());
        // Assert — actor list has one entry
        assertEquals(1, newUseCase.getActors().size());
        assertEquals("Customer", newUseCase.getActors().get(0).getName());
        // Assert — use case was saved
        verify(useCaseMapper).save(newUseCase);
    }

    /**
     * saveUseCase should REUSE an existing actor (not create a duplicate).
     * If "Customer" already exists for this project, we reuse that row.
     */
    @Test
    void saveUseCase_shouldReuseExistingActor_whenActorAlreadyExists() {
        // Arrange
        when(projectMapper.findById(1)).thenReturn(Optional.of(testProject));

        Actor existingActor = new Actor();
        existingActor.setId(10);
        existingActor.setName("Customer");

        // "Customer" already exists -> return the existing one
        when(actorMapper.findByNameAndProjectId("Customer", 1))
                .thenReturn(Optional.of(existingActor));

        UseCase newUseCase = new UseCase();
        newUseCase.setName("Place Order");

        // Act
        useCaseService.saveUseCase(newUseCase, 1, "Customer");

        // Assert — the existing actor was used, not a new one
        assertEquals(10, newUseCase.getActors().get(0).getId());
        // actorMapper.save should NOT have been called for a new actor
        // (only useCaseMapper.save is called)
        verify(useCaseMapper).save(newUseCase);
    }

    /**
     * saveUseCase with an empty actors string should save with an empty actors list.
     */
    @Test
    void saveUseCase_shouldSaveWithEmptyActorsList_whenActorsInputIsEmpty() {
        // Arrange
        when(projectMapper.findById(1)).thenReturn(Optional.of(testProject));

        UseCase newUseCase = new UseCase();
        newUseCase.setName("View Report");

        // Act — pass empty string for actors
        useCaseService.saveUseCase(newUseCase, 1, "");

        // Assert — actors list is empty
        assertTrue(newUseCase.getActors().isEmpty());
        verify(useCaseMapper).save(newUseCase);
    }

    // -------------------------------------------------------------------------
    // deleteUseCase — US10
    // -------------------------------------------------------------------------

    @Test
    void deleteUseCase_shouldDelete_whenUseCaseBelongsToProject() {
        // Arrange
        when(useCaseMapper.findByIdAndProjectId(5, 1))
                .thenReturn(Optional.of(testUseCase));

        // Act
        useCaseService.deleteUseCase(5, 1);

        // Assert
        verify(useCaseMapper).delete(testUseCase);
    }

    @Test
    void deleteUseCase_shouldThrow_whenUseCaseNotFound() {
        // Arrange
        when(useCaseMapper.findByIdAndProjectId(99, 1))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class,
                () -> useCaseService.deleteUseCase(99, 1));
    }

    // -------------------------------------------------------------------------
    // getActorsAsString — helper
    // -------------------------------------------------------------------------

    /**
     * getActorsAsString should convert [Actor("Customer"), Actor("Admin")]
     * into the string "Customer, Admin".
     */
    @Test
    void getActorsAsString_shouldReturnCommaSeparatedNames() {
        // Arrange
        Actor a1 = new Actor(); a1.setName("Customer");
        Actor a2 = new Actor(); a2.setName("Admin");
        testUseCase.setActors(Arrays.asList(a1, a2));

        // Act
        String result = useCaseService.getActorsAsString(testUseCase);

        // Assert
        assertEquals("Customer, Admin", result);
    }

    /**
     * getActorsAsString should return an empty string when actors list is empty.
     */
    @Test
    void getActorsAsString_shouldReturnEmpty_whenNoActors() {
        // Arrange
        testUseCase.setActors(Arrays.asList());

        // Act
        String result = useCaseService.getActorsAsString(testUseCase);

        // Assert
        assertEquals("", result);
    }
}
