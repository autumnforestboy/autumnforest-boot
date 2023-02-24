package io.github.autumnforest.boot.commons.mybatis;

import io.github.autumnforest.boot.commons.DisplayedEnum;
import lombok.SneakyThrows;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * mybatis专用，枚举字段使用
 */
@MappedJdbcTypes(value = JdbcType.TINYINT, includeNullJdbcType = true)
public class DefaultEnumTypeHandler extends BaseTypeHandler<DisplayedEnum> {
    private Class<DisplayedEnum> type;

    public DefaultEnumTypeHandler() {
    }


    public DefaultEnumTypeHandler(Class<DisplayedEnum> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @SneakyThrows
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DisplayedEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public DisplayedEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convert(rs.getInt(columnName));
    }

    @Override
    public DisplayedEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convert(rs.getInt(columnIndex));
    }

    @Override
    public DisplayedEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convert(cs.getInt(columnIndex));
    }

    @SneakyThrows
    private DisplayedEnum convert(int status) {
        DisplayedEnum[] objs = type.getEnumConstants();
        for (DisplayedEnum em : objs) {
            if (em.getValue() == status) {
                return em;
            }
        }
        return null;
    }
}
