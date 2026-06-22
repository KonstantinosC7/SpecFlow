package com.myy803.requirements.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myy803.requirements.dao.CRCCardMapper;
import com.myy803.requirements.dao.ProjectMapper;
import com.myy803.requirements.dao.UseCaseMapper;
import com.myy803.requirements.model.CRCCard;
import com.myy803.requirements.model.Project;
import com.myy803.requirements.model.UseCase;

/**
 * Implementation of CRCCardService.
 *
 * === US13 LINK TO USE CASES — HOW IT WORKS ===
 *
 * The HTML form displays all use cases of the project as checkboxes.
 * Each checkbox has value="${uc.id}". When the user checks some boxes
 * and submits the form, the browser sends a list of integer IDs.
 *
 * In saveCRCCard() we:
 *   1. Load each UseCase entity from DB using the submitted IDs.
 *   2. Build a List<UseCase> from them.
 *   3. Set that list on the CRCCard.linkedUseCases field.
 *   4. Save the CRCCard — JPA updates the join table (crc_card_use_cases)
 *      automatically because of the @ManyToMany mapping.
 *
 * If linkedUseCaseIds is null or empty (no checkboxes ticked),
 * we set an empty list — the card is saved with no linked use cases.
 *
 * === WHY WE INJECT UseCaseMapper HERE ===
 * We need to load full UseCase objects from DB using the IDs the form sent.
 * UseCaseMapper.findById() does exactly that.
 */
@Service
public class CRCCardServiceImpl implements CRCCardService {

    @Autowired
    private CRCCardMapper crcCardMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private UseCaseMapper useCaseMapper;

    /**
     * US11 view / US14 — returns all CRC cards for the given project.
     */
    @Override
    public List<CRCCard> getCRCCardsByProject(int projectId) {
        return crcCardMapper.findByProjectId(projectId);
    }

    /**
     * US11 / US12 / US13 — saves a CRC card (create or update).
     *
     * Step 1: Load the Project entity and set it on the card.
     *         Without this, the project_id FK would be null.
     *
     * Step 2: Resolve linked use cases from the submitted IDs.
     *         We iterate the ID list and load each UseCase from DB.
     *         Missing IDs (shouldn't happen in normal use) are silently skipped.
     *
     * Step 3: Set the resolved list on the card and save.
     *         JPA inserts or updates based on whether card.id == 0.
     *         JPA also manages the crc_card_use_cases join table automatically.
     */
    @Override
    public void saveCRCCard(CRCCard card, int projectId, List<Integer> linkedUseCaseIds) {

        // Step 1: associate with project
        Project project = projectMapper.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));
        card.setProject(project);

        // Step 2: resolve use case links
        List<UseCase> linkedUseCases = new ArrayList<>();

        if (linkedUseCaseIds != null && !linkedUseCaseIds.isEmpty()) {
            for (int ucId : linkedUseCaseIds) {
                // findById returns Optional — only add if the use case was actually found
                useCaseMapper.findById(ucId).ifPresent(linkedUseCases::add);
            }
        }

        // Step 3: set links and save
        card.setLinkedUseCases(linkedUseCases);
        crcCardMapper.save(card);
    }

    /**
     * US14 — deletes a CRC card.
     * findByIdAndProjectId verifies the card belongs to this project
     * before deleting (safety check).
     */
    @Override
    public void deleteCRCCard(int cardId, int projectId) {
        CRCCard card = crcCardMapper.findByIdAndProjectId(cardId, projectId)
                .orElseThrow(() -> new RuntimeException(
                        "CRC card not found or access denied: " + cardId));
        crcCardMapper.delete(card);
    }

    /**
     * US12 — loads a single CRC card for the edit form.
     * Verifies the card belongs to projectId before returning.
     */
    @Override
    public CRCCard getCRCCardByIdAndProject(int cardId, int projectId) {
        return crcCardMapper.findByIdAndProjectId(cardId, projectId)
                .orElseThrow(() -> new RuntimeException(
                        "CRC card not found or access denied: " + cardId));
    }
}