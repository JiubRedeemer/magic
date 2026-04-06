package com.jiubredeemer.magic.dto.ttg.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TtgV2ClassRef(String url, String name) {
}
