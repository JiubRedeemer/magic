package com.jiubredeemer.magic.dto.spellbook;

import lombok.Data;

import java.util.UUID;

@Data
public class SpellBookItemDto {
    private UUID id;
    private UUID spellBookId;
    private UUID spellId;
    private Boolean inUse;
    private Boolean alwaysPrepared;
    private SpellDto spell;
}
