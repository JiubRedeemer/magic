package com.jiubredeemer.magic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "room_spell_bundle", schema = "magic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomSpellBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "room_id", nullable = false)
    private UUID roomId;

    @Column(name = "spell_bundle_id", nullable = false)
    private UUID spellBundleId;
}
