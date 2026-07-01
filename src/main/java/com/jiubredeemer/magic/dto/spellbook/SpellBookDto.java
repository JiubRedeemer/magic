package com.jiubredeemer.magic.dto.spellbook;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class SpellBookDto {
    private UUID id;
    private UUID characterId;
    private UUID roomId;
    private Long manaMax;
    private Long manaCurrent;
    private LocalDateTime deletedAt;
    private List<SpellBookItemDto> spells;
    private Map<Long, SpellCellDto> spellCells;
    private List<CharacterResourceDto> customResources;
}
