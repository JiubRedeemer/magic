package com.jiubredeemer.magic.dto.spellbook;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class SpellBookDto {
    private UUID id;
    private UUID characterId;
    private UUID roomId;
    private Long manaMax;
    private Long manaCurrent;
    private Map<Long, SpellCellDto> spellCells;
}
