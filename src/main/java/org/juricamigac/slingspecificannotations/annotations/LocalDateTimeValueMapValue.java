package org.juricamigac.slingspecificannotations.annotations;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@InjectAnnotation
@Source("local-date-time-provider-injector")
public @interface LocalDateTimeValueMapValue {

    String value() default StringUtils.EMPTY;
    String name() default StringUtils.EMPTY;

}
