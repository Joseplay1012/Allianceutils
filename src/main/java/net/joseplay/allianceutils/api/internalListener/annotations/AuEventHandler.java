package net.joseplay.allianceutils.api.internalListener.annotations;

import net.joseplay.allianceutils.api.internalListener.AuEventpriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuEventHandler {
    AuEventpriority priority() default AuEventpriority.NORMAL;
}
