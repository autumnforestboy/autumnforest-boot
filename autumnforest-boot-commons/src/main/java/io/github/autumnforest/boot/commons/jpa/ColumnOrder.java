package io.github.autumnforest.boot.commons.jpa;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ColumnOrder {
    int value();
}
