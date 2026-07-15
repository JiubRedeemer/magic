CREATE TABLE magic.spell_bundle
(
    id             uuid      NOT NULL,
    name           text      NOT NULL,
    description    text      NOT NULL,
    created_at     timestamp NOT NULL,
    img_url        text,
    owner_user_id  uuid,
    is_public      boolean   NOT NULL DEFAULT false,
    price_crystals integer   NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE magic.room_spell_bundle
(
    id              uuid NOT NULL,
    room_id         uuid NOT NULL,
    spell_bundle_id uuid NOT NULL,
    PRIMARY KEY (id)
);
CREATE INDEX room_spell_bundle_room_id ON magic.room_spell_bundle (room_id);
ALTER TABLE magic.room_spell_bundle
    ADD CONSTRAINT fk_room_spell_bundle FOREIGN KEY (spell_bundle_id) REFERENCES magic.spell_bundle (id);

CREATE TABLE magic.spell_bundled
(
    id                  uuid                   NOT NULL,
    spell_bundle_id     uuid                   NOT NULL,
    name                jsonb                  NOT NULL,
    alias_name          jsonb,
    level               text                   NOT NULL,
    class               text,
    school              text                   NOT NULL,
    ritual              boolean,
    customization       boolean DEFAULT false  NOT NULL,
    damage_type         text,
    heal_type           text,
    saving_throw        text,
    use_time            text,
    distance            text,
    duration            text,
    components          text,
    material_components text,
    description         text,
    eng_description     text,
    ttg_slug            varchar(255),
    created_at          timestamp              NOT NULL,
    created_by          varchar(255),
    creator_id          uuid,
    img_url             varchar,
    PRIMARY KEY (id)
);
CREATE INDEX spell_bundled_bundle_id ON magic.spell_bundled (spell_bundle_id);
ALTER TABLE magic.spell_bundled
    ADD CONSTRAINT fk_spell_bundled_bundle FOREIGN KEY (spell_bundle_id) REFERENCES magic.spell_bundle (id);

CREATE TABLE magic.spell_bundle_purchase
(
    user_id         uuid      NOT NULL,
    spell_bundle_id uuid      NOT NULL,
    created_at      timestamp NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, spell_bundle_id)
);
CREATE INDEX spell_bundle_purchase_bundle_idx ON magic.spell_bundle_purchase (spell_bundle_id);
ALTER TABLE magic.spell_bundle_purchase
    ADD CONSTRAINT fk_spell_bundle_purchase FOREIGN KEY (spell_bundle_id) REFERENCES magic.spell_bundle (id);

-- Четыре системных бандла: издание × источник таблицы.
INSERT INTO magic.spell_bundle (id, name, description, created_at, is_public)
VALUES ('a0000001-0000-0000-0000-000000002014', '2014', 'Заклинания D&D 2014', now(), true),
       ('a0000001-0000-0000-0000-000000002024', '2024', 'Заклинания D&D 2024', now(), true),
       ('a0000001-0000-0000-0000-0000000a2014', '2014 AI', 'Заклинания D&D 2014 (AI)', now(), true),
       ('a0000001-0000-0000-0000-0000000a2024', '2024 AI', 'Заклинания D&D 2024 (AI)', now(), true);

-- Копии строк из существующих таблиц (новые id, чтобы spell/spell_ai не конфликтовали по PK).
INSERT INTO magic.spell_bundled (id, spell_bundle_id, name, level, class, school, ritual, customization,
                                 damage_type, heal_type, saving_throw, use_time, distance, duration, components,
                                 material_components, description, ttg_slug, created_at, created_by, img_url)
SELECT gen_random_uuid(), 'a0000001-0000-0000-0000-000000002014', name, level, class, school, ritual, customization,
       damage_type, heal_type, saving_throw, use_time, distance, duration, components,
       material_components, description, ttg_slug, created_at, created_by, img_url
FROM magic.spell;

INSERT INTO magic.spell_bundled (id, spell_bundle_id, name, level, class, school, ritual, customization,
                                 damage_type, heal_type, saving_throw, use_time, distance, duration, components,
                                 material_components, description, ttg_slug, created_at, created_by, img_url)
SELECT gen_random_uuid(), 'a0000001-0000-0000-0000-000000002024', name, level, class, school, ritual, customization,
       damage_type, heal_type, saving_throw, use_time, distance, duration, components,
       material_components, description, ttg_slug, created_at, created_by, img_url
FROM magic.spell_24;

INSERT INTO magic.spell_bundled (id, spell_bundle_id, name, alias_name, level, class, school, ritual, customization,
                                 damage_type, heal_type, saving_throw, use_time, distance, duration, components,
                                 material_components, description, eng_description, ttg_slug, created_at, created_by, img_url)
SELECT gen_random_uuid(), 'a0000001-0000-0000-0000-0000000a2014', name, alias_name, level, class, school, ritual, customization,
       damage_type, heal_type, saving_throw, use_time, distance, duration, components,
       material_components, description, eng_description, ttg_slug, created_at, created_by, img_url
FROM magic.spell_ai;

INSERT INTO magic.spell_bundled (id, spell_bundle_id, name, alias_name, level, class, school, ritual, customization,
                                 damage_type, heal_type, saving_throw, use_time, distance, duration, components,
                                 material_components, description, eng_description, ttg_slug, created_at, created_by, img_url)
SELECT gen_random_uuid(), 'a0000001-0000-0000-0000-0000000a2024', name, alias_name, level, class, school, ritual, customization,
       damage_type, heal_type, saving_throw, use_time, distance, duration, components,
       material_components, description, eng_description, ttg_slug, created_at, created_by, img_url
FROM magic.spell_24_ai;
