package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.Spell;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpellRepository extends JpaRepository<Spell, java.util.UUID> {

    Optional<Spell> findByTtgSlug(String ttgSlug);
}
