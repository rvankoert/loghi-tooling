package nl.knaw.huc.di.images.layoutds;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicAnnotation {
    String label();

    String uuid();

    boolean readOnly();
}
