package com.myy803.requirements.dao;

import java.util.List;
import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import com.myy803.requirements.model.Actor;
 
/**
 * Data Mapper for the Actor entity.
 *
 * findByNameAndProjectId:
 *   Used when saving a use case — before creating a new Actor we check
 *   whether one with the same name already exists in this project.
 *   This avoids duplicate Actor rows for the same person/system.
 *
 * findByProjectId:
 *   Returns all actors that belong to a project (useful for listings).
 */
@Repository
public interface ActorMapper extends JpaRepository<Actor, Integer> {
 
    /** Find an actor by name within a specific project (to avoid duplicates) */
    Optional<Actor> findByNameAndProjectId(String name, int projectId);
 
    /** Find all actors that belong to a project */
    List<Actor> findByProjectId(int projectId);
}
