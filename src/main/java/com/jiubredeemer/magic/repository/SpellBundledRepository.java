package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.SpellBundled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpellBundledRepository extends JpaRepository<SpellBundled, UUID> {

    List<SpellBundled> findBySpellBundleId(UUID spellBundleId);

    void deleteBySpellBundleId(UUID spellBundleId);

    List<SpellBundled> findBySpellBundleIdInOrderByCreatedAt(List<UUID> spellBundleIds);

    @Query(value = """
            SELECT * FROM magic.spell_bundled s
            WHERE s.spell_bundle_id IN (:bundleIds)
              AND (s.class = :code
                   OR s.class LIKE :codePrefix
                   OR s.class LIKE :codeSuffix
                   OR s.class LIKE :codeMiddle)
            """, nativeQuery = true)
    List<SpellBundled> findBySpellClassInBundles(@Param("bundleIds") List<UUID> bundleIds,
                                                 @Param("code") String code,
                                                 @Param("codePrefix") String codePrefix,
                                                 @Param("codeSuffix") String codeSuffix,
                                                 @Param("codeMiddle") String codeMiddle);
}
