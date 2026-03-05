package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.SpellBook;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpellBookRepository extends CrudRepository<SpellBook, UUID> {
    Optional<SpellBook> findByRoomIdAndCharacterId(UUID roomId, UUID characterId);

    List<SpellBook> findByRoomId(UUID roomId);
}
