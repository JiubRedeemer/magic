package com.jiubredeemer.magic.dto.ttg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TtgSource(
        String shortName,
        String name,
        TtgSourceGroup group,
        Integer page
) {
}
