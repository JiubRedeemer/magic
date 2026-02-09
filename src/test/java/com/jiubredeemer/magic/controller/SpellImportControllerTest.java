package com.jiubredeemer.magic.controller;

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

    @Test
    void importSpells_returnsImportResult() throws Exception {
        TtgSpellImportService.ImportResult result =
                new TtgSpellImportService.ImportResult(100, 80, 15, 5);
        when(importService.importSpells()).thenReturn(result);

        mockMvc.perform(post("/api/spells/import"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(100))
                .andExpect(jsonPath("$.imported").value(80))
                .andExpect(jsonPath("$.updated").value(15))
                .andExpect(jsonPath("$.failed").value(5));

        verify(importService).importSpells();
    }
}
