package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.entity.Spell;
import com.jiubredeemer.magic.entity.SpellAi;
import com.jiubredeemer.magic.entity.SpellUser;
import com.jiubredeemer.magic.repository.Spell24AiRepository;
import com.jiubredeemer.magic.repository.Spell24Repository;
import com.jiubredeemer.magic.repository.SpellAiRepository;
import com.jiubredeemer.magic.repository.SpellRepository;
import com.jiubredeemer.magic.repository.SpellUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpellStorageServiceTest {

    @Mock
    SpellRepository spellRepository;
    @Mock
    SpellAiRepository spellAiRepository;
    @Mock
    SpellUserRepository spellUserRepository;
    @Mock
    Spell24Repository spell24Repository;
    @Mock
    Spell24AiRepository spell24AiRepository;

    SpellStorageService withAi;

    @BeforeEach
    void setUp() {
        withAi = new SpellStorageService(
                spellRepository,
                spellAiRepository,
                spellUserRepository,
                spell24Repository,
                spell24AiRepository,
                true
        );
    }

    @Test
    void save_userSpell_withUseAiTable_writesOnlySpellsUser() {
        Spell input = new Spell();
        input.setName(Map.of("en", "Custom"));
        input.setLevel("1");
        input.setSchool("EV");
        input.setCustomization(false);
        input.setTtgSlug(null);

        UUID id = UUID.randomUUID();
        when(spellUserRepository.save(any(SpellUser.class))).thenAnswer(inv -> {
            SpellUser u = inv.getArgument(0);
            u.setId(id);
            return u;
        });

        Spell saved = withAi.save(input);

        assertThat(saved.getName()).containsEntry("en", "Custom");
        verify(spellUserRepository).save(any(SpellUser.class));
        verify(spellRepository, never()).save(any());
        verify(spellAiRepository, never()).save(any());
    }

    @Test
    void save_ttgSpell_withUseAiTable_writesSpellAndSpellAi() {
        Spell input = new Spell();
        input.setName(Map.of("en", "Fireball"));
        input.setLevel("3");
        input.setSchool("EV");
        input.setCustomization(false);
        input.setTtgSlug("fireball");

        UUID id = UUID.randomUUID();
        when(spellRepository.save(any(Spell.class))).thenAnswer(inv -> {
            Spell s = inv.getArgument(0);
            s.setId(id);
            return s;
        });
        when(spellAiRepository.save(any(SpellAi.class))).thenAnswer(inv -> inv.getArgument(0));

        withAi.save(input);

        verify(spellRepository).save(any(Spell.class));
        verify(spellAiRepository).save(any(SpellAi.class));
        verify(spellUserRepository, never()).save(any());
    }

    @Test
    void findById_resolvesSpellUserAfterAiBranch() {
        UUID id = UUID.randomUUID();
        when(spellAiRepository.findById(id)).thenReturn(Optional.empty());
        SpellUser user = new SpellUser();
        user.setId(id);
        user.setName(Map.of("en", "Mine"));
        user.setLevel("0");
        user.setSchool("AB");
        user.setCustomization(false);
        when(spellUserRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<Spell> found = withAi.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).containsEntry("en", "Mine");
    }

    @Test
    void findById_prefersSpellAiWhenPresent() {
        UUID id = UUID.randomUUID();
        SpellAi ai = new SpellAi();
        ai.setId(id);
        ai.setName(Map.of("en", "AI"));
        ai.setLevel("3");
        ai.setSchool("EV");
        ai.setCustomization(false);
        when(spellAiRepository.findById(id)).thenReturn(Optional.of(ai));

        Optional<Spell> found = withAi.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).containsEntry("en", "AI");
        verify(spellUserRepository, never()).findById(any());
    }

    @Test
    void findById_fallsBackToSpellWhenAiRowMissing() {
        UUID id = UUID.randomUUID();
        when(spellAiRepository.findById(id)).thenReturn(Optional.empty());
        Spell base = new Spell();
        base.setId(id);
        base.setName(Map.of("en", "SRD"));
        base.setLevel("3");
        base.setSchool("EV");
        base.setCustomization(false);
        when(spellRepository.findById(id)).thenReturn(Optional.of(base));

        Optional<Spell> found = withAi.findById(id);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).containsEntry("en", "SRD");
    }

    @Test
    void deleteById_withUseAi_removesSpellAiAndPairedSpell() {
        UUID id = UUID.randomUUID();
        when(spellUserRepository.existsById(id)).thenReturn(false);
        when(spellAiRepository.existsById(id)).thenReturn(true);
        when(spellRepository.existsById(id)).thenReturn(true);

        withAi.deleteById(id);

        verify(spellAiRepository).deleteById(id);
        verify(spellRepository).deleteById(id);
    }
}
