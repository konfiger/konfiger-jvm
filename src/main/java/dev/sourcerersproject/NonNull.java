/*
 * The MIT License
 *
 * Copyright 2019 Adewale Azeez <azeezadewale98@gmail.com>.
 *
 */
package dev.sourcerersproject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Adewale Azeez <azeezadewale98@gmail.com>
 */
@Retention(value = RetentionPolicy.CLASS)
@Target(value = {ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface NonNull {
    
}
