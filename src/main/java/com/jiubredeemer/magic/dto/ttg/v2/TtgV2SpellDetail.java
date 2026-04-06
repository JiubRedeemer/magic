package com.jiubredeemer.magic.dto.ttg.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jiubredeemer.magic.dto.ttg.TtgComponents;

/**
 * Spell card from API v2. {@code description} / {@code upper} deserialize as generic JSON ({@link Object}):
 * arrays of strings and/or nested maps (e.g. quote blocks). Avoids a compile dependency on {@code JsonNode}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TtgV2SpellDetail(
        String url,
        TtgV2LocalizedName name,
        Integer level,
        String school,
        String castingTime,
        String range,
        String duration,
        TtgComponents components,
        Object description,
        Object upper,
        Boolean ritual,
        Boolean concentration,
        TtgV2Affiliation affiliation
) {
}
