package com.jiubredeemer.magic.dto.ttg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TtgSpellClass(
        String name,
        String url,
        String icon
) {
}
