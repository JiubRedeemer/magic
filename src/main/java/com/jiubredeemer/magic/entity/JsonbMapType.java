package com.jiubredeemer.magic.entity;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Hibernate UserType for mapping Map&lt;String, String&gt; to PostgreSQL jsonb.
 * Uses PGobject to properly pass jsonb values to PostgreSQL.
 */
public class JsonbMapType implements UserType<Map<String, String>> {

    private static final String JSONB_TYPE = "jsonb";

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<Map<String, String>> returnedClass() {
        return (Class) Map.class;
    }

    @Override
    public boolean equals(Map<String, String> x, Map<String, String> y) {
        return (x == null && y == null) || (x != null && x.equals(y));
    }

    @Override
    public int hashCode(Map<String, String> x) {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public Map<String, String> nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session,
                                           Object owner) throws SQLException {
        String json = rs.getString(position);
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        return SpellNameConverter.fromJson(json);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Map<String, String> value, int index,
                            SharedSessionContractImplementor session) throws SQLException {
        if (value == null || value.isEmpty()) {
            st.setObject(index, createPgObject("{}"));
        } else {
            st.setObject(index, createPgObject(SpellNameConverter.toJson(value)));
        }
    }

    @Override
    public Map<String, String> deepCopy(Map<String, String> value) {
        return value == null ? null : new HashMap<>(value);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Map<String, String> value) {
        return (Serializable) deepCopy(value);
    }

    @Override
    public Map<String, String> assemble(Serializable cached, Object owner) {
        return deepCopy((Map<String, String>) cached);
    }

    private Object createPgObject(String json) throws SQLException {
        org.postgresql.util.PGobject pgObject = new org.postgresql.util.PGobject();
        pgObject.setType(JSONB_TYPE);
        pgObject.setValue(json);
        return pgObject;
    }
}
