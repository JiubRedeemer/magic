package com.jiubredeemer.magic.entity;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

public final class SpellNameConverter {

    private static final ObjectMapper MAPPER = JsonMapper.builder().build();

    private SpellNameConverter() {
    }

    public static String toJson(Map<String, String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}";
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to serialize spell name to JSON", e);
        }
    }

    public static Map<String, String> fromJson(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Map.of();
        }
        try {
            return MAPPER.readValue(dbData,
                    MAPPER.getTypeFactory().constructMapType(Map.class, String.class, String.class));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to deserialize spell name from JSON", e);
        }
    }
}
