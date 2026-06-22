package com.myy803.requirements.service;

import java.util.List;

import com.myy803.requirements.model.Comment;

/**
 * Service interface for comment operations (US19).
 *
 * addCommentToUseCase:
 *   Creates and saves a comment linked to a specific use case.
 *   The author is the currently logged-in user (passed as userId).
 *
 * addCommentToCRCCard:
 *   Same but linked to a CRC card.
 *
 * getCommentsForUseCase / getCommentsForCRCCard:
 *   Returns all comments for a use case or CRC card, ordered oldest first.
 *   Used to display the comment thread on the comments page.
 */
public interface CommentService {

    /**
     * US19 — Adds a comment to a use case.
     *
     * @param useCaseId the use case to comment on
     * @param authorId  the id of the user posting the comment
     * @param text      the comment text
     */
    void addCommentToUseCase(int useCaseId, int authorId, String text);

    /**
     * US19 — Adds a comment to a CRC card.
     *
     * @param crcCardId the CRC card to comment on
     * @param authorId  the id of the user posting the comment
     * @param text      the comment text
     */
    void addCommentToCRCCard(int crcCardId, int authorId, String text);

    /** Returns all comments for a use case, oldest first */
    List<Comment> getCommentsForUseCase(int useCaseId);

    /** Returns all comments for a CRC card, oldest first */
    List<Comment> getCommentsForCRCCard(int crcCardId);
}
