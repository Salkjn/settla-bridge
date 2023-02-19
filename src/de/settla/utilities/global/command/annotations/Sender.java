package de.settla.utilities.global.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.TYPE)
public @interface Sender {

    SenderType sender() default SenderType.ANY;

}
