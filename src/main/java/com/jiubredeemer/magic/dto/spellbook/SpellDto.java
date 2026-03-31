package com.jiubredeemer.magic.dto.spellbook;

import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
public class SpellDto {
    private java.util.UUID id;
    private Map<String, String> name;
    private String level;
    private String spellClass;
    private String school;
    private Boolean ritual;
    private Boolean customization;
    private String damageType;
    private String healType;
    private String savingThrow;
    private String useTime;
    private String distance;
    private String duration;
    private String components;
    private String materialComponents;
    private String description;
    /** English description; persisted only on {@code spell_ai}. */
    private String engDescription;
    private Map<String, String> aliasName;
    private Instant createdAt;
    /** Set when creating a spell; stored as createdBy. */
    private java.util.UUID characterId;
    private String createdBy;
    private String imgUrl;
}
