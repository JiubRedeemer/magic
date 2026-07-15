package com.jiubredeemer.magic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "spell_bundle_purchase", schema = "magic")
@IdClass(SpellBundlePurchase.Key.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpellBundlePurchase {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Id
    @Column(name = "spell_bundle_id", nullable = false)
    private UUID spellBundleId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Key implements Serializable {
        private UUID userId;
        private UUID spellBundleId;
    }
}
