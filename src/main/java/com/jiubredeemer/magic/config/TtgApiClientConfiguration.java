package com.jiubredeemer.magic.config;

import com.jiubredeemer.magic.client.TtgApiClient;
import com.jiubredeemer.magic.client.TtgClubV2ApiClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TtgApiClientConfiguration {

    @Bean
    @Primary
    public TtgApiClient ttgApiClient5e(
            @Value("${ttg.api.base-url:https://5e14.ttg.club/api/v1}") String baseUrl,
            @Value("${ttg.api.origin:https://5e14.ttg.club}") String origin,
            @Value("${ttg.api.referer:https://5e14.ttg.club/spells}") String referer) {
        return new TtgApiClient(baseUrl, origin, referer);
    }

    /** DnD 2024: GET {@code /spells/search} and GET {@code /spells/{slug}} on API v2. */
    @Bean
    @Qualifier("ttg2024")
    public TtgClubV2ApiClient ttgClubV2ApiClient(
            @Value("${ttg2024.api.base-url:https://new.ttg.club/api/v2}") String baseUrl,
            @Value("${ttg2024.api.origin:https://new.ttg.club}") String origin,
            @Value("${ttg2024.api.referer:https://new.ttg.club/spells}") String referer,
            @Value("${ttg2024.api.search-sources:PHB,EFA}") String searchSources) {
        return new TtgClubV2ApiClient(baseUrl, origin, referer, searchSources);
    }
}
