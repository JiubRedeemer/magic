package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.CharacterResource;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CharacterResourceRepository extends CrudRepository<CharacterResource, UUID> {
    List<CharacterResource> findAllBySpellBookId(UUID spellBookId);
}
