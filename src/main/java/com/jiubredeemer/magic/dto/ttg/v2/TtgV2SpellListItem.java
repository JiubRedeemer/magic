package com.jiubredeemer.magic.dto.ttg.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jiubredeemer.magic.dto.ttg.TtgComponents;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TtgV2SpellListItem(
        String url,
        TtgV2LocalizedName name,
        Integer level,
        String school,
        Boolean concentration,
        Boolean ritual,
        TtgComponents components
) {
    /** Slug for DB / detail URL (e.g. {@code acid-splash-phb}). */
    public String slug() {
        return url;
    }
}
