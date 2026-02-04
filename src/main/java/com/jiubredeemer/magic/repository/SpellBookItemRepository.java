package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.SpellBookItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpellBookItemRepository extends CrudRepository<SpellBookItem, UUID> {
    void deleteBySpellBookIdAndSpellId(UUID spellBookId, UUID spellId);

    Optional<SpellBookItem> findBySpellBookIdAndSpellId(UUID spellBookId, UUID spellId);

    List<SpellBookItem> findAllBySpellBookId(UUID spellBookId);
}
