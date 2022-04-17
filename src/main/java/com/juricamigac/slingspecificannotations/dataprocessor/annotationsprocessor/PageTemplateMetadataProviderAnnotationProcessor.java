package com.juricamigac.slingspecificannotations.dataprocessor.annotationsprocessor;

import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor2;

public class PageTemplateMetadataProviderAnnotationProcessor extends AbstractInjectAnnotationProcessor2 {

    @Override
    public InjectionStrategy getInjectionStrategy() {
        return InjectionStrategy.OPTIONAL;
    }

    @Override
    public Boolean isOptional() {
        return Boolean.TRUE;
    }

}
