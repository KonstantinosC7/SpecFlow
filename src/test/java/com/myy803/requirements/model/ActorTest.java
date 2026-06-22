package com.myy803.requirements.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Actor domain class.
 *
 * Actor is a simple entity with three fields: id, name, and project.
 * These tests verify that Lombok @Builder, @Getter, @Setter, and
 * @NoArgsConstructor work correctly on all fields.
 */
public class ActorTest {

    private Actor actor;
    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1);
        project.setName("Banking App");

        actor = Actor.builder()
                .id(10)
                .name("Customer")
                .project(project)
                .build();
    }

    // -------------------------------------------------------------------------
    // Getter tests
    // -------------------------------------------------------------------------

    @Test
    void getId_shouldReturnCorrectId() {
        assertEquals(10, actor.getId());
    }

    @Test
    void getName_shouldReturnCorrectName() {
        assertEquals("Customer", actor.getName());
    }

    @Test
    void getProject_shouldReturnLinkedProject() {
        assertNotNull(actor.getProject());
        assertEquals("Banking App", actor.getProject().getName());
    }

    // -------------------------------------------------------------------------
    // Setter tests
    // -------------------------------------------------------------------------

    @Test
    void setName_shouldUpdateName() {
        actor.setName("Admin");
        assertEquals("Admin", actor.getName());
    }

    @Test
    void setProject_shouldUpdateProject() {
        Project other = new Project();
        other.setId(2);
        other.setName("E-Commerce");

        actor.setProject(other);

        assertEquals(2, actor.getProject().getId());
        assertEquals("E-Commerce", actor.getProject().getName());
    }

    // -------------------------------------------------------------------------
    // @NoArgsConstructor test
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_shouldCreateEmptyActor() {
        Actor empty = new Actor();
        assertNotNull(empty);
        assertEquals(0, empty.getId());
        assertNull(empty.getName());
    }

    // -------------------------------------------------------------------------
    // @Builder test — verify all fields set via builder
    // -------------------------------------------------------------------------

    @Test
    void builder_shouldSetAllFields() {
        Actor built = Actor.builder()
                .id(99)
                .name("Payment System")
                .project(project)
                .build();

        assertEquals(99, built.getId());
        assertEquals("Payment System", built.getName());
        assertNotNull(built.getProject());
    }
}
