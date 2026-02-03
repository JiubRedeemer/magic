package com.jiubredeemer.magic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "spell_cell", schema = "magic")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class SpellCell {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID spellBookId;
    private Long level;
    private Long maxCount;
    private Long currentCount;
    @Enumerated(value = EnumType.STRING)
    private ChargesRefillEnum refillRestType;
}
