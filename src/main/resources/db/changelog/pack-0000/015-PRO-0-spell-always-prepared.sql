ALTER TABLE magic.spell_book_item
    ADD COLUMN IF NOT EXISTS always_prepared boolean DEFAULT false NOT NULL;
