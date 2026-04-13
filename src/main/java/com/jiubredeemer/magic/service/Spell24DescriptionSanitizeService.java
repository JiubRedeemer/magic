package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellDescriptionSanitizeResult;
import com.jiubredeemer.magic.entity.Spell24;
import com.jiubredeemer.magic.entity.Spell24Ai;
import com.jiubredeemer.magic.repository.Spell24AiRepository;
import com.jiubredeemer.magic.repository.Spell24Repository;
import com.jiubredeemer.magic.service.ttg.TtgMarkdownSanitizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class Spell24DescriptionSanitizeService {

    private static final int BATCH_SIZE = 100;

    private final Spell24Repository spell24Repository;
    private final Spell24AiRepository spell24AiRepository;
    private final TransactionTemplate transactionTemplate;

    public Spell24DescriptionSanitizeService(
            Spell24Repository spell24Repository,
            Spell24AiRepository spell24AiRepository,
            PlatformTransactionManager transactionManager) {
        this.spell24Repository = spell24Repository;
        this.spell24AiRepository = spell24AiRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Sanitizes {@code description} on {@code spell_24}, and {@code description} / {@code eng_description} on
     * {@code spell_24_ai}. Processes in paged batches with a transaction per batch to limit lock duration and memory.
     */
    public SpellDescriptionSanitizeResult sanitizeAll() {
        AtomicInteger total24 = new AtomicInteger();
        AtomicInteger changed24 = new AtomicInteger();

        Pageable pageable = PageRequest.of(0, BATCH_SIZE, Sort.by("id"));
        while (true) {
            Page<Spell24> page = spell24Repository.findAll(pageable);
            List<Spell24> batch = page.getContent();
            if (batch.isEmpty()) {
                break;
            }
            transactionTemplate.executeWithoutResult(status -> {
                for (Spell24 row : batch) {
                    total24.incrementAndGet();
                    String cleaned = TtgMarkdownSanitizer.sanitize(row.getDescription());
                    if (!Objects.equals(row.getDescription(), cleaned)) {
                        row.setDescription(cleaned);
                        spell24Repository.save(row);
                        changed24.incrementAndGet();
                    }
                }
            });
            if (!page.hasNext()) {
                break;
            }
            pageable = page.nextPageable();
        }

        AtomicInteger totalAi = new AtomicInteger();
        AtomicInteger changedAi = new AtomicInteger();

        pageable = PageRequest.of(0, BATCH_SIZE, Sort.by("id"));
        while (true) {
            Page<Spell24Ai> page = spell24AiRepository.findAll(pageable);
            List<Spell24Ai> batch = page.getContent();
            if (batch.isEmpty()) {
                break;
            }
            transactionTemplate.executeWithoutResult(status -> {
                for (Spell24Ai row : batch) {
                    totalAi.incrementAndGet();
                    boolean rowChanged = false;
                    String d = TtgMarkdownSanitizer.sanitize(row.getDescription());
                    if (!Objects.equals(row.getDescription(), d)) {
                        row.setDescription(d);
                        rowChanged = true;
                    }
                    String e = TtgMarkdownSanitizer.sanitize(row.getEngDescription());
                    if (!Objects.equals(row.getEngDescription(), e)) {
                        row.setEngDescription(e);
                        rowChanged = true;
                    }
                    if (rowChanged) {
                        spell24AiRepository.save(row);
                        changedAi.incrementAndGet();
                    }
                }
            });
            if (!page.hasNext()) {
                break;
            }
            pageable = page.nextPageable();
        }

        return new SpellDescriptionSanitizeResult(
                total24.get(), changed24.get(), totalAi.get(), changedAi.get());
    }
}
