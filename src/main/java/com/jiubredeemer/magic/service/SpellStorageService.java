package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.entity.Spell;
import com.jiubredeemer.magic.entity.SpellAi;
import com.jiubredeemer.magic.repository.SpellAiRepository;
import com.jiubredeemer.magic.repository.SpellRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SpellStorageService {

    private final SpellRepository spellRepository;
    private final SpellAiRepository spellAiRepository;
    private final boolean useAiTable;

    public SpellStorageService(SpellRepository spellRepository,
                               SpellAiRepository spellAiRepository,
                               @Value("${magic.spells.use-ai-table:false}") boolean useAiTable) {
        this.spellRepository = spellRepository;
        this.spellAiRepository = spellAiRepository;
        this.useAiTable = useAiTable;
    }

    public Spell save(Spell spell) {
        if (!useAiTable) {
            return spellRepository.save(spell);
        }
        SpellAi aiToSave = toSpellAi(spell);
        SpellAi saved = spellAiRepository.save(aiToSave);
        return toSpell(saved);
    }

    public Optional<Spell> findById(UUID id) {
        if (!useAiTable) {
            return spellRepository.findById(id);
        }
        return spellAiRepository.findById(id).map(this::toSpell);
    }

    public List<Spell> findAll() {
        if (!useAiTable) {
            return spellRepository.findAll();
        }
        return spellAiRepository.findAll().stream().map(this::toSpell).toList();
    }

    public void deleteById(UUID id) {
        if (!useAiTable) {
            spellRepository.deleteById(id);
            return;
        }
        spellAiRepository.deleteById(id);
    }

    public Optional<Spell> findByTtgSlug(String ttgSlug) {
        if (!useAiTable) {
            return spellRepository.findByTtgSlug(ttgSlug);
        }
        return spellAiRepository.findByTtgSlug(ttgSlug).map(this::toSpell);
    }

    public List<Spell> findBySpellClass(String code, String codePrefix, String codeSuffix, String codeMiddle) {
        if (!useAiTable) {
            return spellRepository.findBySpellClass(code, codePrefix, codeSuffix, codeMiddle);
        }
        return spellAiRepository.findBySpellClass(code, codePrefix, codeSuffix, codeMiddle)
                .stream()
                .map(this::toSpell)
                .toList();
    }

    private Spell toSpell(SpellAi source) {
        Spell target = new Spell();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    private SpellAi toSpellAi(Spell source) {
        SpellAi target = new SpellAi();
        BeanUtils.copyProperties(source, target);
        return target;
    }
}
