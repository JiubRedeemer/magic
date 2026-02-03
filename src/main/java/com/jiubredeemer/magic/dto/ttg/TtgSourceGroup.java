package com.jiubredeemer.magic.dto.ttg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TtgSourceGroup(
        String name,
        String shortName
) {
}
