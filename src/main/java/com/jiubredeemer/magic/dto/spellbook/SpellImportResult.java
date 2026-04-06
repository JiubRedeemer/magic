package com.jiubredeemer.magic.dto.spellbook;

public record SpellImportResult(int total, int imported, int updated, int failed) {

    @Override
    public String toString() {
        return "total=%d, imported=%d, updated=%d, failed=%d".formatted(total, imported, updated, failed);
    }
}
