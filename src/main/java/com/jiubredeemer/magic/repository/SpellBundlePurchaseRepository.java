package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.SpellBundlePurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpellBundlePurchaseRepository
        extends JpaRepository<SpellBundlePurchase, SpellBundlePurchase.Key> {

    List<SpellBundlePurchase> findByUserId(UUID userId);

    boolean existsByUserIdAndSpellBundleId(UUID userId, UUID spellBundleId);

    void deleteBySpellBundleId(UUID spellBundleId);
}
