package com.myy803.requirements.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the CRCCard domain class.
 *
 * Tests verify that all CRC card fields (className, responsibilities,
 * collaborations) are correctly stored and retrieved, and that the
 * linkedUseCases relationship (US13) works as expected.
 */
public class CRCCardTest {

    private CRCCard crcCard;
    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1);
        project.setName("Banking App");

        crcCard = CRCCard.builder()
                .id(3)
                .className("UserAccount")
                .responsibilities("Stores user credentials\nValidates login")
                .collaborations("AuthService, SessionManager")
                .project(project)
                .build();
    }

    // -------------------------------------------------------------------------
    // Getter tests
    // -------------------------------------------------------------------------

    @Test
    void getId_shouldReturnCorrectId() {
        assertEquals(3, crcCard.getId());
    }

    @Test
    void getClassName_shouldReturnCorrectClassName() {
        assertEquals("UserAccount", crcCard.getClassName());
    }

    @Test
    void getResponsibilities_shouldReturnResponsibilities() {
        assertEquals("Stores user credentials\nValidates login",
                crcCard.getResponsibilities());
    }

    @Test
    void getCollaborations_shouldReturnCollaborations() {
        assertEquals("AuthService, SessionManager",
                crcCard.getCollaborations());
    }

    @Test
    void getProject_shouldReturnLinkedProject() {
        assertNotNull(crcCard.getProject());
        assertEquals("Banking App", crcCard.getProject().getName());
    }

    // -------------------------------------------------------------------------
    // Setter tests
    // -------------------------------------------------------------------------

    @Test
    void setClassName_shouldUpdateClassName() {
        crcCard.setClassName("AuthService");
        assertEquals("AuthService", crcCard.getClassName());
    }

    @Test
    void setResponsibilities_shouldUpdateResponsibilities() {
        crcCard.setResponsibilities("New responsibility");
        assertEquals("New responsibility", crcCard.getResponsibilities());
    }

    @Test
    void setCollaborations_shouldUpdateCollaborations() {
        crcCard.setCollaborations("UserRepo, TokenService");
        assertEquals("UserRepo, TokenService", crcCard.getCollaborations());
    }

    // -------------------------------------------------------------------------
    // LinkedUseCases relationship — US13
    // -------------------------------------------------------------------------

    /**
     * US13 — linkedUseCases should store the list set by the service.
     */
    @Test
    void setLinkedUseCases_shouldStoreAndReturnList() {
        UseCase uc1 = new UseCase(); uc1.setId(1); uc1.setName("User Login");
        UseCase uc2 = new UseCase(); uc2.setId(2); uc2.setName("Transfer Funds");

        crcCard.setLinkedUseCases(Arrays.asList(uc1, uc2));

        assertEquals(2, crcCard.getLinkedUseCases().size());
        assertEquals("User Login", crcCard.getLinkedUseCases().get(0).getName());
        assertEquals("Transfer Funds", crcCard.getLinkedUseCases().get(1).getName());
    }

    @Test
    void setLinkedUseCases_shouldHandleEmptyList() {
        crcCard.setLinkedUseCases(Arrays.asList());

        assertNotNull(crcCard.getLinkedUseCases());
        assertTrue(crcCard.getLinkedUseCases().isEmpty());
    }

    // -------------------------------------------------------------------------
    // @NoArgsConstructor test
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_shouldCreateEmptyCRCCard() {
        CRCCard empty = new CRCCard();
        assertNotNull(empty);
        assertEquals(0, empty.getId());
        assertNull(empty.getClassName());
    }
}
