package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellCellDto;
import com.jiubredeemer.magic.entity.ChargesRefillEnum;
import com.jiubredeemer.magic.service.SpellCellService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpellCellController.class)
class SpellCellControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SpellCellService spellCellService;

    private static SpellCellDto spellCellDto(UUID id) {
        SpellCellDto dto = new SpellCellDto();
        dto.setId(id);
        dto.setSpellBookId(UUID.randomUUID());
        dto.setLevel(1L);
        dto.setMaxCount(2L);
        dto.setCurrentCount(2L);
        dto.setRefillRestType(ChargesRefillEnum.SHORT_REST);
        return dto;
    }

    @Test
    void create_returnsCreatedSpellCell() throws Exception {
        SpellCellDto request = spellCellDto(null);
        SpellCellDto response = spellCellDto(UUID.randomUUID());
        when(spellCellService.create(any(SpellCellDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/spell-cells")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.refillRestType").value("SHORT_REST"));

        verify(spellCellService).create(any(SpellCellDto.class));
    }

    @Test
    void getById_returnsSpellCell() throws Exception {
        UUID id = UUID.randomUUID();
        SpellCellDto dto = spellCellDto(id);
        when(spellCellService.getById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/spell-cells/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.level").value(1));

        verify(spellCellService).getById(id);
    }

    @Test
    void list_returnsSpellCellList() throws Exception {
        SpellCellDto dto = spellCellDto(UUID.randomUUID());
        when(spellCellService.list()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/spell-cells"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(spellCellService).list();
    }

    @Test
    void update_returnsUpdatedSpellCell() throws Exception {
        UUID id = UUID.randomUUID();
        SpellCellDto request = spellCellDto(id);
        when(spellCellService.update(eq(id), any(SpellCellDto.class))).thenReturn(request);

        mockMvc.perform(put("/api/spell-cells/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(spellCellService).update(eq(id), any(SpellCellDto.class));
    }

    @Test
    void use_returnsUpdatedSpellCell() throws Exception {
        UUID id = UUID.randomUUID();
        SpellCellDto response = spellCellDto(id);
        response.setCurrentCount(1L);
        when(spellCellService.use(id)).thenReturn(response);

        mockMvc.perform(post("/api/spell-cells/{id}/use", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentCount").value(1));

        verify(spellCellService).use(id);
    }

    @Test
    void delete_returns204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/spell-cells/{id}", id))
                .andExpect(status().isNoContent());

        verify(spellCellService).delete(id);
    }
}
