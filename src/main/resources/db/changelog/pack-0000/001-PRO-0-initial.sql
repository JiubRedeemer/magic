CREATE TABLE magic.spell
(
    id            uuid      NOT NULL,
    name          jsonb     NOT NULL,
    level         text      NOT NULL,
    class         text      NOT NULL,
    school        text      NOT NULL,
    ritual        boolean,
    customization boolean   not null,
    damage_type   text,
    heal_type     text,
    saving_throw  text,
    use_time      text,
    distance      text,
    duration      text,
    components    text,
    created_at    timestamp NOT NULL,
    PRIMARY KEY (id)
);