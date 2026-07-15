package com.jiubredeemer.magic.repository;

import com.jiubredeemer.magic.entity.RoomSpellBundle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomSpellBundleRepository extends JpaRepository<RoomSpellBundle, UUID> {

    List<RoomSpellBundle> findByRoomId(UUID roomId);

    Optional<RoomSpellBundle> findByRoomIdAndSpellBundleId(UUID roomId, UUID spellBundleId);

    void deleteByRoomIdAndSpellBundleId(UUID roomId, UUID spellBundleId);

    void deleteBySpellBundleId(UUID spellBundleId);
}
