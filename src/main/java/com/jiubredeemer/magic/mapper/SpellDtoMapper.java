package com.jiubredeemer.magic.mapper;

import com.jiubredeemer.magic.dto.spellbook.SpellDto;
import com.jiubredeemer.magic.entity.Spell;
import com.jiubredeemer.magic.entity.Spell24;
import com.jiubredeemer.magic.entity.SpellBundled;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class SpellDtoMapper {

    public SpellDto toDto(Spell entity) {
        final SpellDto model = new SpellDto();
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    public SpellDto toDto(Spell24 entity) {
        final SpellDto model = new SpellDto();
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    public List<SpellDto> toDto(List<Spell> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public List<SpellDto> toDto(Iterable<Spell> entities) {
        return StreamSupport.stream(entities.spliterator(), false).map(this::toDto).toList();
    }

    public SpellDto toDto(SpellBundled entity) {
        final SpellDto model = new SpellDto();
        BeanUtils.copyProperties(entity, model);
        return model;
    }

    public List<SpellDto> toDtoFromBundled(List<SpellBundled> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public SpellBundled toBundled(SpellDto model) {
        final SpellBundled entity = new SpellBundled();
        BeanUtils.copyProperties(model, entity);
        return entity;
    }

    public Spell toEntity(SpellDto model) {
        final Spell entity = new Spell();
        BeanUtils.copyProperties(model, entity);
        return entity;
    }

    public Spell updateEntity(SpellDto model, Spell entity) {
        BeanUtils.copyProperties(model, entity, "id", "createdBy");
        return entity;
    }

    public List<Spell> toEntity(List<SpellDto> models) {
        return models.stream().map(this::toEntity).toList();
    }
}
