package com.jiubredeemer.magic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "spell_book", schema = "magic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpellBook {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID characterId;
    private UUID roomId;
    private Long manaMax;
    private Long manaCurrent;
    private LocalDateTime deletedAt;

}
