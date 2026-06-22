package com.myy803.requirements.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "project_shares",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"project_id", "shared_with_user_id"}
    )
)
public class ProjectShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    /**
     * The project being shared.
     * FetchType.LAZY: don't load the full Project unless needed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * The user who received access.
     * Named "sharedWith" to make the intent clear:
     *   "this project is shared WITH this user".
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_with_user_id", nullable = false)
    private User sharedWith;
}
