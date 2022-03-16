package org.juricamigac.slingspecificannotations.dataprocessor.annotationsprocessor;

import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;

public class RequestedDateMetadataProviderAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

    @Override
    public InjectionStrategy getInjectionStrategy() {
        return InjectionStrategy.DEFAULT;
    }

    @Override
    public Boolean isOptional() {
        return Boolean.FALSE;
    }

}
