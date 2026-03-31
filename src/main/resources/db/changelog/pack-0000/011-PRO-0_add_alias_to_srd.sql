ALTER TABLE magic.spell_ai ADD COLUMN alias_name jsonb NULL;

UPDATE magic.spell_ai SET alias_name = (select "name" from magic.spell where spell.id = spell_ai.id) WHERE 1=1