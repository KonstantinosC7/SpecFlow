package com.myy803.requirements.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myy803.requirements.model.Comment;

/**
 * findByUseCaseId:
 *   -> SELECT * FROM comments WHERE use_case_id = ?
 *   Returns all comments on a specific use case.
 *
 * findByCrcCardId:
 *   -> SELECT * FROM comments WHERE crc_card_id = ?
 *   Returns all comments on a specific CRC card.
 *
 * Both queries return results ordered by creation time
 * (oldest first) using Spring Data's OrderBy keyword.
 */
@Repository
public interface CommentMapper extends JpaRepository<Comment, Integer> {

    /** Returns all comments for a use case, oldest first */
    List<Comment> findByUseCaseIdOrderByCreatedAtAsc(int useCaseId);

    /** Returns all comments for a CRC card, oldest first */
    List<Comment> findByCrcCardIdOrderByCreatedAtAsc(int crcCardId);
}
