package com.jiubredeemer.magic.client;

import com.jiubredeemer.magic.dto.ttg.TtgSpellDetail;
import com.jiubredeemer.magic.dto.ttg.TtgSpellListItem;
import com.jiubredeemer.magic.dto.ttg.TtgSpellListRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class TtgApiClient {

    private static final String BASE_URL = "https://5e14.ttg.club/api/v1";
    private static final ParameterizedTypeReference<List<TtgSpellListItem>> SPELL_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

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
        return restClient.post()
                .uri("/spells")
                .body(request)
                .retrieve()
                .body(SPELL_LIST_TYPE);
    }

    public TtgSpellDetail fetchSpellDetail(String slug) {
        return restClient.post()
                .uri("/spells/{slug}", slug)
                .retrieve()
                .body(TtgSpellDetail.class);
    }
}
