package com.jiubredeemer.magic.dto.ttg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TtgSpellListItem(
        Map<String, String> name,
        Integer level,
        String school,
        String additionalType,
        TtgComponents components,
        Boolean concentration,
        String url,
        TtgSource source
) {

    public String getSlug() {
        if (url == null || !url.startsWith("/spells/")) {
            return null;
        }
        return url.substring("/spells/".length());
    }
}
