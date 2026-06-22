package com.myy803.requirements.service;

import java.util.List;

import com.myy803.requirements.model.CRCCard;

/**
 * Service interface for CRC card operations.
 *
 * US11 — saveCRCCard        : create a new CRC card for a project
 * US12 — saveCRCCard        : update an existing CRC card (same method,
 *                             JPA decides INSERT or UPDATE based on id)
 * US13 — saveCRCCard        : also handled here — the linkedUseCaseIds
 *                             parameter carries the selected use case links
 * US14 — deleteCRCCard      : delete a CRC card
 *         getCRCCardsByProject : list all cards (needed to show the list for delete)
 *
 * === linkedUseCaseIds explained ===
 * In the HTML form, each use case is shown as a checkbox.
 * When the form is submitted, the browser sends the IDs of all
 * checked boxes as a list of integers.
 * The service receives this list and uses it to load the actual
 * UseCase entities and set them on the CRCCard.linkedUseCases field.
 * If no boxes are checked, the list is empty (no link — also valid).
 */
public interface CRCCardService {

    /** US11 view / US14 — returns all CRC cards for a project */
    List<CRCCard> getCRCCardsByProject(int projectId);

    /**
     * US11 / US12 / US13 — saves (create or update) a CRC card.
     *
     * @param card             the CRCCard object populated from the form
     * @param projectId        the project this card belongs to
     * @param linkedUseCaseIds list of use case IDs selected in the form (US13)
     *                         may be null or empty if no use cases are linked
     */
    void saveCRCCard(CRCCard card, int projectId, List<Integer> linkedUseCaseIds);

    /** US14 — deletes a CRC card, verifying it belongs to projectId */
    void deleteCRCCard(int cardId, int projectId);

    /** US12 — loads a single CRC card for the edit form */
    CRCCard getCRCCardByIdAndProject(int cardId, int projectId);
}