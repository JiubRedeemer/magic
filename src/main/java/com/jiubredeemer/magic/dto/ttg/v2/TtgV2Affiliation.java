package com.jiubredeemer.magic.dto.ttg.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TtgV2Affiliation(
        List<TtgV2ClassRef> classes,
        List<TtgV2ClassRef> subclasses,
        List<Object> species,
        List<Object> lineages
) {
}
