package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.client.TtgApiClient;
import com.jiubredeemer.magic.dto.ttg.TtgSpellDetail;
import com.jiubredeemer.magic.dto.ttg.TtgSpellListItem;
import com.jiubredeemer.magic.entity.Spell;
import com.jiubredeemer.magic.repository.SpellRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TtgSpellImportServiceTest {

    @Mock
    TtgApiClient ttgApiClient;

    @Mock
    SpellRepository spellRepository;

    @InjectMocks
    TtgSpellImportService ttgSpellImportService;

    private static TtgSpellListItem listItem(String slug) {
        return new TtgSpellListItem(
                Map.of("en", "Fireball"),
                3,
                "Evocation",
                null,
                null,
                false,
                "/spells/" + slug,
                null
        );
    }

    private static TtgSpellDetail detail(String slug) {
        return new TtgSpellDetail(
                Map.of("en", "Fireball"),
                3,
                "Evocation",
                null,
                null,
                false,
                false,
                "/spells/" + slug,
                null,
                "150 feet",
                "Instantaneous",
                "1 action",
                List.of(),
                "A bright streak flashes."
        );
    }

    @Test
    void importSpells_returnsResultWithCounts() {
        String slug = "fireball";
        TtgSpellListItem listItem = listItem(slug);
        TtgSpellDetail spellDetail = detail(slug);

        when(ttgApiClient.fetchSpellList(0, 1000)).thenReturn(List.of(listItem));
        when(spellRepository.findByTtgSlug(slug)).thenReturn(Optional.empty());
        when(ttgApiClient.fetchSpellDetail(slug)).thenReturn(spellDetail);
        when(spellRepository.save(any(Spell.class))).thenAnswer(inv -> {
            Spell s = inv.getArgument(0);
            if (s.getId() == null) {
                s.setId(UUID.randomUUID());
            }
            return s;
        });

        TtgSpellImportService.ImportResult result = ttgSpellImportService.importSpells();

        assertThat(result.total()).isEqualTo(1);
        assertThat(result.imported()).isEqualTo(1);
        assertThat(result.updated()).isEqualTo(0);
        assertThat(result.failed()).isEqualTo(0);

        ArgumentCaptor<Spell> spellCaptor = ArgumentCaptor.forClass(Spell.class);
        verify(spellRepository).save(spellCaptor.capture());
        assertThat(spellCaptor.getValue().getCreatedBy()).isEqualTo("TTG");

        verify(ttgApiClient).fetchSpellDetail(slug);
    }

    @Test
    void importSpells_skipsItemWithBlankSlug() {
        TtgSpellListItem invalidItem = new TtgSpellListItem(
                Map.of("en", "Unknown"),
                null,
                null,
                null,
                null,
                null,
                "/invalid",
                null
        );

        when(ttgApiClient.fetchSpellList(0, 1000)).thenReturn(List.of(invalidItem));

        TtgSpellImportService.ImportResult result = ttgSpellImportService.importSpells();

        assertThat(result.total()).isEqualTo(1);
        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.updated()).isEqualTo(0);
        assertThat(result.failed()).isEqualTo(1);

        verify(ttgApiClient, never()).fetchSpellDetail(any());
        verify(spellRepository, never()).save(any());
    }

    @Test
    void importSpells_updatesExistingSpell() {
        String slug = "fireball";
        TtgSpellListItem listItem = listItem(slug);
        TtgSpellDetail spellDetail = detail(slug);
        Spell existingSpell = new Spell();
        existingSpell.setId(UUID.randomUUID());
        existingSpell.setTtgSlug(slug);
        existingSpell.setName(Map.of("en", "Old Name"));

        when(ttgApiClient.fetchSpellList(0, 1000)).thenReturn(List.of(listItem));
        when(spellRepository.findByTtgSlug(slug)).thenReturn(Optional.of(existingSpell));
        when(ttgApiClient.fetchSpellDetail(slug)).thenReturn(spellDetail);
        when(spellRepository.save(any(Spell.class))).thenAnswer(inv -> inv.getArgument(0));

        TtgSpellImportService.ImportResult result = ttgSpellImportService.importSpells();

        assertThat(result.total()).isEqualTo(1);
        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.updated()).isEqualTo(1);
        assertThat(result.failed()).isEqualTo(0);

        ArgumentCaptor<Spell> spellCaptor = ArgumentCaptor.forClass(Spell.class);
        verify(spellRepository).save(spellCaptor.capture());
        Spell saved = spellCaptor.getValue();
        assertThat(saved.getId()).isEqualTo(existingSpell.getId());
        assertThat(saved.getName()).containsEntry("en", "Fireball");
    }

    @Test
    void importSpells_countsFailedWhenDetailFails() {
        String slug = "fireball";
        TtgSpellListItem listItem = listItem(slug);

        when(ttgApiClient.fetchSpellList(0, 1000)).thenReturn(List.of(listItem));
        when(spellRepository.findByTtgSlug(slug)).thenReturn(Optional.empty());
        when(ttgApiClient.fetchSpellDetail(slug)).thenThrow(new RuntimeException("API error"));

        TtgSpellImportService.ImportResult result = ttgSpellImportService.importSpells();

        assertThat(result.total()).isEqualTo(1);
        assertThat(result.imported()).isEqualTo(0);
        assertThat(result.updated()).isEqualTo(0);
        assertThat(result.failed()).isEqualTo(1);

        verify(spellRepository, never()).save(any());
    }
}
