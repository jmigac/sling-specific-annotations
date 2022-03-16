package org.juricamigac.slingspecificannotations.dataprocessor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.juricamigac.slingspecificannotations.annotations.LocalDateValueMapValue;
import org.juricamigac.slingspecificannotations.dataprocessor.annotationsprocessor.RequestedDateMetadataProviderAnnotationProcessor;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.osgi.framework.Constants.SERVICE_RANKING;

@Slf4j
@Component(
        immediate = true,
        service = {Injector.class, StaticInjectAnnotationProcessorFactory.class},
        property = {SERVICE_RANKING + ":Integer=7000"}
)
public class LocalDateProcessor implements Injector, StaticInjectAnnotationProcessorFactory {

    private static final String INJECTION_NAME = "local-date-provider-injector";

    @Override
    public String getName() {
        return INJECTION_NAME;
    }

    @Override
    public Object getValue(Object adaptable, String fieldName, Type type, AnnotatedElement annotatedElement, DisposalCallbackRegistry disposalCallbackRegistry) {
        if (adaptable instanceof SlingHttpServletRequest && annotatedElement.isAnnotationPresent(LocalDateValueMapValue.class)) {
            return this.getLocalDateFromAdaptableRequest(adaptable, annotatedElement, fieldName);
        } else if (adaptable instanceof Resource && annotatedElement.isAnnotationPresent(LocalDateValueMapValue.class)) {
            return this.getLocalDateFromAdaptableResource(adaptable, annotatedElement, fieldName);
        }
        return null;
    }

    private LocalDate getLocalDateFromAdaptableResource(Object adaptable, AnnotatedElement annotatedElement, String fieldName) {
        try {
            final LocalDateValueMapValue annotation = annotatedElement.getAnnotation(LocalDateValueMapValue.class);
            final Resource resource = (Resource) adaptable;
            return this.getLocalDateValueFromValueMap(annotation, resource, fieldName);
        } catch (final Exception e) {
            log.error("Error during fetching LocalDate from value map", e);
        }
        return null;
    }

    private LocalDate getLocalDateFromAdaptableRequest(final Object adaptable, AnnotatedElement annotatedElement, final String fieldName) {
        try {
            final LocalDateValueMapValue annotation = annotatedElement.getAnnotation(LocalDateValueMapValue.class);
            final Resource resource = ((SlingHttpServletRequest) adaptable).getResource();
            return this.getLocalDateValueFromValueMap(annotation, resource, fieldName);
        } catch (final Exception e) {
            log.error("Error during fetching LocalDate from value map", e);
        }
        return null;
    }

    private LocalDate getLocalDateValueFromValueMap(final LocalDateValueMapValue annotation, final Resource resource, final String fieldName) {
        if (StringUtils.isNotEmpty(annotation.value()) && resource.hasChildren()) {
            final Resource childResource = resource.getChild(annotation.value());
            if (childResource != null) {
                final Date date = childResource.getValueMap().get(this.getFieldName(annotation, fieldName), Date.class);
                if (date != null) {
                    return this.convertDateToLocalDate(date);
                }
            }
        } else {
            final Date date = resource.getValueMap().get(this.getFieldName(annotation, fieldName), Date.class);
            if (date != null) {
                return this.convertDateToLocalDate(date);
            }
        }
        return null;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement annotatedElement) {
        LocalDateValueMapValue annotation = annotatedElement.getAnnotation(LocalDateValueMapValue.class);
        if(annotation != null) {
            return new RequestedDateMetadataProviderAnnotationProcessor();
        }
        return null;
    }

    private String getFieldName(final LocalDateValueMapValue annotation, final String fieldName) {
        return StringUtils.isNotEmpty(annotation.name()) ? annotation.name() : fieldName;
    }

    private LocalDate convertDateToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
