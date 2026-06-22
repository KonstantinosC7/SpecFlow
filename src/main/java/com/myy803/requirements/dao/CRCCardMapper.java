package com.myy803.requirements.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myy803.requirements.model.CRCCard;

/**
 * findByProjectId:
 *   -> SELECT * FROM crc_cards WHERE project_id = ?
 *   Used by US11 view / US14 list: show all CRC cards of a project.
 *
 * findByIdAndProjectId:
 *   -> SELECT * FROM crc_cards WHERE id = ? AND project_id = ?
 *   Used for safe access before edit (US12) or delete (US14):
 *   ensures the card belongs to the expected project.
 */
@Repository
public interface CRCCardMapper extends JpaRepository<CRCCard, Integer> {

    /** US11 view / US14 — all CRC cards that belong to a project */
    List<CRCCard> findByProjectId(int projectId);

    /** US12 / US14 — fetch one card only if it belongs to the given project */
    Optional<CRCCard> findByIdAndProjectId(int id, int projectId);
}