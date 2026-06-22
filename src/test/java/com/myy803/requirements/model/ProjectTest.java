package com.myy803.requirements.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Project domain class.
 *
 * Tests verify:
 *   - Lombok getters/setters work on all fields.
 *   - The owner (User) relationship is set and retrieved correctly.
 *   - The useCases and crcCards relationship lists are handled correctly.
 *   - @Builder works as expected.
 */
public class ProjectTest {

    private Project project;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setUsername("alexdev");

        project = Project.builder()
                .id(10)
                .name("Banking App")
                .description("An online banking system")
                .createdAt(LocalDateTime.of(2026, 1, 15, 10, 0))
                .user(owner)
                .build();
    }

    // -------------------------------------------------------------------------
    // Getter tests
    // -------------------------------------------------------------------------

    @Test
    void getId_shouldReturnCorrectId() {
        assertEquals(10, project.getId());
    }

    @Test
    void getName_shouldReturnCorrectName() {
        assertEquals("Banking App", project.getName());
    }

    @Test
    void getDescription_shouldReturnCorrectDescription() {
        assertEquals("An online banking system", project.getDescription());
    }

    @Test
    void getCreatedAt_shouldReturnCorrectTimestamp() {
        assertEquals(LocalDateTime.of(2026, 1, 15, 10, 0), project.getCreatedAt());
    }

    @Test
    void getUser_shouldReturnOwner() {
        assertNotNull(project.getUser());
        assertEquals("alexdev", project.getUser().getUsername());
    }

    // -------------------------------------------------------------------------
    // Setter tests
    // -------------------------------------------------------------------------

    @Test
    void setName_shouldUpdateName() {
        project.setName("E-Commerce System");
        assertEquals("E-Commerce System", project.getName());
    }

    @Test
    void setDescription_shouldUpdateDescription() {
        project.setDescription("Updated description");
        assertEquals("Updated description", project.getDescription());
    }

    @Test
    void setUser_shouldUpdateOwner() {
        User newOwner = new User();
        newOwner.setId(2);
        newOwner.setUsername("otherdev");

        project.setUser(newOwner);

        assertEquals(2, project.getUser().getId());
        assertEquals("otherdev", project.getUser().getUsername());
    }

    // -------------------------------------------------------------------------
    // Relationship list tests
    // -------------------------------------------------------------------------

    /**
     * Setting and retrieving the useCases list should work correctly.
     * In real usage Hibernate manages this list, but we test the setter/getter.
     */
    @Test
    void setUseCases_shouldStoreAndReturnList() {
        UseCase uc = new UseCase();
        uc.setId(1);
        uc.setName("User Login");

        project.setUseCases(Arrays.asList(uc));

        assertEquals(1, project.getUseCases().size());
        assertEquals("User Login", project.getUseCases().get(0).getName());
    }

    @Test
    void setCrcCards_shouldStoreAndReturnList() {
        CRCCard card = new CRCCard();
        card.setId(1);
        card.setClassName("UserAccount");

        project.setCrcCards(Arrays.asList(card));

        assertEquals(1, project.getCrcCards().size());
        assertEquals("UserAccount", project.getCrcCards().get(0).getClassName());
    }

    // -------------------------------------------------------------------------
    // @NoArgsConstructor test
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_shouldCreateEmptyProject() {
        Project emptyProject = new Project();
        assertNotNull(emptyProject);
        assertEquals(0, emptyProject.getId());
        assertNull(emptyProject.getName());
    }
}
