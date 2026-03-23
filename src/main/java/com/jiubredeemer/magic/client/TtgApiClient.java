package com.jiubredeemer.magic.client;

import com.jiubredeemer.magic.dto.ttg.TtgSpellDetail;
import com.jiubredeemer.magic.dto.ttg.TtgSpellListItem;
import com.jiubredeemer.magic.dto.ttg.TtgSpellListRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Supplier;
import java.util.List;

@Component
public class TtgApiClient {

    private static final String BASE_URL = "https://5e14.ttg.club/api/v1";
    private static final Logger log = LoggerFactory.getLogger(TtgApiClient.class);

    private static final ParameterizedTypeReference<List<TtgSpellListItem>> SPELL_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    private static final int MAX_429_RETRIES = 15;
    private static final long INITIAL_429_DELAY_MS = 2000L;
    private static final long MAX_429_DELAY_MS = 180_000L; // 3 minutes

    private final RestClient restClient;

    public TtgApiClient(@Value("${ttg.api.base-url:" + BASE_URL + "}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Origin", "https://5e14.ttg.club")
                .defaultHeader("Referer", "https://5e14.ttg.club/spells")
                .build();
    }

    public List<TtgSpellListItem> fetchSpellList(int page, int size) {
        TtgSpellListRequest request = TtgSpellListRequest.allSpells(page, size);
        return executeWith429Retry(
                "ttg.fetchSpellList(page=%d,size=%d)".formatted(page, size),
                () -> restClient.post()
                        .uri("/spells")
                        .body(request)
                        .retrieve()
                        .body(SPELL_LIST_TYPE)
        );
    }

    public TtgSpellDetail fetchSpellDetail(String slug) {
        return executeWith429Retry(
                "ttg.fetchSpellDetail(slug=%s)".formatted(slug),
                () -> restClient.post()
                        .uri("/spells/{slug}", slug)
                        .retrieve()
                        .body(TtgSpellDetail.class)
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
        // Should be unreachable because the loop either returns or throws.
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
        // Retry-After can be either seconds (integer) or an HTTP date.
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
