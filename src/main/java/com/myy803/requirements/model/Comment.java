package com.myy803.requirements.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    /**
     * The comment text.
     * TEXT type allows long comments without truncation.
     */
    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text;

    /**
     * @CreationTimestamp sets this automatically when the comment is saved.
     * updatable = false: once set, the timestamp never changes.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The developer who wrote this comment.
     * FetchType.LAZY: load the author only when getAuthor() is called.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_user_id", nullable = false)
    private User author;

    /**
     * The use case this comment belongs to.
     * Null if this is a CRC card comment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "use_case_id", nullable = true)
    private UseCase useCase;

    /**
     * The CRC card this comment belongs to.
     * Null if this is a use case comment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crc_card_id", nullable = true)
    private CRCCard crcCard;
}
