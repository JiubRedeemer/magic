package com.jiubredeemer.magic.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "spell", schema = "magic")
public class Spell {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @Type(JsonbMapType.class)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, String> name;

    @Column(nullable = false)
    private String level;

    @Column(name = "\"class\"", nullable = false)
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

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "ttg_slug", unique = true)
    private String ttgSlug;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public Spell() {
    }

    public java.util.UUID getId() {
        return id;
    }

    public void setId(java.util.UUID id) {
        this.id = id;
    }

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSpellClass() {
        return spellClass;
    }

    public void setSpellClass(String spellClass) {
        this.spellClass = spellClass;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Boolean getRitual() {
        return ritual;
    }

    public void setRitual(Boolean ritual) {
        this.ritual = ritual;
    }

    public Boolean getCustomization() {
        return customization;
    }

    public void setCustomization(Boolean customization) {
        this.customization = customization;
    }

    public String getDamageType() {
        return damageType;
    }

    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }

    public String getHealType() {
        return healType;
    }

    public void setHealType(String healType) {
        this.healType = healType;
    }

    public String getSavingThrow() {
        return savingThrow;
    }

    public void setSavingThrow(String savingThrow) {
        this.savingThrow = savingThrow;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getComponents() {
        return components;
    }

    public void setComponents(String components) {
        this.components = components;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTtgSlug() {
        return ttgSlug;
    }

    public void setTtgSlug(String ttgSlug) {
        this.ttgSlug = ttgSlug;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
