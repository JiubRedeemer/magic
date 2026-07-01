package com.jiubredeemer.magic.mapper;

import com.jiubredeemer.magic.dto.spellbook.CharacterResourceDto;
import com.jiubredeemer.magic.entity.CharacterResource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CharacterResourceDtoMapper {

    public CharacterResourceDto toDto(CharacterResource entity) {
        CharacterResourceDto dto = new CharacterResourceDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public List<CharacterResourceDto> toDto(List<CharacterResource> entities) {
        return entities.stream().map(this::toDto).toList();
    }

    public CharacterResource toEntity(CharacterResourceDto dto) {
        CharacterResource entity = new CharacterResource();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    public CharacterResource updateEntity(CharacterResourceDto dto, CharacterResource entity) {
        BeanUtils.copyProperties(dto, entity, "id");
        return entity;
    }
}
