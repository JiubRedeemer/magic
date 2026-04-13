package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellDescriptionSanitizeJobResponse;
import com.jiubredeemer.magic.dto.spellbook.SpellDescriptionSanitizeResult;
import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.service.Spell24DescriptionSanitizeService;
import com.jiubredeemer.magic.service.SpellDescriptionSanitizeJobService;
import com.jiubredeemer.magic.service.SpellImageGenerationService;
import com.jiubredeemer.magic.service.SpellService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @MockitoBean
    Spell24DescriptionSanitizeService spell24DescriptionSanitizeService;

    @MockitoBean
    SpellDescriptionSanitizeJobService spellDescriptionSanitizeJobService;

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
    void sanitizeDnd2024Descriptions_returnsResult() throws Exception {
        var result = new SpellDescriptionSanitizeResult(10, 2, 10, 1);
        when(spell24DescriptionSanitizeService.sanitizeAll()).thenReturn(result);

        mockMvc.perform(post("/api/spells/dnd2024/sanitize-descriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSpell24").value(10))
                .andExpect(jsonPath("$.changedSpell24").value(2))
                .andExpect(jsonPath("$.totalSpell24Ai").value(10))
                .andExpect(jsonPath("$.changedSpell24Ai").value(1));

        verify(spell24DescriptionSanitizeService).sanitizeAll();
    }

    @Test
    void sanitizeDnd2024DescriptionsAsync_returns202WithJobId() throws Exception {
        UUID jobId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(spellDescriptionSanitizeJobService.submit()).thenReturn(jobId);

        mockMvc.perform(post("/api/spells/dnd2024/sanitize-descriptions/async"))
                .andExpect(status().isAccepted())
                .andExpect(header().string(
                        HttpHeaders.LOCATION,
                        "/api/spells/dnd2024/sanitize-descriptions/jobs/00000000-0000-0000-0000-000000000001"))
                .andExpect(jsonPath("$.jobId").value("00000000-0000-0000-0000-000000000001"));

        verify(spellDescriptionSanitizeJobService).submit();
    }

    @Test
    void getSanitizeJob_returnsStatus() throws Exception {
        UUID jobId = UUID.randomUUID();
        when(spellDescriptionSanitizeJobService.getJob(jobId))
                .thenReturn(Optional.of(new SpellDescriptionSanitizeJobResponse("RUNNING", null, null)));

        mockMvc.perform(get("/api/spells/dnd2024/sanitize-descriptions/jobs/{jobId}", jobId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RUNNING"));

        verify(spellDescriptionSanitizeJobService).getJob(jobId);
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
