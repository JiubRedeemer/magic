package com.jiubredeemer.magic.dto.spellbook;

import com.jiubredeemer.magic.entity.ChargesRefillEnum;
import lombok.Data;

import java.util.UUID;

@Data
public class SpellCellDto {
    private UUID id;
    private UUID spellBookId;
    private Long level;
    private Long maxCount;
    private Long currentCount;
    private ChargesRefillEnum refillRestType;
}
