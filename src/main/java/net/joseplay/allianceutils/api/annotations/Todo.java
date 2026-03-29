package net.joseplay.allianceutils.api.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Documented
@Target({ElementType.METHOD, ElementType.PACKAGE, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface Todo {
    String value() default "";
    String priority() default "MEDIUM";
}
