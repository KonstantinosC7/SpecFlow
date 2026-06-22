package com.myy803.requirements.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import com.myy803.requirements.dao.CRCCardMapper;
import com.myy803.requirements.dao.ProjectMapper;
import com.myy803.requirements.dao.UseCaseMapper;
import com.myy803.requirements.model.CRCCard;
import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.UseCase;

/**
 * Unit tests for CRCCardServiceImpl.
 *
 * Focus areas:
 *   getCRCCardsByProject  (US11 view)
 *   saveCRCCard           (US11 create / US12 update / US13 link)
 *   deleteCRCCard         (US14)
 *   getCRCCardByIdAndProject (used by edit form)
 */
@ExtendWith(MockitoExtension.class)
public class CRCCardServiceImplTest {

    @Mock
    private CRCCardMapper crcCardMapper;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UseCaseMapper useCaseMapper;

    @InjectMocks
    private CRCCardServiceImpl crcCardService;

    private Project testProject;
    private CRCCard testCard;
    private UseCase testUseCase;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setId(1);
        testProject.setName("Banking App");

        testCard = new CRCCard();
        testCard.setId(3);
        testCard.setClassName("UserAccount");
        testCard.setProject(testProject);

        testUseCase = new UseCase();
        testUseCase.setId(7);
        testUseCase.setName("User Login");
    }

    // -------------------------------------------------------------------------
    // getCRCCardsByProject — US11 view
    // -------------------------------------------------------------------------

    @Test
    void getCRCCardsByProject_shouldReturnList() {
        // Arrange
        when(crcCardMapper.findByProjectId(1)).thenReturn(Arrays.asList(testCard));

        // Act
        List<CRCCard> result = crcCardService.getCRCCardsByProject(1);

        // Assert
        assertEquals(1, result.size());
        assertEquals("UserAccount", result.get(0).getClassName());
    }

    // -------------------------------------------------------------------------
    // saveCRCCard — US11 / US12 / US13
    // -------------------------------------------------------------------------

    /**
     * US11 — saveCRCCard should set the project and save the card.
     * No use cases linked in this test.
     */
    @Test
    void saveCRCCard_shouldSetProjectAndSave_whenNoUseCaseLinked() {
        // Arrange
        when(projectMapper.findById(1)).thenReturn(Optional.of(testProject));

        CRCCard newCard = new CRCCard();
        newCard.setClassName("AuthService");

        // Act — pass null for linkedUseCaseIds (no checkboxes ticked)
        crcCardService.saveCRCCard(newCard, 1, null);

        // Assert — project was set
        assertEquals(testProject, newCard.getProject());
        // Assert — linkedUseCases is empty (not null)
        assertTrue(newCard.getLinkedUseCases().isEmpty());
        // Assert — saved
        verify(crcCardMapper).save(newCard);
    }

    /**
     * US13 — saveCRCCard should resolve use case IDs into UseCase entities
     * and link them to the card.
     */
    @Test
    void saveCRCCard_shouldLinkUseCases_whenIdsProvided() {
        // Arrange
        when(projectMapper.findById(1)).thenReturn(Optional.of(testProject));
        when(useCaseMapper.findById(7)).thenReturn(Optional.of(testUseCase));

        CRCCard newCard = new CRCCard();
        newCard.setClassName("AuthService");

        // Act — link use case with id=7
        crcCardService.saveCRCCard(newCard, 1, Arrays.asList(7));

        // Assert — one use case linked
        assertEquals(1, newCard.getLinkedUseCases().size());
        assertEquals("User Login", newCard.getLinkedUseCases().get(0).getName());
        verify(crcCardMapper).save(newCard);
    }

    /**
     * saveCRCCard should silently skip IDs that do not match any use case.
     * (Defensive: invalid ID in request should not crash the app.)
     */
    @Test
    void saveCRCCard_shouldSkipInvalidUseCaseIds() {
        // Arrange
        when(projectMapper.findById(1)).thenReturn(Optional.of(testProject));
        // ID 999 does not exist
        when(useCaseMapper.findById(999)).thenReturn(Optional.empty());

        CRCCard newCard = new CRCCard();
        newCard.setClassName("AuthService");

        // Act
        crcCardService.saveCRCCard(newCard, 1, Arrays.asList(999));

        // Assert — no use cases linked (the invalid id was skipped)
        assertTrue(newCard.getLinkedUseCases().isEmpty());
    }

    // -------------------------------------------------------------------------
    // deleteCRCCard — US14
    // -------------------------------------------------------------------------

    @Test
    void deleteCRCCard_shouldDelete_whenCardBelongsToProject() {
        // Arrange
        when(crcCardMapper.findByIdAndProjectId(3, 1))
                .thenReturn(Optional.of(testCard));

        // Act
        crcCardService.deleteCRCCard(3, 1);

        // Assert
        verify(crcCardMapper).delete(testCard);
    }

    @Test
    void deleteCRCCard_shouldThrow_whenCardNotFound() {
        // Arrange
        when(crcCardMapper.findByIdAndProjectId(99, 1))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class,
                () -> crcCardService.deleteCRCCard(99, 1));
    }

    // -------------------------------------------------------------------------
    // getCRCCardByIdAndProject
    // -------------------------------------------------------------------------

    @Test
    void getCRCCardByIdAndProject_shouldReturnCard_whenFound() {
        // Arrange
        when(crcCardMapper.findByIdAndProjectId(3, 1))
                .thenReturn(Optional.of(testCard));

        // Act
        CRCCard result = crcCardService.getCRCCardByIdAndProject(3, 1);

        // Assert
        assertEquals("UserAccount", result.getClassName());
    }

    @Test
    void getCRCCardByIdAndProject_shouldThrow_whenNotFound() {
        // Arrange
        when(crcCardMapper.findByIdAndProjectId(99, 1))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class,
                () -> crcCardService.getCRCCardByIdAndProject(99, 1));
    }
}
