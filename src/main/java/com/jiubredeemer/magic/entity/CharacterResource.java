package com.jiubredeemer.magic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "character_resource", schema = "magic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CharacterResource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID spellBookId;
    private String name;
    private String icon;
    private Long maxCount;
    private Long currentCount;
    @Enumerated(EnumType.STRING)
    private ChargesRefillEnum refillRestType;
}
