package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.Spell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpellRepository extends JpaRepository<Spell, java.util.UUID> {

    Optional<Spell> findByTtgSlug(String ttgSlug);

    /**
     * Find spells available to the given class. The spell class column stores comma-separated codes
     * (e.g. "BARD, WIZARD"), so we match the code as a whole segment.
     */
    @Query(value = """
            SELECT * FROM magic.spell s
            WHERE s.class = :code
               OR s.class LIKE :codePrefix
               OR s.class LIKE :codeSuffix
               OR s.class LIKE :codeMiddle
            """, nativeQuery = true)
    List<Spell> findBySpellClass(@Param("code") String code,
                                 @Param("codePrefix") String codePrefix,
                                 @Param("codeSuffix") String codeSuffix,
                                 @Param("codeMiddle") String codeMiddle);
}
