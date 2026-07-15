package com.jiubredeemer.magic.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "spell_bundled", schema = "magic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpellBundled {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "spell_bundle_id", nullable = false)
    private UUID spellBundleId;

    @Type(JsonbMapType.class)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, String> name;

    @Type(JsonbMapType.class)
    @Column(name = "alias_name", columnDefinition = "jsonb")
    private Map<String, String> aliasName;

    @Column(nullable = false)
    private String level;

    @Column(name = "\"class\"")
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

    @Column(name = "eng_description", columnDefinition = "text")
    private String engDescription;

    @Column(name = "ttg_slug")
    private String ttgSlug;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "creator_id")
    private UUID creatorId;

    @Column(name = "img_url")
    private String imgUrl;
}
