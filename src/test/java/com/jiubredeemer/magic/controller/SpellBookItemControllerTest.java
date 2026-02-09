package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellBookItemDto;
import com.jiubredeemer.magic.service.SpellBookItemService;
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

@WebMvcTest(SpellBookItemController.class)
class SpellBookItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SpellBookItemService spellBookItemService;

    private static SpellBookItemDto spellBookItemDto(UUID id) {
        SpellBookItemDto dto = new SpellBookItemDto();
        dto.setId(id);
        dto.setSpellBookId(UUID.randomUUID());
        dto.setSpellId(UUID.randomUUID());
        dto.setInUse(false);
        return dto;
    }

    @Test
    void create_returnsCreatedSpellBookItem() throws Exception {
        SpellBookItemDto request = spellBookItemDto(null);
        SpellBookItemDto response = spellBookItemDto(UUID.randomUUID());
        when(spellBookItemService.create(any(SpellBookItemDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/spell-book-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()));

        verify(spellBookItemService).create(any(SpellBookItemDto.class));
    }

    @Test
    void getById_returnsSpellBookItem() throws Exception {
        UUID id = UUID.randomUUID();
        SpellBookItemDto dto = spellBookItemDto(id);
        when(spellBookItemService.getById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/spell-book-items/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(spellBookItemService).getById(id);
    }

    @Test
    void list_returnsSpellBookItemList() throws Exception {
        SpellBookItemDto dto = spellBookItemDto(UUID.randomUUID());
        when(spellBookItemService.list()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/spell-book-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(spellBookItemService).list();
    }

    @Test
    void update_returnsUpdatedSpellBookItem() throws Exception {
        UUID id = UUID.randomUUID();
        SpellBookItemDto request = spellBookItemDto(id);
        when(spellBookItemService.update(eq(id), any(SpellBookItemDto.class))).thenReturn(request);

        mockMvc.perform(put("/api/spell-book-items/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(spellBookItemService).update(eq(id), any(SpellBookItemDto.class));
    }

    @Test
    void setInUse_returnsUpdatedSpellBookItem() throws Exception {
        UUID id = UUID.randomUUID();
        SpellBookItemDto dto = spellBookItemDto(id);
        dto.setInUse(true);
        when(spellBookItemService.setInUse(id, true)).thenReturn(dto);

        mockMvc.perform(patch("/api/spell-book-items/{id}/in-use", id)
                        .param("inUse", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inUse").value(true));

        verify(spellBookItemService).setInUse(id, true);
    }

    @Test
    void delete_returns204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/spell-book-items/{id}", id))
                .andExpect(status().isNoContent());

        verify(spellBookItemService).delete(id);
    }
}
