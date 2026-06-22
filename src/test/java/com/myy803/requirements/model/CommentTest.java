package com.myy803.requirements.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Comment domain class (US19).
 *
 * Two scenarios are tested:
 *   1. A comment linked to a UseCase (crcCard is null).
 *   2. A comment linked to a CRCCard (useCase is null).
 *
 * This verifies the mutual-exclusivity design of the two FK fields.
 */
public class CommentTest {

    private User author;
    private UseCase useCase;
    private CRCCard crcCard;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1);
        author.setUsername("alexdev");
        author.setFirstName("Alex");
        author.setLastName("Dev");

        useCase = new UseCase();
        useCase.setId(5);
        useCase.setName("User Login");

        crcCard = new CRCCard();
        crcCard.setId(3);
        crcCard.setClassName("UserAccount");
    }

    // -------------------------------------------------------------------------
    // Use case comment tests
    // -------------------------------------------------------------------------

    /**
     * A comment on a use case must have useCase set and crcCard null.
     */
    @Test
    void useCaseComment_shouldHaveUseCaseSetAndCrcCardNull() {
        Comment comment = Comment.builder()
                .id(1)
                .text("Should we add OAuth support?")
                .author(author)
                .useCase(useCase)
                .crcCard(null)
                .build();

        assertNotNull(comment.getUseCase());
        assertNull(comment.getCrcCard());
        assertEquals("User Login", comment.getUseCase().getName());
    }

    @Test
    void useCaseComment_shouldReturnCorrectText() {
        Comment comment = new Comment();
        comment.setText("This precondition needs clarification.");
        comment.setAuthor(author);
        comment.setUseCase(useCase);

        assertEquals("This precondition needs clarification.", comment.getText());
    }

    @Test
    void useCaseComment_shouldReturnCorrectAuthor() {
        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setUseCase(useCase);

        assertNotNull(comment.getAuthor());
        assertEquals("alexdev", comment.getAuthor().getUsername());
    }

    // -------------------------------------------------------------------------
    // CRC card comment tests
    // -------------------------------------------------------------------------

    /**
     * A comment on a CRC card must have crcCard set and useCase null.
     */
    @Test
    void crcCardComment_shouldHaveCrcCardSetAndUseCaseNull() {
        Comment comment = Comment.builder()
                .id(2)
                .text("The collaborations list is missing OrderService.")
                .author(author)
                .useCase(null)
                .crcCard(crcCard)
                .build();

        assertNotNull(comment.getCrcCard());
        assertNull(comment.getUseCase());
        assertEquals("UserAccount", comment.getCrcCard().getClassName());
    }

    @Test
    void crcCardComment_shouldReturnCorrectText() {
        Comment comment = new Comment();
        comment.setText("Consider splitting responsibilities.");
        comment.setAuthor(author);
        comment.setCrcCard(crcCard);

        assertEquals("Consider splitting responsibilities.", comment.getText());
    }

    // -------------------------------------------------------------------------
    // createdAt field
    // -------------------------------------------------------------------------

    /**
     * createdAt is set by @CreationTimestamp on save.
     * Here we just test the setter/getter works correctly.
     */
    @Test
    void setCreatedAt_shouldStoreAndReturnTimestamp() {
        Comment comment = new Comment();
        LocalDateTime now = LocalDateTime.of(2026, 3, 16, 10, 30);
        comment.setCreatedAt(now);

        assertEquals(now, comment.getCreatedAt());
    }

    // -------------------------------------------------------------------------
    // @NoArgsConstructor test
    // -------------------------------------------------------------------------

    @Test
    void noArgsConstructor_shouldCreateEmptyComment() {
        Comment empty = new Comment();
        assertNotNull(empty);
        assertEquals(0, empty.getId());
        assertNull(empty.getText());
        assertNull(empty.getUseCase());
        assertNull(empty.getCrcCard());
    }
}
