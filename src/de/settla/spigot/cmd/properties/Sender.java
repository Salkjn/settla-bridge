package de.settla.spigot.cmd.properties;

import de.settla.spigot.cmd.SenderType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(ElementType.TYPE)
public @interface Sender {

    SenderType value() default SenderType.ANY;

}
