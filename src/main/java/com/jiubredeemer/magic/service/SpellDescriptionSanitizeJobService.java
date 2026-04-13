package com.jiubredeemer.magic.service;

import com.jiubredeemer.magic.dto.spellbook.SpellDescriptionSanitizeJobResponse;
import com.jiubredeemer.magic.dto.spellbook.SpellDescriptionSanitizeResult;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

@Service
public class SpellDescriptionSanitizeJobService {

    private final Spell24DescriptionSanitizeService sanitizeService;
    private final Executor spellSanitizeExecutor;

    public SpellDescriptionSanitizeJobService(
            Spell24DescriptionSanitizeService sanitizeService,
            @Qualifier("spellSanitizeExecutor") Executor spellSanitizeExecutor) {
        this.sanitizeService = sanitizeService;
        this.spellSanitizeExecutor = spellSanitizeExecutor;
    }

    private final ConcurrentHashMap<UUID, CompletableFuture<SpellDescriptionSanitizeResult>> jobs =
            new ConcurrentHashMap<>();

    public UUID submit() {
        UUID id = UUID.randomUUID();
        CompletableFuture<SpellDescriptionSanitizeResult> future =
                CompletableFuture.supplyAsync(sanitizeService::sanitizeAll, spellSanitizeExecutor);
        jobs.put(id, future);
        return id;
    }

    public Optional<SpellDescriptionSanitizeJobResponse> getJob(UUID jobId) {
        CompletableFuture<SpellDescriptionSanitizeResult> future = jobs.get(jobId);
        if (future == null) {
            return Optional.empty();
        }
        if (!future.isDone()) {
            return Optional.of(new SpellDescriptionSanitizeJobResponse("RUNNING", null, null));
        }
        try {
            SpellDescriptionSanitizeResult result = future.get();
            return Optional.of(new SpellDescriptionSanitizeJobResponse("DONE", result, null));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.of(new SpellDescriptionSanitizeJobResponse("FAILED", null, e.getMessage()));
        } catch (ExecutionException e) {
            Throwable c = e.getCause() != null ? e.getCause() : e;
            String msg = c.getMessage() != null ? c.getMessage() : c.getClass().getSimpleName();
            return Optional.of(new SpellDescriptionSanitizeJobResponse("FAILED", null, msg));
        }
    }
}
