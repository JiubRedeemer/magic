CREATE TABLE magic.spell_book (
                                  id uuid NOT NULL,
                                  character_id uuid NOT NULL,
                                  room_id uuid NOT NULL,
                                  mana_max int8 NULL,
                                  mana_current int8 NULL,
                                  CONSTRAINT spell_book_pk PRIMARY KEY (id)
);

CREATE TABLE magic.spell_cell (
                                  id uuid NOT NULL,
                                  spell_book_id uuid NOT NULL,
                                  "level" int8 NOT NULL,
                                  max_count int8 NOT NULL,
                                  current_count int8 NOT NULL,
                                  refill_rest_type varchar DEFAULT 'LONG_REST' NOT NULL,
                                  CONSTRAINT spell_cell_pk PRIMARY KEY (id),
                                  CONSTRAINT spell_cell_spell_book_fk FOREIGN KEY (spell_book_id) REFERENCES magic.spell_book(id)
);

CREATE TABLE magic.spell_book_item (
                                       id uuid NOT NULL,
                                       spell_book_id uuid NOT NULL,
                                       spell_id uuid NOT NULL,
                                       in_use boolean DEFAULT false NOT NULL,
                                       CONSTRAINT spell_book_item_pk PRIMARY KEY (id),
                                       CONSTRAINT spell_book_item_spell_book_fk FOREIGN KEY (spell_book_id) REFERENCES magic.spell_book(id),
                                       CONSTRAINT spell_book_item_spell_fk FOREIGN KEY (spell_id) REFERENCES magic.spell(id)
);

ALTER TABLE magic.spell ADD img_url varchar NULL;
