package io.github.autumnforest.boot.commons.web;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ColumnOrder {
    int value();
}
