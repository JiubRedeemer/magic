package com.jiubredeemer.magic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "spell_bundle", schema = "magic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpellBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "img_url")
    private String imgUrl;

    /** null = системный (официальный) бандл. */
    @Column(name = "owner_user_id")
    private UUID ownerUserId;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    @Column(name = "price_crystals", nullable = false)
    private Integer priceCrystals = 0;
}
