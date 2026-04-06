package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.service.SpellImageGenerationService;
import com.jiubredeemer.magic.service.SpellService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpellController.class)
class SpellControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SpellService spellService;

    @MockitoBean
    SpellImageGenerationService spellImageGenerationService;

    private static SpellDto spellDto(UUID id) {
        SpellDto dto = new SpellDto();
        dto.setId(id);
        dto.setName(Map.of("en", "Fireball"));
        dto.setLevel("3");
        dto.setSpellClass("Wizard");
        dto.setSchool("Evocation");
        return dto;
    }

    @Test
    void create_returnsCreatedSpell() throws Exception {
        SpellDto request = spellDto(null);
        SpellDto response = spellDto(UUID.randomUUID());
        when(spellService.create(any(SpellDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/spells")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name.en").value("Fireball"));

        verify(spellService).create(any(SpellDto.class));
    }

    @Test
    void getById_returnsSpell() throws Exception {
        UUID id = UUID.randomUUID();
        SpellDto dto = spellDto(id);
        when(spellService.getById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/spells/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name.en").value("Fireball"));

        verify(spellService).getById(id);
    }

    @Test
    void list_returnsSpellList() throws Exception {
        SpellDto dto = spellDto(UUID.randomUUID());
        when(spellService.list()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/spells"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name.en").value("Fireball"));

        verify(spellService).list();
    }

    @Test
    void list_withSpellClassParam_callsListByClass() throws Exception {
        SpellDto dto = spellDto(UUID.randomUUID());
        dto.setSpellClass("WIZARD");
        when(spellService.listByClass("WIZARD")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/spells").param("spellClass", "WIZARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].spellClass").value("WIZARD"));

        verify(spellService).listByClass("WIZARD");
    }

    @Test
    void list2024_returnsSpellList() throws Exception {
        SpellDto dto = spellDto(UUID.randomUUID());
        when(spellService.list2024()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/spells/dnd2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name.en").value("Fireball"));

        verify(spellService).list2024();
    }

    @Test
    void list2024_withSpellClassParam_callsListByClass() throws Exception {
        SpellDto dto = spellDto(UUID.randomUUID());
        dto.setSpellClass("WIZARD");
        when(spellService.list2024ByClass("WIZARD")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/spells/dnd2024").param("spellClass", "WIZARD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].spellClass").value("WIZARD"));

        verify(spellService).list2024ByClass("WIZARD");
    }

    @Test
    void update_returnsUpdatedSpell() throws Exception {
        UUID id = UUID.randomUUID();
        SpellDto request = spellDto(id);
        when(spellService.update(eq(id), any(SpellDto.class))).thenReturn(request);

        mockMvc.perform(put("/api/spells/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(spellService).update(eq(id), any(SpellDto.class));
    }

    @Test
    void delete_returns204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/spells/{id}", id))
                .andExpect(status().isNoContent());

        verify(spellService).delete(id);
    }
}
