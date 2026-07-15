package com.jiubredeemer.magic.dto.spellbook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpellBundleDto {
    private UUID id;
    private String name;
    private String description;
    private Instant createdAt;
    private String imgUrl;
    /** null = системный (официальный) бандл. */
    private UUID ownerUserId;
    private Boolean isPublic;
    private Integer priceCrystals;
    /** Куплен ли бандл запрашивающим пользователем. */
    private Boolean purchased;
    /** Заполняется только при листинге в контексте комнаты. */
    private Boolean enabled;
}
