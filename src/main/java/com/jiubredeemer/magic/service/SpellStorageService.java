package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.entity.Spell;
import com.jiubredeemer.magic.entity.Spell24;
import com.jiubredeemer.magic.entity.Spell24Ai;
import com.jiubredeemer.magic.entity.SpellAi;
import com.jiubredeemer.magic.entity.SpellBundled;
import com.jiubredeemer.magic.entity.SpellUser;
import com.jiubredeemer.magic.repository.Spell24AiRepository;
import com.jiubredeemer.magic.repository.Spell24Repository;
import com.jiubredeemer.magic.repository.SpellAiRepository;
import com.jiubredeemer.magic.repository.SpellBundledRepository;
import com.jiubredeemer.magic.repository.SpellRepository;
import com.jiubredeemer.magic.repository.SpellUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SpellStorageService {

    private final SpellRepository spellRepository;
    private final SpellAiRepository spellAiRepository;
    private final SpellUserRepository spellUserRepository;
    private final Spell24Repository spell24Repository;
    private final Spell24AiRepository spell24AiRepository;
    private final SpellBundledRepository spellBundledRepository;
    private final boolean useAiTable;

    public SpellStorageService(SpellRepository spellRepository,
                               SpellAiRepository spellAiRepository,
                               SpellUserRepository spellUserRepository,
                               Spell24Repository spell24Repository,
                               Spell24AiRepository spell24AiRepository,
                               SpellBundledRepository spellBundledRepository,
                               @Value("${magic.spells.use-ai-table:false}") boolean useAiTable) {
        this.spellRepository = spellRepository;
        this.spellAiRepository = spellAiRepository;
        this.spellUserRepository = spellUserRepository;
        this.spell24Repository = spell24Repository;
        this.spell24AiRepository = spell24AiRepository;
        this.spellBundledRepository = spellBundledRepository;
        this.useAiTable = useAiTable;
    }

    public Spell save(Spell spell) {
        if (isUserSpell(spell)) {
            SpellUser su = toSpellUser(spell);
            return toSpell(spellUserRepository.save(su));
        }
        if (!useAiTable) {
            return spellRepository.save(spell);
        }
        Spell spellBase = spellRepository.save(spell);
        SpellAi aiToSave = toSpellAi(spell);
        aiToSave.setId(spellBase.getId());
        if (aiToSave.getAliasName() == null) {
            aiToSave.setAliasName(aiToSave.getName());
        }
        SpellAi saved = spellAiRepository.save(aiToSave);
        return toSpell(saved);
    }

    /**
     * Resolution order: 5e AI (if enabled) → 5e base → user spells → 2024 AI (if enabled) → 2024 base.
     * Fallback to base tables when the AI row is missing keeps TTG pairs ({@code spell}/{@code spell_ai})
     * consistent after partial data or legacy deletes.
     */
    public Optional<Spell> findById(UUID id) {
        if (useAiTable) {
            Optional<SpellAi> ai = spellAiRepository.findById(id);
            if (ai.isPresent()) {
                return Optional.of(toSpell(ai.get()));
            }
        }
        Optional<Spell> base5e = spellRepository.findById(id);
        if (base5e.isPresent()) {
            return base5e;
        }
        Optional<SpellUser> user = spellUserRepository.findById(id);
        if (user.isPresent()) {
            return Optional.of(toSpell(user.get()));
        }
        if (useAiTable) {
            Optional<Spell24Ai> ai24 = spell24AiRepository.findById(id);
            if (ai24.isPresent()) {
                return Optional.of(toSpell(ai24.get()));
            }
        }
        Optional<Spell24> base24 = spell24Repository.findById(id);
        if (base24.isPresent()) {
            return Optional.of(toSpell(base24.get()));
        }
        // Заклинания, добавленные в книгу из бандла, живут в spell_bundled.
        Optional<SpellBundled> bundled = spellBundledRepository.findById(id);
        return bundled.map(this::toSpell);
    }

    public List<Spell> findAll() {
        List<Spell> result = new ArrayList<>();
        if (useAiTable) {
            result.addAll(spellAiRepository.findAll().stream().map(this::toSpell).toList());
        } else {
            result.addAll(spellRepository.findAll());
        }
        result.addAll(spellUserRepository.findAll().stream().map(this::toSpell).toList());
        if (useAiTable) {
            result.addAll(spell24AiRepository.findAll().stream().map(this::toSpell).toList());
        } else {
            result.addAll(spell24Repository.findAll().stream().map(this::toSpell).toList());
        }
        return result;
    }

    public void deleteById(UUID id) {
        if (spellUserRepository.existsById(id)) {
            spellUserRepository.deleteById(id);
            return;
        }
        if (useAiTable && spellAiRepository.existsById(id)) {
            spellAiRepository.deleteById(id);
            if (spellRepository.existsById(id)) {
                spellRepository.deleteById(id);
            }
            return;
        }
        if (spellRepository.existsById(id)) {
            spellRepository.deleteById(id);
            return;
        }
        if (useAiTable && spell24AiRepository.existsById(id)) {
            spell24AiRepository.deleteById(id);
            if (spell24Repository.existsById(id)) {
                spell24Repository.deleteById(id);
            }
            return;
        }
        if (spell24Repository.existsById(id)) {
            spell24Repository.deleteById(id);
            return;
        }
        if (spellBundledRepository.existsById(id)) {
            spellBundledRepository.deleteById(id);
            return;
        }
        spellRepository.deleteById(id);
    }

    public Optional<Spell> findByTtgSlug(String ttgSlug) {
        if (!useAiTable) {
            return spellRepository.findByTtgSlug(ttgSlug);
        }
        return spellAiRepository.findByTtgSlug(ttgSlug).map(this::toSpell);
    }

    public List<Spell> findBySpellClass(String code, String codePrefix, String codeSuffix, String codeMiddle) {
        List<Spell> result = new ArrayList<>();
        if (useAiTable) {
            result.addAll(spellAiRepository.findBySpellClass(code, codePrefix, codeSuffix, codeMiddle).stream()
                    .map(this::toSpell)
                    .toList());
        } else {
            result.addAll(spellRepository.findBySpellClass(code, codePrefix, codeSuffix, codeMiddle));
        }
        result.addAll(spellUserRepository.findBySpellClass(code, codePrefix, codeSuffix, codeMiddle).stream()
                .map(this::toSpell)
                .toList());
        if (useAiTable) {
            result.addAll(spell24AiRepository.findBySpellClass(code, codePrefix, codeSuffix, codeMiddle).stream()
                    .map(this::toSpell)
                    .toList());
        } else {
            result.addAll(spell24Repository.findBySpellClass(code, codePrefix, codeSuffix, codeMiddle).stream()
                    .map(this::toSpell)
                    .toList());
        }
        return result;
    }

    /** Lists only DnD 2024 catalog rows (AI table is preferred when enabled). */
    public List<Spell> findAll24() {
        if (useAiTable) {
            return spell24AiRepository.findAll().stream().map(this::toSpell).toList();
        }
        return spell24Repository.findAll().stream().map(this::toSpell).toList();
    }

    /** Filters only DnD 2024 catalog rows by class (AI table is preferred when enabled). */
    public List<Spell> findBySpellClass24(String code, String codePrefix, String codeSuffix, String codeMiddle) {
        if (useAiTable) {
            return spell24AiRepository.findBySpellClass(code, codePrefix, codeSuffix, codeMiddle).stream()
                    .map(this::toSpell)
                    .toList();
        }
        return spell24Repository.findBySpellClass(code, codePrefix, codeSuffix, codeMiddle).stream()
                .map(this::toSpell)
                .toList();
    }

    /** Пользовательские заклинания (spells_user) — включаются в комнатный каталог как и раньше. */
    public List<Spell> findAllUserSpells() {
        return spellUserRepository.findAll().stream().map(this::toSpell).toList();
    }

    public List<Spell> findUserSpellsByClass(String code, String codePrefix, String codeSuffix, String codeMiddle) {
        return spellUserRepository.findBySpellClass(code, codePrefix, codeSuffix, codeMiddle).stream()
                .map(this::toSpell)
                .toList();
    }

    private static boolean isUserSpell(Spell spell) {
        return spell.getTtgSlug() == null || spell.getTtgSlug().isBlank();
    }

    private Spell toSpell(SpellAi source) {
        Spell target = new Spell();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    private Spell toSpell(SpellUser source) {
        Spell target = new Spell();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    private Spell toSpell(Spell24 source) {
        Spell target = new Spell();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    private Spell toSpell(Spell24Ai source) {
        Spell target = new Spell();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    private Spell toSpell(SpellBundled source) {
        Spell target = new Spell();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    private SpellUser toSpellUser(Spell source) {
        SpellUser target = new SpellUser();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    private SpellAi toSpellAi(Spell source) {
        SpellAi target = new SpellAi();
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * Loads an existing DnD2024 spell for TTG import updates (prefers {@code spell_24}, then {@code spell_24_ai}).
     */
    public Optional<Spell24> findSpell24ForImportBySlug(String slug) {
        Optional<Spell24> base = spell24Repository.findByTtgSlug(slug);
        if (base.isPresent()) {
            return base;
        }
        if (useAiTable) {
            return spell24AiRepository.findByTtgSlug(slug).map(this::spell24FromAi);
        }
        return Optional.empty();
    }

    /**
     * Persists TTG DnD2024 catalog rows to {@code spell_24} and optionally {@code spell_24_ai}.
     */
    public Spell saveTtgSpell24(Spell24 spell) {
        if (!useAiTable) {
            return toSpell(spell24Repository.save(spell));
        }
        Spell24 base = spell24Repository.save(spell);
        Spell24Ai ai = toSpell24Ai(spell);
        ai.setId(base.getId());
        if (ai.getAliasName() == null) {
            ai.setAliasName(ai.getName());
        }
        Spell24Ai savedAi = spell24AiRepository.save(ai);
        return toSpell(savedAi);
    }

    private Spell24 spell24FromAi(Spell24Ai ai) {
        Spell24 s = new Spell24();
        BeanUtils.copyProperties(ai, s);
        return s;
    }

    private Spell24Ai toSpell24Ai(Spell24 source) {
        Spell24Ai target = new Spell24Ai();
        BeanUtils.copyProperties(source, target);
        return target;
    }
}
