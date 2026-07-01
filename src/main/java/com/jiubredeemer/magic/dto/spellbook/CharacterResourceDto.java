package com.jiubredeemer.magic.dto.spellbook;

import com.jiubredeemer.magic.entity.ChargesRefillEnum;
import lombok.Data;

import java.util.UUID;

@Data
public class CharacterResourceDto {
    private UUID id;
    private UUID spellBookId;
    private String name;
    private String icon;
    private Long maxCount;
    private Long currentCount;
    private ChargesRefillEnum refillRestType;
}
