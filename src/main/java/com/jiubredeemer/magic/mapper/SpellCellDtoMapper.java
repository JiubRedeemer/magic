package com.jiubredeemer.magic.mapper;

import com.jiubredeemer.magic.dto.spellbook.SpellCellDto;
import com.jiubredeemer.magic.entity.SpellCell;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class SpellCellDtoMapper {

    public SpellCellDto toDto(SpellCell entity) {
        final SpellCellDto model = new SpellCellDto();
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    public List<SpellCellDto> toDto(List<SpellCell> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public List<SpellCellDto> toDto(Iterable<SpellCell> entities) {
        return StreamSupport.stream(entities.spliterator(), false).map(this::toDto).toList();
    }

    public SpellCell toEntity(SpellCellDto model) {
        final SpellCell entity = new SpellCell();
        BeanUtils.copyProperties(model, entity);
        return entity;
    }

    public SpellCell updateEntity(SpellCellDto model, SpellCell entity) {
        BeanUtils.copyProperties(model, entity, "id");
        return entity;
    }

    public List<SpellCell> toEntity(List<SpellCellDto> models) {
        return models.stream().map(this::toEntity).toList();
    }
}
