package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.SpellAi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpellAiRepository extends JpaRepository<SpellAi, java.util.UUID> {

    Optional<SpellAi> findByTtgSlug(String ttgSlug);

    @Query(value = """
            SELECT * FROM magic.spell_ai s
            WHERE s.class = :code
               OR s.class LIKE :codePrefix
               OR s.class LIKE :codeSuffix
               OR s.class LIKE :codeMiddle
            """, nativeQuery = true)
    List<SpellAi> findBySpellClass(@Param("code") String code,
                                   @Param("codePrefix") String codePrefix,
                                   @Param("codeSuffix") String codeSuffix,
                                   @Param("codeMiddle") String codeMiddle);
}
