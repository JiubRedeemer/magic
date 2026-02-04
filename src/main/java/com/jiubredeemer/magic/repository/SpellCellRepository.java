package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.SpellCell;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface SpellCellRepository extends CrudRepository<SpellCell, UUID> {
    List<SpellCell> findAllBySpellBookId(UUID id);
}
