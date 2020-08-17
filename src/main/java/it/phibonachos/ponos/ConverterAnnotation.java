package it.phibonachos.ponos;

import it.phibonachos.ponos.converters.Converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConverterAnnotation {
    Class<? extends Converter> with();
}
