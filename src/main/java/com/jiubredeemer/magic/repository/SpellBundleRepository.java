package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.SpellBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpellBundleRepository extends JpaRepository<SpellBundle, UUID> {

    /** Бандлы, видимые пользователю: системные (owner_user_id IS NULL), публичные и его собственные. */
    @Query("""
            SELECT b FROM SpellBundle b
            WHERE b.ownerUserId IS NULL
               OR b.isPublic = true
               OR b.ownerUserId = :userId
            ORDER BY b.name
            """)
    List<SpellBundle> findVisibleForUser(@Param("userId") UUID userId);

    List<SpellBundle> findByOwnerUserIdOrderByName(UUID ownerUserId);
}
