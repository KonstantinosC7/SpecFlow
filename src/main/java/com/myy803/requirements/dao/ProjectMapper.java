package com.myy803.requirements.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myy803.requirements.model.Project;

/**
 * findByUserId — Spring Data JPA generates:
 *   SELECT * FROM projects WHERE user_id = ?
 * This is the primary query for US4 (list all projects for the logged-in user).
 *
 * findByIdAndUserId — used for safe access: we only let a user fetch/delete
 * a project if it belongs to them (prevents one user accessing another's data).
 */
@Repository
public interface ProjectMapper extends JpaRepository<Project, Integer> {

    /** Returns all projects owned by a specific user — US4 */
    List<Project> findByUserId(int userId);

    /** Safely fetches a project only if it belongs to the given user */
    java.util.Optional<Project> findByIdAndUserId(int id, int userId);
}
