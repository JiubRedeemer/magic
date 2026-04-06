package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.SpellUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpellUserRepository extends JpaRepository<SpellUser, UUID> {

    Optional<SpellUser> findByTtgSlug(String ttgSlug);

    @Query(value = """
            SELECT * FROM magic.spells_user s
            WHERE s.class = :code
               OR s.class LIKE :codePrefix
               OR s.class LIKE :codeSuffix
               OR s.class LIKE :codeMiddle
            """, nativeQuery = true)
    List<SpellUser> findBySpellClass(@Param("code") String code,
                                 @Param("codePrefix") String codePrefix,
                                 @Param("codeSuffix") String codeSuffix,
                                 @Param("codeMiddle") String codeMiddle);
}
