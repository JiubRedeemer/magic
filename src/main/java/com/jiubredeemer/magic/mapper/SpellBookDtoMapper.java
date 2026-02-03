package com.jiubredeemer.magic.mapper;

import com.jiubredeemer.magic.dto.spellbook.SpellBookDto;
import com.jiubredeemer.magic.entity.SpellBook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class SpellBookDtoMapper {

    public SpellBookDto toDto(SpellBook entity) {
        final SpellBookDto model = new SpellBookDto();
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    public List<SpellBookDto> toDto(List<SpellBook> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public List<SpellBookDto> toDto(Iterable<SpellBook> entities) {
        return StreamSupport.stream(entities.spliterator(), false).map(this::toDto).toList();
    }

    public SpellBook toEntity(SpellBookDto model) {
        final SpellBook entity = new SpellBook();
        BeanUtils.copyProperties(model, entity);
        return entity;
    }

    public List<SpellBook> toEntity(List<SpellBookDto> models) {
        return models.stream().map(this::toEntity).toList();
    }
}
