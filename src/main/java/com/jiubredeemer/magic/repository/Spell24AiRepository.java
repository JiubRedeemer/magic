package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.Spell24;
import com.jiubredeemer.magic.entity.Spell24Ai;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Spell24AiRepository extends JpaRepository<Spell24Ai, UUID> {

    Optional<Spell24Ai> findByTtgSlug(String ttgSlug);

    @Query(value = """
            SELECT * FROM magic.spell_24_ai s
            WHERE s.class = :code
               OR s.class LIKE :codePrefix
               OR s.class LIKE :codeSuffix
               OR s.class LIKE :codeMiddle
            """, nativeQuery = true)
    List<Spell24Ai> findBySpellClass(@Param("code") String code,
                                     @Param("codePrefix") String codePrefix,
                                     @Param("codeSuffix") String codeSuffix,
                                     @Param("codeMiddle") String codeMiddle);

    @Query(value = """
            SELECT s.*
            FROM magic.spell_24 s
            WHERE NOT EXISTS (
                SELECT 1
                FROM magic.spell_24_ai ai
                WHERE ai.id = s.id
            )
            LIMIT 1
            """, nativeQuery = true)
    Optional<Spell24> findOneMissingInAi();


    @Query(value = """
            SELECT s.*
            FROM magic.spell_24_ai s WHERE s.img_url is null
            LIMIT 1
            """, nativeQuery = true)
    Optional<Spell24> findOneFromSpellMissingImageInAi();
}
