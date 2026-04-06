package com.jiubredeemer.magic.client;

import com.jiubredeemer.magic.dto.ttg.v2.TtgV2SpellDetail;
import com.jiubredeemer.magic.dto.ttg.v2.TtgV2SpellListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Supplier;

/**
 * TTG Club HTTP API v2 (DnD 2024, new.ttg.club): GET list and GET detail.
 */
public class TtgClubV2ApiClient {

    private static final Logger log = LoggerFactory.getLogger(TtgClubV2ApiClient.class);

    private static final ParameterizedTypeReference<List<TtgV2SpellListItem>> SPELL_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    private static final int MAX_429_RETRIES = 15;
    private static final long INITIAL_429_DELAY_MS = 2000L;
    private static final long MAX_429_DELAY_MS = 180_000L;

    private final RestClient restClient;
    private final String searchSources;

    public TtgClubV2ApiClient(String baseUrl, String origin, String referer, String searchSources) {
        this.searchSources = searchSources;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Origin", origin)
                .defaultHeader("Referer", referer)
                .build();
    }

    public List<TtgV2SpellListItem> fetchSpellList(int page, int size) {
        return executeWith429Retry(
                "ttg.v2.fetchSpellList(page=%d,size=%d)".formatted(page, size),
                () -> restClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/spells/search")
                                .queryParam("source", searchSources)
                                .queryParam("page", page)
                                .queryParam("size", size)
                                .build())
                        .retrieve()
                        .body(SPELL_LIST_TYPE)
        );
    }

    public TtgV2SpellDetail fetchSpellDetail(String slug) {
        return executeWith429Retry(
                "ttg.v2.fetchSpellDetail(slug=%s)".formatted(slug),
                () -> restClient.get()
                        .uri("/spells/{slug}", slug)
                        .retrieve()
                        .body(TtgV2SpellDetail.class)
        );
    }

    private <T> T executeWith429Retry(String operation, Supplier<T> supplier) {
        long delayMs = INITIAL_429_DELAY_MS;
        for (int attempt = 1; attempt <= MAX_429_RETRIES; attempt++) {
            try {
                return supplier.get();
            } catch (RestClientResponseException e) {
                if (e.getStatusCode().value() == 429 && attempt < MAX_429_RETRIES) {
                    long retryAfterMs = parseRetryAfterMs(e);
                    long sleepMs = Math.max(delayMs, retryAfterMs);
                    sleepMs = Math.min(sleepMs, MAX_429_DELAY_MS);
                    log.warn("{} hit 429 (attempt {}/{}). Sleeping {} ms before retry.",
                            operation, attempt, MAX_429_RETRIES, sleepMs);
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted while waiting for TTG rate limit", ie);
                    }
                    delayMs = Math.min(delayMs * 2, MAX_429_DELAY_MS);
                    continue;
                }
                throw e;
            }
        }
        throw new IllegalStateException("Retry loop exhausted: " + operation);
    }

    private long parseRetryAfterMs(RestClientResponseException e) {
        if (e.getResponseHeaders() == null) {
            return 0L;
        }
        String retryAfter = e.getResponseHeaders().getFirst("Retry-After");
        if (retryAfter == null) {
            return 0L;
        }
        retryAfter = retryAfter.trim();
        if (retryAfter.isEmpty()) {
            return 0L;
        }
        try {
            long seconds = Long.parseLong(retryAfter);
            return Math.max(0L, seconds * 1000L);
        } catch (NumberFormatException ignored) {
            // fallthrough
        }
        try {
            ZonedDateTime dateTime = ZonedDateTime.parse(retryAfter, DateTimeFormatter.RFC_1123_DATE_TIME);
            Instant until = dateTime.toInstant();
            long millis = Duration.between(Instant.now(), until).toMillis();
            return Math.max(0L, millis);
        } catch (DateTimeParseException ignored) {
            return 0L;
        }
    }
}
