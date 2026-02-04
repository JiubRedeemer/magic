package com.jiubredeemer.magic.mapper;

import com.jiubredeemer.magic.dto.spellbook.SpellBookItemDto;
import com.jiubredeemer.magic.entity.SpellBookItem;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class SpellBookItemDtoMapper {
    public SpellBookItemDto toDto(SpellBookItem entity) {
        final SpellBookItemDto model = new SpellBookItemDto();
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    public List<SpellBookItemDto> toDto(List<SpellBookItem> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public List<SpellBookItemDto> toDto(Iterable<SpellBookItem> entities) {
        return StreamSupport.stream(entities.spliterator(), false).map(this::toDto).toList();
    }

    public SpellBookItem toEntity(SpellBookItemDto model) {
        final SpellBookItem entity = new SpellBookItem();
        BeanUtils.copyProperties(model, entity);
        return entity;
    }

    public SpellBookItem updateEntity(SpellBookItemDto model, SpellBookItem entity) {
        BeanUtils.copyProperties(model, entity, "id");
        return entity;
    }

    public List<SpellBookItem> toEntity(List<SpellBookItemDto> models) {
        return models.stream().map(this::toEntity).toList();
    }
}
