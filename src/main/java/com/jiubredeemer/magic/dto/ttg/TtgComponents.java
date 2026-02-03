package com.jiubredeemer.magic.dto.ttg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Spell components. Field 'm' can be Boolean (true = has material) or String (material description).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TtgComponents(
        Boolean v,
        Boolean s,
        Object m  // Boolean (true) or String (material component description)
) {
}
