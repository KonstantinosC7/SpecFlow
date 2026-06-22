package com.myy803.requirements.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "crc_cards")
public class CRCCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    @Column(name = "responsibilities", columnDefinition = "TEXT")
    private String responsibilities;

    @Column(name = "collaborations", columnDefinition = "TEXT")
    private String collaborations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "crc_card_use_cases",
        joinColumns = @JoinColumn(name = "crc_card_id"),
        inverseJoinColumns = @JoinColumn(name = "use_case_id")
    )
    private List<UseCase> linkedUseCases;

    @OneToMany(mappedBy = "crcCard", cascade = CascadeType.ALL,
               fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments;
}