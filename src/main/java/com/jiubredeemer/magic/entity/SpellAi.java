package com.jiubredeemer.magic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "spell_ai", schema = "magic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpellAi {

    @Id
    private UUID id;

    @PrePersist
    void assignIdIfNull() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @Type(JsonbMapType.class)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, String> name;

    @Type(JsonbMapType.class)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, String> aliasName = name;

    @Column(nullable = false)
    private String level;

    @Column(name = "\"class\"", nullable = true)
    private String spellClass;

    @Column(nullable = false)
    private String school;

    private Boolean ritual;

    @Column(nullable = false)
    private Boolean customization = false;

    @Column(name = "damage_type")
    private String damageType;

    @Column(name = "heal_type")
    private String healType;

    @Column(name = "saving_throw")
    private String savingThrow;

    @Column(name = "use_time")
    private String useTime;

    private String distance;

    private String duration;

    private String components;

    @Column(name = "material_components", columnDefinition = "text")
    private String materialComponents;

    @Column(columnDefinition = "text")
    private String description;

    /** English description for prompts / display; optional manual fill to avoid translation. */
    @Column(name = "eng_description", columnDefinition = "text")
    private String engDescription;

    @Column(name = "ttg_slug", unique = true)
    private String ttgSlug;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "img_url", nullable = true)
    private String imgUrl;
}
