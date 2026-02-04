package com.jiubredeemer.magic.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "spell_book_item", schema = "magic")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SpellBookItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID spellBookId;
    private UUID spellId;
    private Boolean inUse;

}
