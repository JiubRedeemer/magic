package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.SpellBookItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpellBookItemRepository extends CrudRepository<SpellBookItem, UUID> {
}
