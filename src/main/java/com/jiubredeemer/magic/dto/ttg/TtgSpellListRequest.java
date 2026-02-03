package com.jiubredeemer.magic.dto.ttg;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TtgSpellListRequest(
        int page,
        int size,
        TtgSearch search,
        List<TtgOrder> order
) {

    public static TtgSpellListRequest allSpells(int page, int size) {
        return new TtgSpellListRequest(
                page,
                size,
                new TtgSearch("", false),
                List.of(new TtgOrder("level", "asc"), new TtgOrder("name", "asc"))
        );
    }
}
