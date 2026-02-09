package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellBookDto;
import com.jiubredeemer.magic.dto.spellbook.SpellBookItemDto;
import com.jiubredeemer.magic.service.SpellBookService;
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

@WebMvcTest(SpellBookController.class)
class SpellBookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SpellBookService spellBookService;

    private static SpellBookDto spellBookDto(UUID id) {
        SpellBookDto dto = new SpellBookDto();
        dto.setId(id);
        dto.setCharacterId(UUID.randomUUID());
        dto.setRoomId(UUID.randomUUID());
        dto.setManaMax(100L);
        dto.setManaCurrent(100L);
        return dto;
    }

    @Test
    void create_returnsCreatedSpellBook() throws Exception {
        SpellBookDto request = spellBookDto(null);
        SpellBookDto response = spellBookDto(UUID.randomUUID());
        when(spellBookService.create(any(SpellBookDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/spell-books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId().toString()));

        verify(spellBookService).create(any(SpellBookDto.class));
    }

    @Test
    void getById_returnsSpellBook() throws Exception {
        UUID id = UUID.randomUUID();
        SpellBookDto dto = spellBookDto(id);
        when(spellBookService.getById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/spell-books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(spellBookService).getById(id);
    }

    @Test
    void list_returnsSpellBookList() throws Exception {
        SpellBookDto dto = spellBookDto(UUID.randomUUID());
        when(spellBookService.list()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/spell-books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(spellBookService).list();
    }

    @Test
    void update_returnsUpdatedSpellBook() throws Exception {
        UUID id = UUID.randomUUID();
        SpellBookDto request = spellBookDto(id);
        when(spellBookService.update(eq(id), any(SpellBookDto.class))).thenReturn(request);

        mockMvc.perform(put("/api/spell-books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(spellBookService).update(eq(id), any(SpellBookDto.class));
    }

    @Test
    void delete_returns204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/spell-books/{id}", id))
                .andExpect(status().isNoContent());

        verify(spellBookService).delete(id);
    }

    @Test
    void getByRoomAndCharacter_returnsSpellBook() throws Exception {
        UUID roomId = UUID.randomUUID();
        UUID characterId = UUID.randomUUID();
        SpellBookDto dto = spellBookDto(UUID.randomUUID());
        when(spellBookService.findSpellBookByRoomIdAndCharacterId(roomId, characterId)).thenReturn(dto);

        mockMvc.perform(get("/api/spell-books/by-room-character")
                        .param("roomId", roomId.toString())
                        .param("characterId", characterId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(dto.getId().toString()));

        verify(spellBookService).findSpellBookByRoomIdAndCharacterId(roomId, characterId);
    }

    @Test
    void addSpell_returnsSpellBook() throws Exception {
        UUID spellBookId = UUID.randomUUID();
        UUID spellId = UUID.randomUUID();
        SpellBookDto dto = spellBookDto(spellBookId);
        when(spellBookService.addSpellToBook(spellBookId, spellId)).thenReturn(dto);

        mockMvc.perform(post("/api/spell-books/{spellBookId}/spells/{spellId}", spellBookId, spellId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(spellBookId.toString()));

        verify(spellBookService).addSpellToBook(spellBookId, spellId);
    }

    @Test
    void removeSpell_returnsSpellBook() throws Exception {
        UUID spellBookId = UUID.randomUUID();
        UUID spellId = UUID.randomUUID();
        SpellBookDto dto = spellBookDto(spellBookId);
        when(spellBookService.removeSpellFromBook(spellBookId, spellId)).thenReturn(dto);

        mockMvc.perform(delete("/api/spell-books/{spellBookId}/spells/{spellId}", spellBookId, spellId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(spellBookId.toString()));

        verify(spellBookService).removeSpellFromBook(spellBookId, spellId);
    }

    @Test
    void setSpellInUse_returnsSpellBookItem() throws Exception {
        UUID spellBookId = UUID.randomUUID();
        UUID spellId = UUID.randomUUID();
        SpellBookItemDto itemDto = new SpellBookItemDto();
        itemDto.setId(UUID.randomUUID());
        itemDto.setInUse(true);
        when(spellBookService.setSpellInUse(spellBookId, spellId, true)).thenReturn(itemDto);

        mockMvc.perform(patch("/api/spell-books/{spellBookId}/spells/{spellId}/in-use", spellBookId, spellId)
                        .param("inUse", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inUse").value(true));

        verify(spellBookService).setSpellInUse(spellBookId, spellId, true);
    }
}
