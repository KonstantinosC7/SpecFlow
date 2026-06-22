-- =============================================================================
-- requirements_db — Complete Database Schema (updated)
-- Web Based Requirements Specification & Analysis App
-- =============================================================================
-- HOW TO USE:
--     Keep spring.jpa.hibernate.ddl-auto=update in application.properties.
--     Hibernate reads your @Entity classes and creates/alters tables automatically.
--     You do NOT need to run this file manually.
-- =============================================================================

CREATE DATABASE IF NOT EXISTS requirements_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE requirements_db;

-- =============================================================================
-- TABLE: users
-- Maps to: User.java
-- Supports: US1 (register/login), US2 (profile management), US3 (logout)
-- =============================================================================
CREATE TABLE IF NOT EXISTS users (
    id         INT          NOT NULL AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL,   -- BCrypt hash (never plain text)
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER',  -- "USER" or "ADMIN"

    PRIMARY KEY (id),
    UNIQUE KEY uq_users_username (username),
    UNIQUE KEY uq_users_email    (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =============================================================================
-- TABLE: projects
-- Maps to: Project.java
-- Supports: US4 (list), US5 (create), US6 (delete)
-- =============================================================================
CREATE TABLE IF NOT EXISTS projects (
    id          INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id     INT          NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_projects_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE   -- deleting a user removes all their projects
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =============================================================================
-- TABLE: use_cases
-- Maps to: UseCase.java
-- Supports: US7 (create), US8 (update), US9 (list), US10 (delete)
-- =============================================================================
CREATE TABLE IF NOT EXISTS use_cases (
    id               INT          NOT NULL AUTO_INCREMENT,
    name             VARCHAR(100) NOT NULL,
    description      TEXT,
    preconditions    TEXT,
    main_flow        TEXT,
    alternative_flow TEXT,
    postconditions   TEXT,
    project_id       INT          NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_use_cases_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE   -- deleting a project removes all its use cases
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =============================================================================
-- TABLE: actors
-- Maps to: Actor.java
-- An actor belongs to a project and participates in one or more use cases.
-- Supports: US7 (actors are part of use case definition)
--
-- BUG FIX NOTE:
-- This table has ON DELETE CASCADE on project_id.
-- The bug was that Project.java was missing a @OneToMany for actors,
-- so JPA did not cascade the delete. The fix is in Project.java.
-- The SQL ON DELETE CASCADE here acts as a database-level safety net.
-- =============================================================================
CREATE TABLE IF NOT EXISTS actors (
    id         INT          NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    project_id INT          NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_actors_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE   -- deleting a project removes its actors
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =============================================================================
-- TABLE: use_case_actors  (join table — many-to-many)
-- One use case can have many actors; one actor can appear in many use cases.
-- =============================================================================
CREATE TABLE IF NOT EXISTS use_case_actors (
    use_case_id INT NOT NULL,
    actor_id    INT NOT NULL,

    PRIMARY KEY (use_case_id, actor_id),
    CONSTRAINT fk_uca_use_case
        FOREIGN KEY (use_case_id) REFERENCES use_cases(id) ON DELETE CASCADE,
    CONSTRAINT fk_uca_actor
        FOREIGN KEY (actor_id)    REFERENCES actors(id)    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =============================================================================
-- TABLE: crc_cards
-- Maps to: CRCCard.java
-- Supports: US11 (create), US12 (update), US13 (link to use cases), US14 (delete)
-- =============================================================================
CREATE TABLE IF NOT EXISTS crc_cards (
    id               INT          NOT NULL AUTO_INCREMENT,
    class_name       VARCHAR(100) NOT NULL,
    responsibilities TEXT,
    collaborations   TEXT,
    project_id       INT          NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_crc_cards_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =============================================================================
-- TABLE: crc_card_use_cases  (join table — many-to-many)
-- US13: Links CRC cards to the use cases they support.
-- =============================================================================
CREATE TABLE IF NOT EXISTS crc_card_use_cases (
    crc_card_id INT NOT NULL,
    use_case_id INT NOT NULL,

    PRIMARY KEY (crc_card_id, use_case_id),
    CONSTRAINT fk_ccuc_crc_card
        FOREIGN KEY (crc_card_id) REFERENCES crc_cards(id)  ON DELETE CASCADE,
    CONSTRAINT fk_ccuc_use_case
        FOREIGN KEY (use_case_id) REFERENCES use_cases(id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =============================================================================
-- TABLE: project_shares                                           *** NEW ***
-- Maps to: ProjectShare.java
-- Supports: US18 — share a project with teammates
--
-- Each row means: project {project_id} is shared with user {shared_with_user_id}.
-- The project owner is stored in projects.user_id — NOT here.
-- This table only records the additional collaborators.
--
-- UNIQUE KEY: prevents sharing the same project with the same person twice.
-- ON DELETE CASCADE on both FKs: if a project or a user is deleted,
--   their share records are automatically removed.
-- =============================================================================
CREATE TABLE IF NOT EXISTS project_shares (
    id                   INT NOT NULL AUTO_INCREMENT,
    project_id           INT NOT NULL,
    shared_with_user_id  INT NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uq_project_shares (project_id, shared_with_user_id),

    CONSTRAINT fk_ps_project
        FOREIGN KEY (project_id)          REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_ps_shared_with
        FOREIGN KEY (shared_with_user_id) REFERENCES users(id)    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =============================================================================
-- TABLE: comments                                                 *** NEW ***
-- Maps to: Comment.java
-- Supports: US19 — comment on use cases and CRC cards
--
-- A comment belongs to EITHER a use case OR a CRC card (never both).
-- The two FK columns (use_case_id, crc_card_id) are both nullable:
--   - Use case comment:  use_case_id = <value>, crc_card_id = NULL
--   - CRC card comment:  use_case_id = NULL,    crc_card_id = <value>
--
-- author_user_id: the developer who wrote the comment.
-- created_at: set automatically by @CreationTimestamp in Java /
--             DEFAULT CURRENT_TIMESTAMP in SQL.
--
-- ON DELETE CASCADE:
--   Deleting a use case removes its comments.
--   Deleting a CRC card removes its comments.
--   Deleting a user removes all their comments.
-- =============================================================================
CREATE TABLE IF NOT EXISTS comments (
    id              INT      NOT NULL AUTO_INCREMENT,
    text            TEXT     NOT NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    author_user_id  INT      NOT NULL,
    use_case_id     INT          NULL,   -- NULL if this is a CRC card comment
    crc_card_id     INT          NULL,   -- NULL if this is a use case comment

    PRIMARY KEY (id),

    CONSTRAINT fk_comments_author
        FOREIGN KEY (author_user_id) REFERENCES users(id)      ON DELETE CASCADE,
    CONSTRAINT fk_comments_use_case
        FOREIGN KEY (use_case_id)    REFERENCES use_cases(id)  ON DELETE CASCADE,
    CONSTRAINT fk_comments_crc_card
        FOREIGN KEY (crc_card_id)    REFERENCES crc_cards(id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;