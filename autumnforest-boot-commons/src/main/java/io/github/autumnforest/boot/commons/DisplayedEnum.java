package io.github.autumnforest.boot.commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * 通用枚举接口，需要两个字段： int：value，  String：label
 */
public interface DisplayedEnum {
    String DEFAULT_VALUE_NAME = "value";

    String DEFAULT_LABEL_NAME = "label";

    /**
     * json 反序列化入口
     *
     * @param status value值
     * @return 枚举
     * @throws IllegalAccessException e
     */
    @JsonCreator
    default DisplayedEnum parse(int status) throws IllegalAccessException {
        DisplayedEnum[] objs = this.getClass().getEnumConstants();
        for (DisplayedEnum em : objs) {
            if (em.getValue() == status) {
                return em;
            }
        }
        return null;
    }

    /**
     * json序列化值, 用于反射获取枚举的value字段值
     *
     * @return value字段值
     * @throws IllegalAccessException e
     */
    @JsonValue
    default Integer getValue() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(this.getClass(), DEFAULT_VALUE_NAME);
        if (field == null) {
            return null;
        }
        field.setAccessible(true);
        return Integer.parseInt(field.get(this).toString());
    }

    /**
     * 用于反射获取枚举的label字段值
     *
     * @return label字段值
     * @throws IllegalAccessException e
     */
    default String getLabel() throws IllegalAccessException {
        Field field = ReflectionUtils.findField(this.getClass(), DEFAULT_LABEL_NAME);
        if (field == null) {
            return null;
        }
        field.setAccessible(true);
        return field.get(this).toString();
    }

    static <T extends Enum<T>> T valueOfEnum(Class<T> enumClass, Integer value) throws IllegalAccessException {
        if (value == null) {
            throw new IllegalArgumentException("DisplayedEnum value should not be null");
        }
        if (enumClass.isAssignableFrom(DisplayedEnum.class)) {
            throw new IllegalArgumentException("illegal DisplayedEnum type");
        }
        T[] enums = enumClass.getEnumConstants();
        for (T t : enums) {
            DisplayedEnum displayedEnum = (DisplayedEnum) t;
            if (displayedEnum.getValue().equals(value)) {
                return (T) displayedEnum;
            }
        }
        throw new IllegalArgumentException("cannot parse integer: " + value + " to " + enumClass.getName());
    }
}
