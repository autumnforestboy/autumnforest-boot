package io.github.autumnforest.boot.commons.mybatis;

import io.github.autumnforest.boot.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseJsonTypeHandler<T> extends BaseTypeHandler<T> {
    private final Class<T> type;

    public BaseJsonTypeHandler(Class<T> type) {
        if (type == null) throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    //


    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, T t, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, JsonUtil.obj2Str(t));
    }

    @Override
    public T getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return this.convert(resultSet.getString(columnName));
    }


    @Override
    public T getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return this.convert(resultSet.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return this.convert(callableStatement.getString(columnIndex));
    }


    private T convert(String string) {
        if (StringUtils.isEmpty(string)) {
            return null;
        }
//        return JsonUtil.str2Obj(string, getClassType());
        return JsonUtil.str2Obj(string, type);
    }

    private Class<T> getClassType() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
//        if (genericSuperclass instanceof ParameterizedType) {
//            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
//            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
//            for (Type actualTypeArgument : actualTypeArguments) {
//                System.out.println(actualTypeArgument);
//            }
//        }
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Type actualTypeArgument = actualTypeArguments[0];
        return (Class<T>) actualTypeArgument;
    }

}
