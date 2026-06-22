package com.myy803.requirements.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the UseCase domain class.
 *
 * Tests verify that all use case fields (name, description, preconditions,
 * mainFlow, alternativeFlow, postconditions) are correctly stored and
 * retrieved, and that the actors relationship works as expected.
 */
public class UseCaseTest {

    private UseCase useCase;
    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1);
        project.setName("Banking App");

        useCase = UseCase.builder()
                .id(5)
                .name("User Login")
                .description("Allows user to authenticate")
                .preconditions("User must have a registered account")
                .mainFlow("1. User enters credentials. 2. System validates.")
                .alternativeFlow("2a. Invalid credentials: show error.")
                .postconditions("User is authenticated")
                .project(project)
                .build();
    }

    // -------------------------------------------------------------------------
    // Getter tests
    // -------------------------------------------------------------------------

    @Test
    void getId_shouldReturnCorrectId() {
        assertEquals(5, useCase.getId());
    }

    @Test
    void getName_shouldReturnCorrectName() {
        assertEquals("User Login", useCase.getName());
    }

    @Test
    void getDescription_shouldReturnDescription() {
        assertEquals("Allows user to authenticate", useCase.getDescription());
    }

    @Test
    void getPreconditions_shouldReturnPreconditions() {
        assertEquals("User must have a registered account", useCase.getPreconditions());
    }

    @Test
    void getMainFlow_shouldReturnMainFlow() {
        assertEquals("1. User enters credentials. 2. System validates.",
                useCase.getMainFlow());
    }

    @Test
    void getAlternativeFlow_shouldReturnAlternativeFlow() {
        assertEquals("2a. Invalid credentials: show error.",
                useCase.getAlternativeFlow());
    }

    @Test
    void getPostconditions_shouldReturnPostconditions() {
        assertEquals("User is authenticated", useCase.getPostconditions());
    }

    @Test
    void getProject_shouldReturnLinkedProject() {
        assertNotNull(useCase.getProject());
        assertEquals("Banking App", useCase.getProject().getName());
    }

    // -------------------------------------------------------------------------
    // Setter tests
    // -------------------------------------------------------------------------

    @Test
    void setName_shouldUpdateName() {
        useCase.setName("Transfer Funds");
        assertEquals("Transfer Funds", useCase.getName());
    }

    @Test
    void setMainFlow_shouldUpdateMainFlow() {
        useCase.setMainFlow("Updated flow");
        assertEquals("Updated flow", useCase.getMainFlow());
    }

    // -------------------------------------------------------------------------
    // Actors relationship
    // -------------------------------------------------------------------------

    /**
     * The actors list should be settable and retrievable.
     * This mirrors how UseCaseServiceImpl sets actors after parsing the form input.
     */
    @Test
    void setActors_shouldStoreAndReturnActors() {
        Actor a1 = new Actor(); a1.setId(1); a1.setName("Customer");
        Actor a2 = new Actor(); a2.setId(2); a2.setName("Admin");

        useCase.setActors(Arrays.asList(a1, a2));

        assertEquals(2, useCase.getActors().size());
        assertEquals("Customer", useCase.getActors().get(0).getName());
        assertEquals("Admin", useCase.getActors().get(1).getName());
    }

    @Test
    void setActors_shouldHandleEmptyList() {
        useCase.setActors(Arrays.asList());

        assertNotNull(useCase.getActors());
        assertTrue(useCase.getActors().isEmpty());
    }

    // -------------------------------------------------------------------------
    // @NoArgsConstructor test
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_shouldCreateEmptyUseCase() {
        UseCase empty = new UseCase();
        assertNotNull(empty);
        assertEquals(0, empty.getId());
        assertNull(empty.getName());
    }
}
