package com.jiubredeemer.magic.dto.ttg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TtgSpellDetail(
        Map<String, String> name,
        Integer level,
        String school,
        String additionalType,
        TtgComponents components,
        Boolean concentration,
        Boolean ritual,
        String url,
        TtgSource source,
        String range,
        String duration,
        String time,
        List<TtgSpellClass> classes,
        String description
) {
}
