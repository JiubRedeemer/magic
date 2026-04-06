package com.jiubredeemer.magic.controller;

import com.jiubredeemer.magic.dto.spellbook.SpellImportResult;
import com.jiubredeemer.magic.service.TtgSpell24ImportService;
import com.jiubredeemer.magic.service.TtgSpellImportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpellImportController.class)
class SpellImportControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TtgSpellImportService importService;

    @MockitoBean
    TtgSpell24ImportService import24Service;

    @Test
    void importSpells_returnsImportResult() throws Exception {
        SpellImportResult result = new SpellImportResult(100, 80, 15, 5);
        when(importService.importSpells()).thenReturn(result);

        mockMvc.perform(post("/api/spells/import"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(100))
                .andExpect(jsonPath("$.imported").value(80))
                .andExpect(jsonPath("$.updated").value(15))
                .andExpect(jsonPath("$.failed").value(5));

        verify(importService).importSpells();
    }

    @Test
    void importSpells2024_returnsImportResult() throws Exception {
        SpellImportResult result = new SpellImportResult(50, 40, 8, 2);
        when(import24Service.importSpells()).thenReturn(result);

        mockMvc.perform(post("/api/spells/import-2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(50))
                .andExpect(jsonPath("$.imported").value(40))
                .andExpect(jsonPath("$.updated").value(8))
                .andExpect(jsonPath("$.failed").value(2));

        verify(import24Service).importSpells();
    }
}
